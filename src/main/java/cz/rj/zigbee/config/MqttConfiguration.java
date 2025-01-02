package cz.rj.zigbee.config;

import cz.rj.zigbee.ZigbeeProperties;
import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.ResourceLoaderConfig;
import io.moquette.broker.security.AcceptAllAuthenticator;
import io.moquette.broker.security.PermitAllAuthorizatorPolicy;
import io.moquette.interception.InterceptHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.File;
import java.util.List;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@RequiredArgsConstructor
@Slf4j
class MqttConfiguration {
    private final ZigbeeProperties props;
    private final ConfigurableEnvironment env;

    @Bean(destroyMethod = "stopServer")
    Server mqttBroker() throws Exception {
        var tmpFile = File.createTempFile("zigbee-mqtt", ".properties");
        log.debug("temporary,{},deleted={}", tmpFile, tmpFile.delete());
        final File persistenceStoreDir = new File(tmpFile.getParentFile(), "mqtt/");

        InterceptHandler callback = new PublisherListener();
        List<InterceptHandler> callbacks = List.of(callback);

        final IConfig config = new ResourceLoaderConfig(new ClasspathResourceLoader());
        //config.setProperty(IConfig.DATA_PATH_PROPERTY_NAME, persistenceStoreDir.getAbsolutePath());
        config.setProperty(IConfig.DATA_PATH_PROPERTY_NAME, props.getBroker().getDataDir());

        final Server mqttBroker = new Server();
        log.info("starting,{}", mqttBroker);
        mqttBroker.startServer(config,
                callbacks,
                //null,
                null,
                new AcceptAllAuthenticator(),
                new PermitAllAuthorizatorPolicy());

        return mqttBroker;
    }

    @Bean(destroyMethod = "close")
    MqttClient publisher(Server mqttBroker) throws Exception {
        var dataStore1 = new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir") + "/mqtt_client");
        var dataStore = new MemoryPersistence();

        MqttClient client = new MqttClient(props.getBroker().getServerUri(), "publisher", dataStore);
        client.connect();
        return client;
    }

    @Bean(destroyMethod = "close")
    MqttClient subscriber(Server mqttBroker) throws Exception {
        var dataStore = new MemoryPersistence();
        MqttClient subClient = new MqttClient(props.getBroker().getServerUri(), "subscriber", dataStore, null);

        SubscriberCallback callback = new SubscriberCallback(subClient);
        subClient.setCallback(callback);
        subClient.connect();

        IMqttToken token = subClient.subscribe("log", 0);
        subClient.subscribe("/exit", 0);
        return subClient;
    }
}