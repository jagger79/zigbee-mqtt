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
curr_dir=`pwd`
PACK_FILE="app.tgz"
HOST="rkad@smb"
#HOST="rkad@192.168.88.194"

export OPTS HOST PACK_FILE

rm -rf build/

# JAVA things for performance boost
echo unpacking dependencies...
java -Djarmode=tools -jar target/*-uber.jar extract --destination build

echo packing...
tar -czf ${PACK_FILE} build/

echo copy to server...
scp -P8080 ${PACK_FILE} ${HOST}:/srv
rm -f ${PACK_FILE}

echo server: unpacking...
#export CMD="; (test -a ${artifactId}/bin/manual-stop.sh && ${artifactId}/bin/manual-stop.sh) || echo $?"
export CMD="cd /srv; (tar -xf ${PACK_FILE}) || echo $?"
set -x
ssh -p8080 -o StrictHostKeyChecking=no ${HOST} "cd /srv; (tar -xf /srv/${PACK_FILE}) || echo $?"

#echo launching...
#cd build
#java $OPTS -XX:SharedArchiveFile=application.jsa -jar *.jar
#cd $curr_dir
