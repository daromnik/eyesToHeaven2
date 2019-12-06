package zebrains.team.detectEye.model;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Data
public class KafkaProducerMessage {

    public static final String TYPE_QUERY = "query";

    private String type;
    private String filename;

}
