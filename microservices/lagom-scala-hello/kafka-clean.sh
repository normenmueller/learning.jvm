#!/bin/sh
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
KAFKA="target/lagom-dynamic-projects/lagom-internal-meta-project-kafka"
rm -rf ${DIR}/${KAFKA}
