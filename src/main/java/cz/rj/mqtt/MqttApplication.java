package cz.rj.mqtt;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/// [Moquette doc](https://moquette-io.github.io/moquette/documentation.html)
///
@SpringBootApplication
public class MqttApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MqttApplication.class).run(args);
    }
}
