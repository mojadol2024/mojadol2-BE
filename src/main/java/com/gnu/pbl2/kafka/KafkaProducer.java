package com.gnu.pbl2.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnu.pbl2.kafka.dto.KafkaVideoPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@EnableKafka
@Component
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "interview-video";

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // 메시지 보내기 메서드
    public void send(KafkaVideoPayload payload) {
        try {
            // Kafka에 보내기 전에 JSON 형식으로 변환
            String payloadJson = new ObjectMapper().writeValueAsString(payload);
            kafkaTemplate.send(TOPIC, payloadJson);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 전송 실패: {}", e.getMessage());
        }
    }
}
