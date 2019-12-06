package zebrains.team.detectEye.producer;

import com.google.common.io.Files;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import zebrains.team.detectEye.model.KafkaConsumerMessage;
import zebrains.team.detectEye.model.KafkaProducerMessage;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j
public class KafkaProd {

    private KafkaTemplate<String, KafkaProducerMessage> kafkaTemplate;
    private KafkaProducerMessage kafkaProducerMessage;
    private KafkaConsumerMessage kafkaConsumerMessage;
    private ConcurrentHashMap<String, KafkaConsumerMessage> kafkaDataProducerConsumer;

    private String imageEyePath = "";

    @Value("${kafka.server.topic}")
    private String topic;

    public KafkaProd(
            KafkaTemplate<String, KafkaProducerMessage> kafkaTemplate,
            KafkaProducerMessage kafkaProducerMessage,
            KafkaConsumerMessage kafkaConsumerMessage,
            ConcurrentHashMap<String, KafkaConsumerMessage> kafkaDataProducerConsumer
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProducerMessage = kafkaProducerMessage;
        this.kafkaConsumerMessage = kafkaConsumerMessage;
        this.kafkaDataProducerConsumer = kafkaDataProducerConsumer;
    }

    public void setImageEyePath(String imageEyePath) {
        this.imageEyePath = imageEyePath;
    }

    public KafkaConsumerMessage send() {

        if (imageEyePath.isEmpty()) {
            log.error("Error! Не передено название картинки!");
            return kafkaConsumerMessage;
        }

        String key = Files.getNameWithoutExtension(imageEyePath);

        kafkaProducerMessage.setType(KafkaProducerMessage.TYPE_QUERY);
        kafkaProducerMessage.setFilename(imageEyePath);
        log.info("Начало отправки информации в кафку: key = " + key + "; message = " + kafkaProducerMessage);
        kafkaTemplate.send(topic, key, kafkaProducerMessage);

        kafkaDataProducerConsumer.put(key, kafkaConsumerMessage);
        synchronized(kafkaConsumerMessage) {
            try {
                kafkaConsumerMessage.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                kafkaDataProducerConsumer.remove(key);
            }
        }
        log.info("Конец отправки информации в кафку: key = " + key + "; message = " + kafkaProducerMessage);
        log.info("Возратился ответ от кафки: key = " + key + "; message = " + kafkaConsumerMessage);

        return kafkaConsumerMessage;
    }
}
