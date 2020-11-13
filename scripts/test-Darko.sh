#!/bin/bash

jdk11
cd NonDex && mvn -pl nondex-instrumentation -pl nondex-common -am clean install -Dcheckstyle.skip=true -DskipTests=true -Dmaven.javadoc.skip=true
cd ../marinov-TestsForNonDex
javac "$1"
java "$1"