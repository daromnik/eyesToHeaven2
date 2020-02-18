package zebrains.team.detectEye.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = false)
@Component
@Scope("prototype")
@Data
public class KafkaConsumerMessage extends KafkaMessage {

    public static final String RESULT_ERROR = "error";
    public static final String RESULT_OK = "ok";

    private String description;
    private String result;
    private String error;
    private String diff;
    private String algo;
    private String name;
    private String uuid;
    private String key;
    private String id;

}
