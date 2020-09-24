#!/bin/bash
if [ ! -f ./s2i ]; then
    echo "s2i not found - downloading..."
    sh ./get_s2i.sh
fi
docker build -t krameriusiiif-builder .
./s2i build https://github.com/MartinRumanek/krameriusiiif.git krameriusiiif-builder martinrumanek/krameriusiiif
