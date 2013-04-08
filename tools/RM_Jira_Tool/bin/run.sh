#!/bin/bash

CURRENT_DIR=`pwd`

CLASSPATH="
rm_jira_tool.jar:\
lib/log4j.jar:\
lib/commons-codec.jar:\
lib/commons-logging.jar:\
lib/guava.jar:\
lib/httpclient.jar:\
lib/httpcore.jar:\
lib/jackson-core-asl.jar:\
lib/jackson-mapper-asl.jar"

java -Dconfig.file=conf/application.properties -Dlog4j.configuration=file:$CURRENT_DIR/conf/log4j.properties -cp $CLASSPATH com.devexperts.gft.DecisionMaker