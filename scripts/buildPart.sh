#!/bin/bash

cd ../ &&  mvn -pl nondex-instrumentation -pl nondex-common -am clean install -Dcheckstyle.skip=true -DskipTests=true -Dmaven.javadoc.skip=true