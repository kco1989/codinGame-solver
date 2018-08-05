package main

import (
	"os"

	"github.com/elastic/beats/libbeat/beat"

	"github.com/codingame/activemqbeat/beater"
)

func main() {
	err := beat.Run("activemqbeat", "", beater.New)
	if err != nil {
		os.Exit(1)
	}
}
