package zebrains.team.detectEye.utils;

import com.google.common.io.Files;
import lombok.extern.log4j.Log4j;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Service
@Log4j
public class DetectEye {

    private BufferedImage originImage;
    private CascadeClassifier faceCascade;
    private Mat originMat;
    private String imageFormat;
    private String imageName;
    private ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    private final String CLASSIFIER_PATH = "haarcascades/haarcascade_eye.xml";

    @Value("${spring.application.eyeFolder}")
    private String UPLOAD_FOLDER;

    public DetectEye() {
        OpenCV.loadLocally();
        faceCascade = new CascadeClassifier(classloader.getResource(CLASSIFIER_PATH).getPath());
    }

    /**
     * @return boolean
     */
    public String detectEye(String pathImage) {
        SaveFile.checkOrCreateDirectory(UPLOAD_FOLDER);
        File file = new File(pathImage);
        imageFormat = Files.getFileExtension(pathImage);
        imageName = Files.getNameWithoutExtension(pathImage);
        if (file.exists()) {
            log.info("Есть картинка " + pathImage);
            try {
                originImage = ImageIO.read(file);

                byte[] pixels = ((DataBufferByte) originImage.getRaster().getDataBuffer()).getData();
                originMat = new Mat(originImage.getHeight(), originImage.getWidth(), CvType.CV_8UC3);
                originMat.put(0, 0, pixels);
                return detectAndSave(originMat);
            } catch (IOException e) {
                log.error("Error!", e);
                e.printStackTrace();
            }
        } else {
            log.error("Нет картинки " + pathImage);
        }
        return "";
    }

    /**
     * Возвращает оригинальную картинку в виде массива байтов
     * @return ByteArrayInputStream
     */
    public ByteArrayInputStream getOriginImageStream() {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(imageFormat, originMat , buffer);
        return new ByteArrayInputStream(buffer.toArray());
    }

    /**
     * Определение на картинке глаз,
     * выделение их в зеленые прямоугольники,
     * сохранение этих областей в файлы.
     *
     * @param originMat Mat
     * @throws IOException
     * @return boolean
     */
    private String detectAndSave(Mat originMat) throws IOException {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(originMat, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        this.faceCascade.detectMultiScale(
                grayFrame,
                faces
        );

        // каждый прямоугольник - это глаз
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            //берем первый элмент массива
            Rect item = facesArray[0];
            BufferedImage dest = originImage.getSubimage(item.x, item.y, item.width, item.height);
            String eyeImageName = imageName + "_eye." + imageFormat;
            File fileForEye = new File(UPLOAD_FOLDER + eyeImageName);
            ImageIO.write(dest, imageFormat, fileForEye);
            return eyeImageName;
        } else {
            return "";
        }
    }

}
