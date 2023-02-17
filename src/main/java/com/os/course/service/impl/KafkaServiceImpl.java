package com.os.course.service.impl;

import com.os.course.service.KafkaService;
import com.os.course.util.Producer;
import com.os.course.util.TopicName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class KafkaServiceImpl implements KafkaService {
    private final Producer producer;
    private final TopicName topicName;

    public KafkaServiceImpl(Producer producer, TopicName topicName) {
        this.producer = producer;
        this.topicName = topicName;
    }

    @Override
    public void sendFile(MultipartFile multipartFile) {
        producer.sendMessage(multipartFile, topicName.getUploadingTopicName());
    }
}
