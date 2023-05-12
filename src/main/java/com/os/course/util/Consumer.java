package com.os.course.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.course.model.dto.SongMetadataDto;
import com.os.course.model.exception.KafkaProducingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class Consumer {

    private static final String uploadingTopic = "uploadingMp3";
    private final String s3StorageUpdatingTopic = "s3StorageUpdatingTopic";

    private final ObjectMapper objectMapper;

    private final MetadataUtil metadataUtil;


    private final MicroserviceUtil microserviceUtil;

    private final MicroserviceProperties microserviceProperties;

    private final Producer producer;


    @Autowired
    public Consumer(ObjectMapper objectMapper, MetadataUtil metadataUtil, MicroserviceUtil microserviceUtil,
                    MicroserviceProperties microserviceProperties, Producer producer) {
        this.objectMapper = objectMapper;
        this.metadataUtil = metadataUtil;
        this.microserviceUtil = microserviceUtil;
        this.microserviceProperties = microserviceProperties;
        this.producer = producer;
    }

    @KafkaListener(topics = uploadingTopic)
    @Retryable(value = {RuntimeException.class},
            backoff = @Backoff(value = 3000L),
            maxAttempts = 5)
    public void consumeIdOfUploadingFile(String message) {
        try {
            List<String> param = Arrays.asList(objectMapper.readValue(message, String[].class));
            long resourceId = Long.parseLong(param.get(0));
            String token = param.get(1);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            byte[] data = microserviceUtil.getObject(microserviceProperties.getUrl()
                    + microserviceProperties.getResourceServiceUrl() + resourceId, byte[].class, headers);
            log.info("message consumed resourceId :" + resourceId);
            SongMetadataDto songMetadataDto = metadataUtil.createMetadata(Objects.requireNonNull(data), resourceId);
            songMetadataDto = microserviceUtil.postObject(microserviceProperties.getUrl()
                    + microserviceProperties.getSongServiceUrl(), songMetadataDto, SongMetadataDto.class);
            Optional.ofNullable(songMetadataDto)
                    .ifPresent(song -> log.info("song service add metadata of file with id: " + song.getId()));
            producer.sendMessage(param, s3StorageUpdatingTopic);
        } catch (JsonProcessingException e) {
            throw new KafkaProducingException(e.getMessage());
        }
    }

    @Recover
    public void consumeIdOfUploadingFile(RuntimeException e) {
        log.error("internal server error", e);
    }

    @Recover
    public void consumeIdOfUploadingFile(ResourceAccessException e) {
        log.error(e.getMessage());
    }
}
