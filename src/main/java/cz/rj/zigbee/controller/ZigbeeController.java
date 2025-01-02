package cz.rj.zigbee.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ZigbeeController {
    @RequestMapping("test")
    public Object getQuestionnaire(@RequestBody Map<String, String> in) {
        log.info("get questionnaire,in={}", in);

        return Map.of("questionnaire", LocalDate.parse("2023-12-12"));
    }
}
