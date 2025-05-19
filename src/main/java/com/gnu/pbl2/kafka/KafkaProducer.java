package com.gnu.pbl2.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnu.pbl2.kafka.dto.KafkaVideoPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@EnableKafka
@Component
@Slf4j
@Profile("!local")
public class KafkaProducer implements IKafkaProducer{

    private final KafkaTemplate<String, KafkaVideoPayload> kafkaTemplate;

    private static final String TOPIC = "interview-video";

    public KafkaProducer(KafkaTemplate<String, KafkaVideoPayload> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // 메시지 보내기 메서드
    public void send(KafkaVideoPayload payload) {
        try {
            kafkaTemplate.send(TOPIC, payload);
        } catch (Exception e) {
            log.error("Kafka 메시지 전송 실패: {}", e.getMessage());
        }
    }
}
