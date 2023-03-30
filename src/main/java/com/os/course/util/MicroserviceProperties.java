package com.os.course.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@RefreshScope
@Component
public class MicroserviceProperties {
    @Value("${topic.uploadingMp3.name}")
    private  String uploadingTopicName;
    @Value("${resource_service.url}")
    private String resourceServiceUrl;
    @Value("${song_service.url}")
    private String songServiceUrl;
    @Value("${server.url}")
    private String url;

}
