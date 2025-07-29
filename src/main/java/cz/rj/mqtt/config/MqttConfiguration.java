package cz.rj.mqtt.config;

import cz.rj.mqtt.MqttProperties;
import cz.rj.mqtt.service.PublisherListener;
import cz.rj.mqtt.service.SubscriberCallback;
import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.ResourceLoaderConfig;
import io.moquette.broker.security.AcceptAllAuthenticator;
import io.moquette.broker.security.PermitAllAuthorizatorPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.context.event.EventListener;

import java.util.List;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@RequiredArgsConstructor
@Slf4j
class MqttConfiguration {
    final MqttProperties props;

    @EventListener(ApplicationStartedEvent.class)
    public void initialize(ApplicationStartedEvent event) {
        log.info("publisher,{}", props.getPublisher().getEnabled() ? "started" : "stopped");
        log.info("subscriber,{}", props.getSubscriber().getEnabled() ? "started" : "stopped");
    }

    @Bean
    IConfig getConfig(MqttProperties props) {
        final var config = new ResourceLoaderConfig(new ClasspathResourceLoader());
        config.setProperty(IConfig.DATA_PATH_PROPERTY_NAME, props.getBroker().getDataDir());
        config.setProperty(IConfig.NETTY_MAX_BYTES_PROPERTY_NAME, props.getBroker().getMaximumMessageSize() + "");
        config.setProperty(IConfig.ALLOW_ANONYMOUS_PROPERTY_NAME, props.getBroker().getAllowAnonymous() + "");
        config.setProperty(IConfig.PERSISTENCE_ENABLED_PROPERTY_NAME, "true");
        return config;
    }

    @Bean(destroyMethod = "stopServer")
    public Server mqttBroker(final IConfig config) throws Exception {
        final var mqttBroker = new Server();
        log.info("starting,{}", mqttBroker);
        mqttBroker.startServer(config,
                List.of(new PublisherListener()),
                //null,
                null,
                new AcceptAllAuthenticator(),
                new PermitAllAuthorizatorPolicy());
        log.info("started,{}", mqttBroker);

        return mqttBroker;
    }

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

        var callback = new SubscriberCallback(subClient);
        subClient.setCallback(callback);
        subClient.connect();

        IMqttToken token = subClient.subscribe("log", 0);
        subClient.subscribe("/exit", 0);
        return subClient;
    }
}