package com.os.course.config;

import com.os.course.util.TopicName;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.retry.annotation.Backoff;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    private final KafkaProperties kafkaProperties;
    private final TopicName topicName;

    public KafkaTopicConfig(KafkaProperties kafkaProperties, TopicName topicName) {
        this.kafkaProperties = kafkaProperties;
        this.topicName = topicName;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getServer());
        return new KafkaAdmin(configs);
    }

    @Bean
    @RetryableTopic(backoff = @Backoff(value = 30000L),
            attempts = "5",
            include = ConnectException.class, exclude = NullPointerException.class)
    public NewTopic uploadingMp3() {
        return TopicBuilder.name(topicName.getUploadingTopicName())
                .partitions(1)
                .compact()
                .build();
    }
}


