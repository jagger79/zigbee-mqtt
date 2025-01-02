package cz.rj.zigbee;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("zigbee")
@Validated
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZigbeeProperties {
    @NestedConfigurationProperty
    private final BrokerConfig broker = new BrokerConfig();

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BrokerConfig {
        String protocol = "tcp";
        String host = "localhost";
        Integer port = 1883;
        @NotBlank
        String dataDir;

        public String getServerUri() {
            // url = "ssl://localhost:8883";
            return getProtocol() + "://" + getHost() + ":" + getPort();
        }
    }
}
