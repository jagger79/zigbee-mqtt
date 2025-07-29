package cz.rj.mqtt;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties("mqtt")
@Validated
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class MqttProperties implements InitializingBean {
    /**
     * Broker configuration
     */
    @NestedConfigurationProperty
    @Valid
    final BrokerConfig broker = new BrokerConfig();
    /**
     * Publisher configuration
     */
    @NestedConfigurationProperty
    @Valid
    final PublisherConfig publisher = new PublisherConfig();
    /**
     * Subscriber configuration
     */
    @NestedConfigurationProperty
    @Valid
    final SubscriberConfig subscriber = new SubscriberConfig();

    @PostConstruct
    public void initialize() {
        log.info("Initializing MQTT properties,broker={}", broker);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initializing MQTT properties,{}", this);
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BrokerConfig {
        String protocol = "tcp";
        String host = "localhost";
        Integer port = 1883;
        @NotBlank
        String dataDir;
        @NotNull
        Integer maximumMessageSize = 1024 * 1024 * 1024;
        Boolean allowAnonymous = true;

        public String getServerUri() {
            // url = "ssl://localhost:8883";
            return getProtocol() + "://" + getHost() + ":" + getPort();
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PublisherConfig {
        @NotNull
        Boolean enabled = true;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SubscriberConfig {
        @NotNull
        Boolean enabled = true;
    }
}
