package cz.rj.zigbee.service;

import cz.rj.zigbee.ZigbeeProperties;
import io.moquette.broker.Server;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqttPublisherService {
    private final Server mqttBroker;
    private final MqttClient publisher;
    private final ZigbeeProperties props;

    public void publish2() {
        MqttPublishMessage message = MqttMessageBuilders.publish()
                .topicName("log")
                .retained(true)
                .qos(MqttQoS.EXACTLY_ONCE)
                .payload(Unpooled.copiedBuffer("Hello World netty !!!".getBytes(UTF_8)))
                .build();

        mqttBroker.internalPublish(message, "INTRLPUB");
    }

    public void publish() throws Exception {
        var id = (int) (Math.random() * 21564321 + 1);
        var message = new MqttMessage(("Hello world eclipse " + id).getBytes());
        message.setId(id);
        message.setQos(MqttQoS.EXACTLY_ONCE.value());
        message.setRetained(true);

        publisher.publish("log", message);
    }

    public void publishExit() throws Exception {
        var message = new MqttMessage("EXIT eclipse!!".getBytes());
        message.setQos(MqttQoS.AT_MOST_ONCE.value());
        message.setRetained(false);

        publisher.publish("/exit", message);
    }
}
