package zebrains.team.detectEye.producer;

import com.google.common.io.Files;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import zebrains.team.detectEye.model.KafkaConsumerMessage;
import zebrains.team.detectEye.model.KafkaProducerMessage;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("prototype")
@Log4j
public class KafkaProd {

    @Autowired
    private KafkaTemplate<String, KafkaProducerMessage> kafkaTemplate;
    @Autowired
    private KafkaProducerMessage kafkaProducerMessage;
    @Autowired
    private KafkaConsumerMessage kafkaConsumerMessage;
    @Autowired
    private ConcurrentHashMap<String, KafkaConsumerMessage> kafkaDataProducerConsumer;

    private String imageEyePath = "";

    @Value("${kafka.server.topic}")
    private String topic;

    public void setImageEyePath(String imageEyePath) {
        this.imageEyePath = imageEyePath;
    }

    public KafkaConsumerMessage send() {

        log.info("THREAD send: " + Thread.currentThread().getName());

        if (imageEyePath.isEmpty()) {
            log.error("Error! Не передено название картинки!");
            return kafkaConsumerMessage;
        }

        String key = Files.getNameWithoutExtension(imageEyePath);

        kafkaProducerMessage.setType(KafkaProducerMessage.TYPE_QUERY);
        kafkaProducerMessage.setFilename(imageEyePath);
        log.info("Начало отправки информации в кафку: key = " + key + "; message = " + kafkaProducerMessage);

        kafkaDataProducerConsumer.put(key, kafkaConsumerMessage);
        synchronized(kafkaConsumerMessage) {
            try {
                kafkaTemplate.send(topic, key, kafkaProducerMessage);
                kafkaConsumerMessage.wait();
            } catch(InterruptedException e) {
                log.error("ERROR" + e.getMessage());
            } finally {
                kafkaDataProducerConsumer.remove(key);
            }
        }

        log.info("Конец отправки информации в кафку: key = " + key + "; message = " + kafkaProducerMessage);
        log.info("Возратился ответ от кафки: key = " + key + "; message = " + kafkaConsumerMessage);

        return kafkaConsumerMessage;
    }
}
