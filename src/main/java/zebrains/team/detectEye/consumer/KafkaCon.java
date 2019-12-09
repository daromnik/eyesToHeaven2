package zebrains.team.detectEye.consumer;

import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import zebrains.team.detectEye.model.KafkaConsumerMessage;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j
public class KafkaCon {

    @Autowired
    private ConcurrentHashMap<String, KafkaConsumerMessage> kafkaDataProducerConsumer;

    @KafkaListener(topics = "#{'${kafka.server.topic}'}", groupId = "#{'${kafka.server.group}'}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, KafkaConsumerMessage> record) {
        log.info("Consumer message: key: " + record.key() + "; message: " + record.value());

        if (record.key() != null && kafkaDataProducerConsumer.get(record.key()) != null) {
            KafkaConsumerMessage kafkaConsumerMessage = kafkaDataProducerConsumer.get(record.key());
            BeanUtils.copyProperties(record.value(), kafkaConsumerMessage);
            synchronized(kafkaConsumerMessage) {
                kafkaDataProducerConsumer.put(record.key(), kafkaConsumerMessage);
                kafkaConsumerMessage.notify();
            }
        }

    }
}