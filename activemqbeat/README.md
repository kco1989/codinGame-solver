# Activemqbeat

Beat to monitor an ActiveMQ instance.


## How to run

The easiest way to launch activemqbeat is to run it in a Docker container:

```
docker run codingame/activemqbeat
```


## Configuring activemqbeat

To override the default configuration, just link yours to `/etc/activemqbeat/activemqbeat.yml`:

```
docker run -d \
  -v /directory/where/your/config/file/is/:/etc/activemqbeat \
  --name activemqbeat \
  codingame/activemqbeat
```

Otherwise, you could create your own image with your custom configuration with a Dockerfile like:

```Dockerfile
FROM codingame/activemqbeat

COPY activemqbeat.yml /etc/activemqbeat/activemqbeat.yml
```


## Exported fields

Example output:

```json
{
  "activemq": {
    "queue": {
      "name": "my-queue",
      "size": 4,
      "consumer_count": 3,
      "dequeue_count": 12,
      "enqueue_count": 12
    }
  }
}
```

To get a detailed list of all generated fields, please read the [fields documentation page](docs/fields.asciidoc).


## Contributing to the project

See [contributing instructions](CONTRIBUTING.md) to set up the project and build it on your machine.
