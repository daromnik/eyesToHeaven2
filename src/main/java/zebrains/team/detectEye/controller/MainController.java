package zebrains.team.detectEye.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import zebrains.team.detectEye.utils.DetectEye;
import zebrains.team.detectEye.model.ResponseObject;
import zebrains.team.detectEye.utils.SaveFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
@Log4j
public class MainController {

    private SaveFile saveFileModel;
    private DetectEye detectEyeModel;
    private ResponseObject responseObject;

    public MainController(
            SaveFile saveFileModel,
            DetectEye detectEyeModel,
            ResponseObject responseObject
    ) {
        this.saveFileModel = saveFileModel;
        this.detectEyeModel = detectEyeModel;
        this.responseObject = responseObject;
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
            boolean isFindEye = detectEyeModel.detectEye(pathImage);
            if (!isFindEye) {
                return initErrorResponse("No eyes found in the picture");
            }
        } catch (IOException e) {
            log.error("Error!", e);
            return initErrorResponse(e.getMessage());
        }

        return initSuccessResponse("Successfully uploaded - " + uploadFile.getOriginalFilename());
    }

    private ResponseEntity initSuccessResponse(String message) {
        responseObject.setDescription(message);
        responseObject.setStatus(responseObject.STATUS_SUCCESS);
        return new ResponseEntity<>(responseObject, new HttpHeaders(), HttpStatus.OK);
    }

    private ResponseEntity initErrorResponse(String error) {
        responseObject.setDescription(error);
        responseObject.setStatus(responseObject.STATUS_ERROR);
        return new ResponseEntity<>(responseObject, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestParam("name") String name) throws URISyntaxException {
        String userDirectory = FileSystems.getDefault()
                .getPath("")
                .toAbsolutePath()
                .toString();

        return initSuccessResponse(userDirectory);
    }

}
