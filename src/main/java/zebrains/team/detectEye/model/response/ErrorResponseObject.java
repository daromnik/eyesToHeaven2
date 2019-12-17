package zebrains.team.detectEye.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Data
@EqualsAndHashCode(callSuper = true)
public class ErrorResponseObject extends BaseResponseObject {

    public static final String TYPE_WITHOUT_EYE = "without_eye";
    public static final String TYPE_INCORRECT_FILE = "incorrect_file";
    public static final String TYPE_SERVER_ERROR = "server_error";

    private String type;

    public ErrorResponseObject() {
        setStatus(STATUS_ERROR);
    }

}
