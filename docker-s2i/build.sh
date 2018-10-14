#!/bin/bash
docker build -t krameriusiiif-builder .
s2i build https://github.com/MartinRumanek/krameriusiiif.git krameriusiiif-builder martinrumanek/krameriusiiif
