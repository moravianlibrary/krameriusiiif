[![build status](https://github.com/moravianlibrary/krameriusiiif/workflows/Java%20CI/badge.svg) ](https://github.com/moravianlibrary/krameriusiiif/)
[![Maintainability](https://api.codeclimate.com/v1/badges/f01ddd1bff90fc91a3fd/maintainability)](https://codeclimate.com/github/moravianlibrary/krameriusiiif/maintainability)
[![Coverage Status](https://coveralls.io/repos/github/moravianlibrary/krameriusiiif/badge.svg?branch=master)](https://coveralls.io/github/moravianlibrary/krameriusiiif?branch=master)

# krameriusiiif
Spring Boot based IIIF manifest generator (V2.1) for Kramerius<p>
https://iiif.io/api/presentation/2.1/

Docker image
========================================

This repository contains the source for creating Docker image using
[source-to-image](https://github.com/openshift/source-to-image).
The resulting image can be run using [Docker](http://docker.io).

Installation
---------------
To create application image run:
```
$ git clone https://github.com/moravianlibrary/krameriusiiif.git
$ cd kramerius/docker-s2i
$ ./build.sh
```

Run
---------------
To run S2I created docker image:
```
$ ./start-docker.sh
# this is shortcut to docker-compose up
```
or
```
$ docker-compose up -d
# to start in detached mode
# port mapping 8080 is preconfigured in docker-compose.yml
```

**Accessing the application:**
```
http://localhost:8080/iiif/
```