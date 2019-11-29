package zebrains.team.detectEye.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SaveFile {

    @Value("${spring.application.uploadFolder}")
    private String UPLOAD_FOLDER;

    /**
     * Сохраняет файл в указанную директорию
     *
     * @param file MultipartFile
     * @throws IOException
     */
    public void saveUploadedFiles(MultipartFile file) throws IOException {
        System.out.println("Upload folder: " + UPLOAD_FOLDER);

        if (!file.isEmpty() && checkOrCreateDirectory(UPLOAD_FOLDER)) {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
        }
    }

    /**
     * Метод проверяет существование директории,
     * если отсутсвует - создает.
     *
     * @param folder String
     * @return boolean
     */
    public boolean checkOrCreateDirectory(String folder) {
        final File directory = new File(folder);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Create directory " + directory.getAbsolutePath() + " - SUCCESS");
                return true;
            } else {
                System.out.println("Create directory " + directory.getAbsolutePath() + " - ERROR");
                return false;
            }
        }
        return true;
    }

}
