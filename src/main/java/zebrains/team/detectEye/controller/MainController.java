package zebrains.team.detectEye.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import zebrains.team.detectEye.utils.DetectEye;
import zebrains.team.detectEye.utils.SaveFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;

@RestController
public class MainController {

    private SaveFile saveFileModel;
    private DetectEye detectEyeModel;

    public MainController(SaveFile saveFileModel, DetectEye detectEyeModel) {
        this.saveFileModel = saveFileModel;
        this.detectEyeModel = detectEyeModel;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadFile) {

        System.out.println("Single file start upload!");

        if (uploadFile.isEmpty()) {
            return new ResponseEntity<>("please select a file!", HttpStatus.OK);
        }

        if (!uploadFile.getContentType().contains("image")) {
            return new ResponseEntity<>("please select a image file (jpeg, jpg, png)!", HttpStatus.OK);
        }

        try {
            String pathImage = saveFileModel.saveUploadedFiles(uploadFile);

            detectEyeModel.init(pathImage);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Successfully uploaded - " +
                uploadFile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

    }

    @PostMapping("/test")
    public String test(@RequestParam("name") String name) throws URISyntaxException {
        String userDirectory = FileSystems.getDefault()
                .getPath("")
                .toAbsolutePath()
                .toString();
        return userDirectory;
    }

}
