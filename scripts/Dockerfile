FROM openjdk:8

COPY debian11/source.list /etc/apt/source.list

RUN apt-get update \
    && apt-get -y install \
        g++ cmake libavcodec-dev libavformat-dev \
        libswscale-dev libopenjp2-7-dev

RUN mkdir -p /workspace/source /workspace/build \
    && chmod go+rw /opt /workspace /workspace/build

VOLUME [ "/workspace/source" ]

WORKDIR /workspace
