package com.os.course.controller;

import com.os.course.service.KafkaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/processor")
public class ProcessorController {

    private final KafkaService kafkaService;

    public ProcessorController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String uploadFile() {
        return "hello world";
    }
}
