package com.os.course.service.impl;

import com.os.course.service.KafkaService;
import com.os.course.util.Producer;
import com.os.course.util.MicroserviceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class KafkaServiceImpl implements KafkaService {
    private final Producer producer;
    private final MicroserviceProperties microserviceProperties;

    public KafkaServiceImpl(Producer producer, MicroserviceProperties microserviceProperties) {
        this.producer = producer;
        this.microserviceProperties = microserviceProperties;
    }

    @Override
    public void sendFile(MultipartFile multipartFile) {
        producer.sendMessage(multipartFile, microserviceProperties.getUploadingTopicName());
    }
}
