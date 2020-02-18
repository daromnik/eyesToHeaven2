package zebrains.team.detectEye.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = false)
@Component
@Scope("prototype")
@Data
public class KafkaProducerMessage extends KafkaMessage {

    public static final String TYPE_QUERY = "query";

    private String type;
    private String filename;

}
