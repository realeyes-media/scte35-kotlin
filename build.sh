#!/bin/bash -f

set -x

./gradlew clean
./gradlew build
./gradlew dokka
./gradlew test

