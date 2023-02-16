package com.os.course.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.course.model.dto.SongMetadataDto;
import com.os.course.model.exception.KafkaProducingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class Consumer {

    private static final String uploadingTopic = "uploadingMp3";

    private final ObjectMapper objectMapper;

    private final MetadataUtil metadataUtil;

    private final TopicName topicName;

    @Autowired
    public Consumer(ObjectMapper objectMapper, MetadataUtil metadataUtil, TopicName topicName) {
        this.objectMapper = objectMapper;
        this.metadataUtil = metadataUtil;
        this.topicName = topicName;
    }

    @KafkaListener(topics = uploadingTopic)
    @Retryable(value = ConnectException.class, backoff = @Backoff(value = 30000L))
    public void consumeIdOfUploadingFile(String message) {
        try {
            Long resourceId = objectMapper.readValue(message, Long.class);
            RestTemplate restTemplate = new RestTemplate();
            byte[] data = restTemplate.getForObject(Constant.GET_MP3_FILE_URL + resourceId, byte[].class);
            log.info("message consumed resourceId :" + resourceId);
            SongMetadataDto songMetadataDto = metadataUtil.createMetadata(Objects.requireNonNull(data), resourceId);
            songMetadataDto = restTemplate.postForObject(Constant.POST_SONG_METADATA, songMetadataDto, SongMetadataDto.class);
            Optional.ofNullable(songMetadataDto)
                    .ifPresent(song -> log.info("song service add metadata of file with id: " + song.getId()));
        } catch (JsonProcessingException e) {
            throw new KafkaProducingException(e.getMessage());
        }
    }
}
