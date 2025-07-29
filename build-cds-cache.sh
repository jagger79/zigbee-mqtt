#!/bin/bash

OPTS=""
OPTS="$OPTS -Dserver.port=8081"
OPTS="$OPTS -Dzigbee.publisher.enabled=false"
OPTS="$OPTS -Dzigbee.subscriber.enabled=false"
#-Dlogging.level.cz=trace
#-Dlogging.level.io.moquette=trace
#-Dlogging.level.io.moquette.broker=info
#-Dlogging1.level.org.eclipse=trace
#-Dlogging.level.org.eclipse.paho.mqttv5.client=trace
#-Dlogging.level.org.eclipse.paho.mqttv5.client.internal.CommsReceiver=info
#-Dlogging.level.BufferManagement=trace
export OPTS
curr_dir=`pwd`

rm -rf build/

# JAVA things for performance boost
echo unpacking dependencies...
java -Djarmode=tools -jar target/*-uber.jar extract --destination build

cd build
echo use cds cache...
java $OPTS \
    -XX:ArchiveClassesAtExit=application.jsa \
    -Dspring.context.exit=onRefresh \
    -jar *-uber.jar
# END JAVA things
cd $curr_dir

echo packing...
tar -czf app.tgz build/

echo copy to server...
scp -P8080 app.tgz rkad@devel.osslite.cz:/apps/mqtt/
rm -f app.tgz

echo server: unpacking...
#export CMD="source ~/.bash_profile; (test -a ${artifactId}/bin/manual-stop.sh && ${artifactId}/bin/manual-stop.sh) || echo $?"
#ssh -o StrictHostKeyChecking=no rkad@smb "$CMD"

echo launching...
cd build
java $OPTS -XX:SharedArchiveFile=application.jsa -jar *.jar
cd $curr_dir
