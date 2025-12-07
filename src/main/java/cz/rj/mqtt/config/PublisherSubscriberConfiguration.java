package cz.rj.mqtt.config;

import cz.rj.mqtt.MqttProperties;
import cz.rj.mqtt.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@RequiredArgsConstructor
@Slf4j
class PublisherSubscriberConfiguration {
    @Bean(destroyMethod = "close")
    MqttClient publisher(final MqttProperties props) throws Exception {
        var dataStore1 = new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir") + "/mqtt_client");
        var dataStore = new MemoryPersistence();

        var client = new MqttClient(props.getBroker().getServerUri(),
                "publisher",
                dataStore);
        if (!props.getPublisher().getEnabled()) {
            return client;
        }
        client.connect();
        return client;
    }

    @Bean(destroyMethod = "close")
    MqttClient subscriber(final MqttProperties props) throws Exception {
        var dataStore = new MemoryPersistence();
        var subClient = new MqttClient(props.getBroker().getServerUri(),
                "subscriber",
                dataStore,
                null);
        if (!props.getSubscriber().getEnabled()) {
            return subClient;
        }

        var callback = new SubscriberService(subClient);
        subClient.setCallback(callback);
        subClient.connect();

        subClient.subscribe("log", 0);
        subClient.subscribe("/exit", 0);
        return subClient;
    }
}