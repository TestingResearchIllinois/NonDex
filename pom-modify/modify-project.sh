#!/bin/bash

if [[ $1 == "" ]]; then
    echo "arg1 - the path to the project, where high-level pom.xml is"
    exit
fi

# Check if python is installed
type -P python3 >/dev/null 2>&1 && echo "You need python3 to run this script but you have it installed."

# Set variables
project_path=$1
current=`pwd`
working_dir=`dirname $0`

# Run script.py
cd ${project_path}
project_path=`pwd`
cd - > /dev/null

cd ${working_dir}

python3 pom-modify.py ${project_path}
echo "Plugins have been installed in the targeted project."
