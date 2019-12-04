package zebrains.team.detectEye.model;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Data
public class ResponseObject {

    public static final String STATUS_ERROR = "error";
    public static final String STATUS_SUCCESS = "success";

    private String status;
    private String description;

}
