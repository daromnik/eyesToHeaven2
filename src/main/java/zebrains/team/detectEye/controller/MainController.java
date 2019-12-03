package zebrains.team.detectEye.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zebrains.team.detectEye.utils.DetectEye;
import zebrains.team.detectEye.utils.SaveFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.util.Map;

@RestController
@RequestMapping("/api")
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
            return initResponse("please select a file!", HttpStatus.BAD_REQUEST);
        }

        if (!uploadFile.getContentType().contains("jpeg")) {
            return initResponse("please select a image file (jpeg, jpg)!", HttpStatus.BAD_REQUEST);
        }

        try {
            String pathImage = saveFileModel.saveUploadedFiles(uploadFile);
            boolean isFindEye = detectEyeModel.detectEye(pathImage);
            if (!isFindEye) {
                return initResponse("No eyes found in the picture", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return initResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return initResponse("Successfully uploaded - " + uploadFile.getOriginalFilename(), HttpStatus.OK);
    }

    private ResponseEntity initResponse(String message, HttpStatus code) {
        return new ResponseEntity<>(message, new HttpHeaders(), code);
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
