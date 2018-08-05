// Config is put into a different package to prevent cyclic imports in case
// it is needed in several locations

package config

import "time"

type Config struct {
	Period   time.Duration `config:"period"`
	Url      string        `config:"url"`
	Username string        `config:"username"`
	Password string        `config:"password"`
}

var DefaultConfig = Config{
	Period:   10 * time.Second,
	Url:      "http://localhost:8161",
	Username: "admin",
	Password: "admin",
}
