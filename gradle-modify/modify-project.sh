#!/bin/bash

ARTIFACT_VERSION="2.1.1"

if [[ $1 == "" ]]; then
    echo "arg1 - the path to the project, where high-level pom.xml is"
    echo "arg2 - (Optional) Custom version for the artifact (e.g., 1.1.0, 2.1). Default is $ARTIFACT_VERSION"
    exit
fi


if [[ ! $2 == "" ]]; then
    ARTIFACT_VERSION=$2
fi

crnt=`pwd`
working_dir=`dirname $0`
project_path=$1

cd ${project_path}
project_path=`pwd`
cd - > /dev/null

cd ${working_dir}

subProjects=`find ${project_path}  -mindepth 2 -name build.gradle | wc -l`

find ${project_path}  -maxdepth 1 -name build.gradle | python3 modify_gradle.py ${ARTIFACT_VERSION} ${subProjects}

cd ${crnt}