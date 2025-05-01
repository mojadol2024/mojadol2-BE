package com.gnu.pbl2.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
public class KafkaConsumer {

    // 메시지 리스너
    private final MessageListener<String, String> messageListener = new MessageListener<String, String>() {
        @Override
        public void onMessage(ConsumerRecord<String, String> record) {
            System.out.println("Received Message: " + record.value());
        }
    };

    // Kafka Listener 설정
    @Bean
    public MessageListenerContainer messageListenerContainer() {
        ContainerProperties containerProps = new ContainerProperties("topic1");  // 토픽 지정
        containerProps.setMessageListener(messageListener);

        return new ConcurrentMessageListenerContainer<>(consumerFactory(), containerProps);
    }

    // Kafka Consumer 설정
    private ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put("bootstrap.servers", "pbl2-kafka1:29092,pbl2-kafka2:29093,pbl2-kafka3:29094");
        consumerProps.put("group.id", "group1");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    // Kafka Listener 시작
    public void startListening() {
        messageListenerContainer().start();
    }
}
