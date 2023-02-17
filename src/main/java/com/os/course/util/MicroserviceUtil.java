package com.os.course.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class MicroserviceUtil {
    private final RestTemplate restTemplate = new RestTemplate();

    public <T> T postObject(String url, T object, Class<T> responseType) {
        return restTemplate.postForObject(url, object, responseType);
    }

    public <T> T getObject(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }

    @Recover
    public void consumeIdOfUploadingFile(RuntimeException e) {
        log.error("internal server error");
    }

    @Recover
    public void consumeIdOfUploadingFile(ResourceAccessException e) {
        log.error(e.getMessage());
    }
}
