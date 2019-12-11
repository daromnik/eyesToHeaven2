package zebrains.team.detectEye.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import zebrains.team.detectEye.model.KafkaConsumerMessage;
import zebrains.team.detectEye.model.ResponseObject;
import zebrains.team.detectEye.producer.KafkaProd;
import zebrains.team.detectEye.utils.DetectEye;
import zebrains.team.detectEye.utils.SaveFile;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
@Log4j
public class MainController {

    private SaveFile saveFileModel;
    private DetectEye detectEyeModel;
    private ResponseObject responseObject;
    private KafkaProd kafkaProd;

    @Autowired
    private ApplicationContext context;

    public MainController(
            SaveFile saveFileModel,
            DetectEye detectEyeModel,
            ResponseObject responseObject
            //KafkaProd kafkaProd
    ) {
        this.saveFileModel = saveFileModel;
        this.detectEyeModel = detectEyeModel;
        this.responseObject = responseObject;
        //this.kafkaProd = kafkaProd;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadFile) {
        log.info("Single file start upload!");

        if (uploadFile.isEmpty()) {
            return initErrorResponse("please select a file!");
        }

        if (!uploadFile.getContentType().contains("jpeg")) {
            return initErrorResponse("please select an image file (jpeg, jpg)!");
        }

        try {
            String pathImage = saveFileModel.saveUploadedFiles(uploadFile);
            String eyeImage = detectEyeModel.detectEye(pathImage);
            if (eyeImage.isEmpty()) {
                return initErrorResponse("No eyes found in the picture");
            }

            kafkaProd = context.getBean(KafkaProd.class);
            kafkaProd.setImageEyePath(eyeImage);
            KafkaConsumerMessage kafkaConsumerMessage = kafkaProd.send();

            return initSuccessResponse(kafkaConsumerMessage);

        } catch (IOException | InterruptedException e) {
            log.error("Error!", e);
            return initErrorResponse(e.getMessage());
        }
    }

    private ResponseEntity initSuccessResponse(KafkaConsumerMessage data) {
        responseObject.setStatus(ResponseObject.STATUS_SUCCESS);
        responseObject.setData(data);
        responseObject.setDescription("");
        log.info("Success: " + data);
        return ResponseEntity.ok(responseObject);
    }

    private ResponseEntity initErrorResponse(String error) {
        responseObject.setDescription(error);
        responseObject.setStatus(ResponseObject.STATUS_ERROR);
        responseObject.setData(null);
        log.error("Error: " + error);
        return ResponseEntity.badRequest().body(responseObject);
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestParam("name") String name) throws URISyntaxException {

        return ResponseEntity.ok("TEST");
    }

}
