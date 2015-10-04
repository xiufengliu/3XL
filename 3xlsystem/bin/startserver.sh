#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

cd $bin
cd ../

XL_HOME=/home/xiliu/workspace/3xlsystem

cp /home/xiliu/workspace/3xlsystem/build/dist/3xlsystem.jar lib

for f in ${XL_HOME}/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

echo ${CLASSPATH}

java -cp ${CLASSPATH} xlsystem.Server --port 9000  --timing
