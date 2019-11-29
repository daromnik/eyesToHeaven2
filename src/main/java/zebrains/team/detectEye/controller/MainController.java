package zebrains.team.detectEye.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import zebrains.team.detectEye.Utils.SaveFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;

@RestController
public class MainController {

    final
    SaveFile saveFileModel;

    public MainController(SaveFile saveFileModel) {
        this.saveFileModel = saveFileModel;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadFile) {

        System.out.println("Single file start upload!");

        if (uploadFile.isEmpty()) {
            return new ResponseEntity<>("please select a file!", HttpStatus.OK);
        }

        try {
            saveFileModel.saveUploadedFiles(uploadFile);
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
