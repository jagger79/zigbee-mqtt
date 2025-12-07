package cz.rj.mqtt.config;

import cz.rj.mqtt.MqttProperties;
import cz.rj.mqtt.service.BrokerPublisherListener;
import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.ResourceLoaderConfig;
import io.moquette.broker.security.AcceptAllAuthenticator;
import io.moquette.broker.security.PermitAllAuthorizatorPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.context.event.EventListener;

import java.nio.file.Path;
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
        var dataDirProp = Path.of(props.getBroker().getDataDir()).normalize();
        var canWrite = dataDirProp.toFile().canWrite();
        var dataDir = dataDirProp.toFile().getAbsolutePath();
        log.info("MQTT broker,writable={},datadir={}", canWrite, dataDir);

        final var config = new ResourceLoaderConfig(new ClasspathResourceLoader());
        config.setProperty(IConfig.DATA_PATH_PROPERTY_NAME, dataDir);
        config.setProperty(IConfig.PERSISTENCE_ENABLED_PROPERTY_NAME, "true");
        return config;
    }

    @Bean(destroyMethod = "stopServer")
    public Server mqttBroker(final IConfig config) throws Exception {
        final var mqttBroker = new Server();

        log.info("starting,{}", mqttBroker);
        mqttBroker.startServer(config,
                List.of(new BrokerPublisherListener()),
                null,
                new AcceptAllAuthenticator(),
                new PermitAllAuthorizatorPolicy());
        log.info("started,{}", mqttBroker);

        return mqttBroker;
    }
}