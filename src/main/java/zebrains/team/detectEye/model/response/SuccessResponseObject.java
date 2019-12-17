package zebrains.team.detectEye.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Data
@EqualsAndHashCode(callSuper = true)
public class SuccessResponseObject extends BaseResponseObject {

    private String name;
    private String url;

    public SuccessResponseObject() {
        setStatus(STATUS_SUCCESS);
    }

}
