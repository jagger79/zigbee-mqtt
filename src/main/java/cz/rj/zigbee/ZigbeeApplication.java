package cz.rj.zigbee;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @see https://moquette-io.github.io/moquette/documentation.html
 */
@SpringBootApplication
public class ZigbeeApplication {
    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        // Root logger - must be lowest to be sent to SLF4J
        Logger.getLogger("").setLevel(Level.FINEST);

        SpringApplication.run(ZigbeeApplication.class, args);
    }
}
