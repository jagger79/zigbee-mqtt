package cz.rj.zigbee.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

@Slf4j
@RequiredArgsConstructor
public class SubscriberCallback implements MqttCallback {
    private final MqttClient client;

    int m_numReceived = 0;
    long m_startTime;
    boolean firstMessageReceived = false;

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        log.info("Disconnected");
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        log.info("MQTTErrorOccurred");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //println "Received message ${new String(message.payload)} on topic [${topic}]"
        if (!firstMessageReceived) {
            m_startTime = System.currentTimeMillis();
            firstMessageReceived = true;
        }
        if (topic.equals("/exit")) {
            long stopTime = System.currentTimeMillis();
            long spentTime = stopTime - m_startTime;
            log.info("/exit received");
            log.info("subscriber disconnected, received {} messages in {} ms", m_numReceived, spentTime);
        } else {
            log.info("received,messageId={},message={},topic={}", message.getId(), message, topic);
            //message.clearPayload();
            //message.setRetained(true);
            m_numReceived++;
            //client.messageArrivedComplete(message.getId(), message.getQos());
            IMqttToken[] tokens = client.getPendingTokens();
            log.info("tokens={}", tokens);
        }
    }

    @Override
    public void deliveryComplete(IMqttToken token) {
        //To change body of implemented methods use File | Settings | File Templates.
        log.info("deliveryComplete");
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("connectComplete");
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {
        log.info("authPacketArrived");
    }
}