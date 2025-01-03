package cz.rj.zigbee.config;

import cz.rj.zigbee.ZigbeeProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@RequiredArgsConstructor
@Slf4j
class InitConfiguration {
    private final ZigbeeProperties props;

    @PostConstruct
    public void initialize() {
        log.info("publisher,{}", props.getPublisher().getEnabled() ? "started" : "stopped");
        log.info("subscriber,{}", props.getSubscriber().getEnabled() ? "started" : "stopped");
    }
}