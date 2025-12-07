package cz.rj.mqtt.service;

import io.moquette.broker.Server;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublisherService {
    private final Server mqttBroker;
    private final MqttClient publisher;

    private Integer id;

    @PostConstruct
    void init() {
        id = (int) (Math.random() * 21564321 + 1);
    }

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
        final byte[] msg = ("Hello world eclipse " + id).getBytes();
        var message = new MqttMessage(msg, MqttQoS.EXACTLY_ONCE.value(), true, null);
        var props = new MqttProperties();
        props.setAssignedClientIdentifier("publisher-" + id);
        message.setProperties(props);

        log.info("publish,topic=log,message={}", message);
        publisher.publish("log", message);
    }

    public void publishExit() throws Exception {
        final byte[] msg = "EXIT eclipse!!".getBytes();
        var message = new MqttMessage(msg, MqttQoS.AT_MOST_ONCE.value(), false, null);
        var props = new MqttProperties();
        props.setAssignedClientIdentifier("publisher-" + id);
        message.setProperties(props);

        log.info("publish,topic=/exit,message={}", message);
        publisher.publish("/exit", message);
    }
}
