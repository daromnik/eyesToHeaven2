package zebrains.team.detectEye.model.response;

import lombok.Data;

@Data
public class BaseResponseObject {

    public static final String STATUS_ERROR = "error";
    public static final String STATUS_SUCCESS = "success";

    private String status;
}
