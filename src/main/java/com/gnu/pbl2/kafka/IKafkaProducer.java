package com.gnu.pbl2.kafka;

import com.gnu.pbl2.kafka.dto.KafkaVideoPayload;

public interface IKafkaProducer {
    void send(KafkaVideoPayload payload);
}
