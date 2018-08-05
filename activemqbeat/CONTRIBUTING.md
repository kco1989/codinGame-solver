# Contributing to Activemqbeat

Welcome to Activemqbeat.

This beat has been created thanks to the official [beat developer guide](https://www.elastic.co/guide/en/beats/libbeat/current/new-beat.html).

## Getting Started with Activemqbeat

### Requirements

* [Golang](https://golang.org/dl/) >= 1.7
* [Glide](https://glide.sh/)
* Virtualenv

### Build

Ensure that this folder is at the following location: `${GOPATH}/src/github.com/codingame/activemqbeat`

To build the binary for Activemqbeat run the command below. This will generate a binary
in the same directory with the name activemqbeat.

```
# Install dependencies and generate config/template files
make setup

# Create the activemqbeat binary
make
```

### Run

To run activemqbeat with debugging output enabled, run:

```
./activemqbeat -c activemqbeat.yml -e -d "*"
```

### Update

Each beat has a template for the mapping in elasticsearch and a documentation for the fields
which is automatically generated based on `_meta/fields.yml` by running the following command:

```
make update
```

### Cleanup

To clean up the build directory and generated artifacts, run:

```
make clean
```

## Packaging

To create an `activemqbeat` Docker image, run:

```
docker build -t activemqbeat .
```

You can run it with:

```
docker run activemqbeat
```

The official Docker images for `codingame/activemqbeat` are built by [Docker Hub](https://hub.docker.com/r/codingame/activemqbeat/).
