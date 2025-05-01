package com.gnu.pbl2.kafka;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;

@EnableKafka
public class KafkaProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "topic1";  // 토픽명

    // 메시지 보내기 메서드
    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
