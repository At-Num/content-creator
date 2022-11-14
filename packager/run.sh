#!/bin/bash
dir=$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)
echo "$dir"

export JAVA_HOME="jdk"
export PATH=$JAVA_HOME/bin:$PATH
java -jar -Djava.library.path=lib/native presenter-swing.jar
