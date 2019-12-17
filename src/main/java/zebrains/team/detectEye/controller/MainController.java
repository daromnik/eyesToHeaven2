package zebrains.team.detectEye.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import zebrains.team.detectEye.model.KafkaConsumerMessage;
import zebrains.team.detectEye.model.response.ErrorResponseObject;
import zebrains.team.detectEye.model.response.ResponseService;
import zebrains.team.detectEye.producer.KafkaProd;
import zebrains.team.detectEye.utils.DetectEye;
import zebrains.team.detectEye.utils.SaveFile;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
@Log4j
public class MainController {

    @Autowired
    private SaveFile saveFileModel;
    @Autowired
    private DetectEye detectEyeModel;
    @Autowired
    private KafkaProd kafkaProd;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private ResponseService responseService;

    private final String IMAGE_FORMAT = "jpeg";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadFile) {

        log.info("Single file start upload!");

        if (uploadFile.isEmpty()) {
            return responseService.initErrorResponse("please select a file!",
                    ErrorResponseObject.TYPE_INCORRECT_FILE, HttpStatus.FORBIDDEN);
        }

        if (!uploadFile.getContentType().contains(IMAGE_FORMAT)) {
            return responseService.initErrorResponse("please select an image file (jpeg, jpg)!",
                    ErrorResponseObject.TYPE_INCORRECT_FILE, HttpStatus.FORBIDDEN);
        }

        try {
            String pathImage = saveFileModel.saveUploadedFiles(uploadFile);
            if (pathImage.isEmpty()) {
                return responseService.initErrorResponse("Image was not saved!",
                        ErrorResponseObject.TYPE_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String eyeImage = detectEyeModel.detectEye(pathImage);
            if (eyeImage.isEmpty()) {
                return responseService.initErrorResponse("No eyes found in the picture!",
                        ErrorResponseObject.TYPE_WITHOUT_EYE, HttpStatus.FORBIDDEN);
            }

            kafkaProd = context.getBean(KafkaProd.class);
            kafkaProd.setImageEyePath(eyeImage);
            KafkaConsumerMessage kafkaConsumerMessage = kafkaProd.send();

            return responseService.initSuccessResponse(kafkaConsumerMessage);

        } catch (IOException e) {
            log.error("Error!", e);
            return responseService.initErrorResponse(e.getMessage(),
                    ErrorResponseObject.TYPE_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestParam("name") String name) throws URISyntaxException {

        return ResponseEntity.ok("TEST");
    }

}
