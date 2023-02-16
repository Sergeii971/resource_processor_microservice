package com.os.course.service;

import org.springframework.web.multipart.MultipartFile;

public interface KafkaService {
    void sendFile(MultipartFile multipartFile);
}
