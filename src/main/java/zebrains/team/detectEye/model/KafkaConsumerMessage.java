package zebrains.team.detectEye.model;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Data
public class KafkaConsumerMessage {

    public static final String RESULT_ERROR = "error";
    public static final String RESULT_OK = "ok";

    private String result;
    private String error;
    private String diff;
    private String algo;
    private String name;
    private String uuid;

}
