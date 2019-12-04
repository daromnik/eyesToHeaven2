package zebrains.team.detectEye.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Image {

    private String name;
    private String url;

}
