#!/bin/bash
curl -s https://api.github.com/repos/openshift/source-to-image/releases/latest \
  | grep browser_download_url \
  | grep linux-amd64 \
  | cut -d '"' -f 4 \
  | wget -qi -
tar xvf source-to-image*.gz
rm source-to-image*.gz
