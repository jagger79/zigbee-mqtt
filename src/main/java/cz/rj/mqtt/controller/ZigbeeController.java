package cz.rj.mqtt.controller;

import cz.rj.mqtt.service.PublisherService;
import cz.rj.mqtt.tests.TestProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ZigbeeController {
    private final PublisherService service;
    private final TestProfile test;

    @RequestMapping("test")
    public void test() throws Exception {
        //service.publish2();
        service.publish();
        test.execute("");
    }

    @RequestMapping("exit")
    public void exit() throws Exception {
        service.publishExit();
    }
}
