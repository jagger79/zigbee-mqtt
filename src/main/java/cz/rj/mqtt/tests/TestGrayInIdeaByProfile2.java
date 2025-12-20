package cz.rj.mqtt.tests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Profile("test2")
@Component
public class TestGrayInIdeaByProfile2 implements TestProfile {
    @Override
    public Object execute(Object context) {
        return null;
    }
}