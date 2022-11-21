#!/bin/bash
dir=$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)
echo "$dir"
chmod a+x $dir/jdk/bin/*
export JAVA_HOME=$dir/jdk
export PATH=$JAVA_HOME/bin:$PATH
java -jar -Djava.library.path=lib/native presenter-swing.jar
