package zebrains.team.detectEye.kafka.error;

import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ContainerAwareErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.DeserializationException;

import java.util.List;

@Log4j
public class KafkaErrorHandler implements ContainerAwareErrorHandler {

    @Override
    public void handle(Exception thrownException, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer, MessageListenerContainer container) {
                //String ss = "";
        if (!records.isEmpty()) {
            ConsumerRecord<?, ?> record = records.get(0);
            String topic = record.topic();
            long offset = record.offset();
            int partition = record.partition();
            if (thrownException.getClass().equals(DeserializationException.class)) {
                DeserializationException exception = (DeserializationException) thrownException;
                String malformedMessage = new String(exception.getData());
                log.info(String.format("Skipping message with topic %s and offset %s  - malformed message: %s , exception: %s",
                        topic, offset, malformedMessage, exception.getLocalizedMessage()));
            } else {
                log.info(String.format("Skipping message with topic %s - offset %s - partition %s - exception %s",
                        topic, offset, partition, thrownException));
            }
        } else {
            log.info(String.format("Consumer exception - cause: %s", thrownException.getMessage()));
        }
    }
}
