#!/bin/bash
set -eo pipefail
mvn clean package -DskipTests
mkdir build
mv target/s3-java-1.0-SNAPSHOT.jar build/s3-java-lib.zip
