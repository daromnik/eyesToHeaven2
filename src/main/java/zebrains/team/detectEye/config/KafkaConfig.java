package zebrains.team.detectEye.config;

import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer2;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.ui.Model;
import zebrains.team.detectEye.error.KafkaErrorHandler;
import zebrains.team.detectEye.model.KafkaConsumerMessage;
import zebrains.team.detectEye.model.KafkaProducerMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableKafka
@Configuration
@Log4j
public class KafkaConfig {

    @Value("${kafka.server.url}")
    private String kafkaServerUrl;

    @Value("${kafka.server.group}")
    private String kafkaGroup;

    @Bean
    public ProducerFactory<String, KafkaProducerMessage> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, KafkaProducerMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


//    @Bean
//    public ReplyingKafkaTemplate<String, KafkaProducerMessage, KafkaConsumerMessage> replyKafkaTemplate(ProducerFactory<String, KafkaProducerMessage> pf, KafkaMessageListenerContainer<String, KafkaConsumerMessage> container) {
//        return new ReplyingKafkaTemplate<String, KafkaProducerMessage, KafkaConsumerMessage>(pf, container);
//    }
//
//    @Bean
//
//    public KafkaMessageListenerContainer<String, Model> replyContainer(ConsumerFactory<String, KafkaConsumerMessage> cf) {
//        ContainerProperties containerProperties = new ContainerProperties(requestReplyTopic);
//        return new KafkaMessageListenerContainer<>(cf, containerProperties);
//
//    }

    @Bean
    public ConsumerFactory<String, KafkaConsumerMessage> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroup);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");
        //config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);


        ErrorHandlingDeserializer2<String> keyErrorHandlingDeserializer
                = new ErrorHandlingDeserializer2<>(new StringDeserializer());
        ErrorHandlingDeserializer2<KafkaConsumerMessage> valueErrorHandlingDeserializer
                = new ErrorHandlingDeserializer2<>(new JsonDeserializer<>(KafkaConsumerMessage.class));

        return new DefaultKafkaConsumerFactory<>(config, keyErrorHandlingDeserializer, valueErrorHandlingDeserializer);


        //return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(KafkaConsumerMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaConsumerMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaConsumerMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //factory.setRecordFilterStrategy(record -> record.value().getResult().isEmpty());
        factory.setErrorHandler(new KafkaErrorHandler());
        return factory;
    }

    @Bean
    public ConcurrentHashMap<String, KafkaConsumerMessage> kafkaDataProducerConsumer() {
        return new ConcurrentHashMap<>();
    }

}
