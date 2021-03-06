package zebrains.team.detectEye.utils;

import com.google.common.io.Files;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Log4j
public class SaveFile {

    @Value("${spring.application.uploadFolder}")
    private String UPLOAD_FOLDER;

    /**
     * Сохраняет файл в указанную директорию
     *
     * @param file MultipartFile
     * @throws IOException
     * @return String
     */
    public String saveUploadedFiles(MultipartFile file) throws IOException {
        log.info("Upload folder: " + UPLOAD_FOLDER);

        if (!file.isEmpty() && checkOrCreateDirectory(UPLOAD_FOLDER)) {
            byte[] bytes = file.getBytes();
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + "." + Files.getFileExtension(file.getOriginalFilename());
            Path path = Paths.get(UPLOAD_FOLDER, fileName);
            log.info("Файл сохранен: " + fileName);
            java.nio.file.Files.write(path, bytes);
            return path.toString();
        }

        return "";
    }

    /**
     * Метод проверяет существование директории,
     * если отсутсвует - создает.
     *
     * @param folder String
     * @return boolean
     */
    public static boolean checkOrCreateDirectory(String folder) {
        final File directory = new File(folder);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                log.info("Create directory " + directory.getAbsolutePath() + " - SUCCESS");
                return true;
            } else {
                log.error("Create directory " + directory.getAbsolutePath() + " - ERROR");
                return false;
            }
        }
        return true;
    }
}
