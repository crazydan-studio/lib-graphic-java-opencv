#!/bin/bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd -P)"

DCR_IMAGE_NAME="opencv-builder-base"

docker build \
    -f "${DIR}/Dockerfile" \
    -t "${DCR_IMAGE_NAME}" \
    "${DIR}"
