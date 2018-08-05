package beater

import (
	"encoding/xml"
	"fmt"
	"io/ioutil"
	"net/http"
	"time"

	"github.com/elastic/beats/libbeat/beat"
	"github.com/elastic/beats/libbeat/common"
	"github.com/elastic/beats/libbeat/logp"
	"github.com/elastic/beats/libbeat/publisher"

	"github.com/codingame/activemqbeat/config"
)

type Activemqbeat struct {
	done   chan struct{}
	config config.Config
	client publisher.Client
}

type ActivemqMetrics struct {
	Queues []Queue `xml:"queue"`
}

type Queue struct {
	Name  string `xml:"name,attr"`
	Stats Stats  `xml:"stats"`
}

type Stats struct {
	Size          int `xml:"size,attr"`
	ConsumerCount int `xml:"consumerCount,attr"`
	EnqueueCount  int `xml:"enqueueCount,attr"`
	DequeueCount  int `xml:"dequeueCount,attr"`
}

func getActivemqMetrics(baseUrl string, username string, password string) (*ActivemqMetrics, error) {
	client := &http.Client{}

	queuesUrl := baseUrl + "/admin/xml/queues.jsp"

	req, _ := http.NewRequest("GET", queuesUrl, nil)

	req.SetBasicAuth(username, password)

	resp, err := client.Do(req)

	if err != nil {
		logp.Err("Error retrieving queues from ActiveMQ", err.Error())
		return nil, err
	}

	body, _ := ioutil.ReadAll(resp.Body)

	activemqMetrics := ActivemqMetrics{}

	err = xml.Unmarshal(body, &activemqMetrics)

	if err != nil {
		logp.Err("Error parsing response body as XML", err.Error())
		return nil, err
	}

	return &activemqMetrics, nil
}

// Creates beater
func New(b *beat.Beat, cfg *common.Config) (beat.Beater, error) {
	config := config.DefaultConfig
	if err := cfg.Unpack(&config); err != nil {
		return nil, fmt.Errorf("Error reading config file: %v", err)
	}

	bt := &Activemqbeat{
		done:   make(chan struct{}),
		config: config,
	}
	return bt, nil
}

func (bt *Activemqbeat) Run(b *beat.Beat) error {
	logp.Info("activemqbeat is running! Hit CTRL-C to stop it.")

	bt.client = b.Publisher.Connect()
	ticker := time.NewTicker(bt.config.Period)
	for {
		select {
		case <-bt.done:
			return nil
		case <-ticker.C:
		}

		activemq, err := getActivemqMetrics(
			bt.config.Url,
			bt.config.Username,
			bt.config.Password,
		)

		if err != nil {
			event := common.MapStr{
				"@timestamp": common.Time(time.Now()),
				"type":       b.Name,
				"error":      err.Error(),
			}
			bt.client.PublishEvent(event)
			logp.Info("Event sent")
			continue
		}

		for _, queue := range activemq.Queues {
			event := common.MapStr{
				"@timestamp": common.Time(time.Now()),
				"type":       b.Name,
				"activemq": common.MapStr{
					"queue": common.MapStr{
						"name":           queue.Name,
						"size":           queue.Stats.Size,
						"consumer_count": queue.Stats.ConsumerCount,
						"enqueue_count":  queue.Stats.EnqueueCount,
						"dequeue_count":  queue.Stats.DequeueCount,
					},
				},
			}

			bt.client.PublishEvent(event)
			logp.Info("Event sent")
		}
	}
}

func (bt *Activemqbeat) Stop() {
	bt.client.Close()
	close(bt.done)
}
