package zebrains.team.detectEye.utils;

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
import java.util.UUID;

@Service
public class DetectEye {

    private BufferedImage originImage;
    private CascadeClassifier faceCascade;
    private Mat originMat;
    private final String pngFormat = ".png";
    private ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    private final String CLASSIFIER_PATH = "haarcascades/haarcascade_eye.xml";

    @Value("${spring.application.uploadFolder}")
    private String UPLOAD_FOLDER;

    public DetectEye() {
        OpenCV.loadLocally();
        faceCascade = new CascadeClassifier(classloader.getResource(CLASSIFIER_PATH).getPath());
    }

    /**
     *
     */
    public void init(String pathImage) {
        File file = new File(pathImage);
        if (file.exists()) {
            System.out.println("Есть картинка");
            try {
                originImage = ImageIO.read(file);

                byte[] pixels = ((DataBufferByte) originImage.getRaster().getDataBuffer()).getData();
                originMat = new Mat(originImage.getHeight(), originImage.getWidth(), CvType.CV_8UC3);
                originMat.put(0, 0, pixels);

                detect(originMat);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Нет картинки");
        }
    }

    /**
     * Возвращает оригинальную картинку в виде массива байтов
     * @return ByteArrayInputStream
     */
    public ByteArrayInputStream getOriginImageStream() {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(pngFormat, originMat , buffer);
        return new ByteArrayInputStream(buffer.toArray());
    }

    /**
     * Определение на картинке глаз,
     * выделение их в зеленые прямоугольники,
     * сохранение этих областей в файлы.
     *
     * @param originMat Mat
     * @throws IOException
     */
    private void detect(Mat originMat) throws IOException {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(originMat, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        //loadClassifier(CLASSIFIER_PATH);

        this.faceCascade.detectMultiScale(
                grayFrame,
                faces
        );

        // каждый прямоугольник - это глас
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            //берем первый элмент массива
            Rect item = facesArray[0];
            BufferedImage dest = originImage.getSubimage(item.x, item.y, item.width, item.height);
            UUID uuid = UUID.randomUUID(); // рандомное название картинки
            File fileForEye = new File(UPLOAD_FOLDER + uuid.toString() + pngFormat);
            ImageIO.write(dest, "PNG", fileForEye);
        }
    }

}
