package com.gnu.pbl2.kafka;

import com.gnu.pbl2.kafka.dto.KafkaVideoPayload;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class DummyKafkaProducer implements IKafkaProducer{

    @Override
    public void send(KafkaVideoPayload payload) {

    }
}
