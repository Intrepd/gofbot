#!/bin/bash
#

#LIB="lib/"
VM_ARGS="-Djava.util.logging.config.file=log.properties -Djava.util.logging.manager=java.util.logging.LogManager"
MAIN="com.guildoffools.bot.GoFBot"
#CLASSPATH=$(find "$LIB" -name '*.jar' -printf '%p:' | sed 's/:$//')
CLASSPATH="GoFBot.jar"
JAR="GoFBot.jar"

/usr/bin/java $VM_ARGS -cp $CLASSPATH $MAIN

EXITCODE=$?
echo Exiting with code $EXITCODE

exit $EXITCODE
