#!/bin/bash

#DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd `dirname $0`
cd ../ && mvn clean install -Dcheckstyle.skip=true -DskipTests=true -Dmaven.javadoc.skip=true