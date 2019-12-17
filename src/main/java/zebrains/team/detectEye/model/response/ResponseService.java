package zebrains.team.detectEye.model.response;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import zebrains.team.detectEye.model.KafkaConsumerMessage;

import java.nio.file.Paths;

@Service
@Log4j
public class ResponseService {

    @Value("${spring.application.eyeFolder}")
    private String UPLOAD_FOLDER;
    private final String IMAGE_FORMAT = ".jpg";

    private final SuccessResponseObject successResponseObject;
    private final ErrorResponseObject errorResponseObject;

    public ResponseService(SuccessResponseObject successResponseObject, ErrorResponseObject errorResponseObject) {
        this.successResponseObject = successResponseObject;
        this.errorResponseObject = errorResponseObject;
    }

    public ResponseEntity initSuccessResponse(KafkaConsumerMessage data) {
        successResponseObject.setName(data.getName());
        successResponseObject.setDescription(data.getDescription());
        successResponseObject.setUrl(Paths.get(UPLOAD_FOLDER, data.getUuid() + IMAGE_FORMAT).toString());
        log.info("Success: \ndata rom kafka:\n" + data + "\nResponse:\n" + successResponseObject);
        return ResponseEntity.ok(successResponseObject);
    }

    public ResponseEntity initErrorResponse(String errorDescription, String typeError, HttpStatus forbidden) {
        errorResponseObject.setDescription(errorDescription);
        errorResponseObject.setType(typeError);
        log.error("Error: \nDescription: " + errorDescription + "\nType: " + typeError);
        return ResponseEntity.status(forbidden).body(errorResponseObject);
    }

}
