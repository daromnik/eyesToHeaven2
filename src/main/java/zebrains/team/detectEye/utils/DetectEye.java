package zebrains.team.detectEye.utils;

import com.google.common.io.Files;
import lombok.extern.log4j.Log4j;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
@Log4j
public class DetectEye {

    private BufferedImage originImage;
    private CascadeClassifier eyeCascade;
    private CascadeClassifier faceCascade;
    private Mat originMat;
    private String imageFormat;
    private String imageName;
    private ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    @Value("${spring.application.eyeFolder}")
    private String UPLOAD_FOLDER;

    @Autowired
    public DetectEye(
            @Value("${opencv.classifier.path}") String classifierEyePath,
            @Value("${opencv.classifier.face.path}") String classifierFacePath
    ) {
        OpenCV.loadLocally();
        eyeCascade = new CascadeClassifier(classifierEyePath);
        faceCascade = new CascadeClassifier(classifierFacePath);
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
                originMat = Imgcodecs.imread(pathImage);
                return detectAndSave(originMat);
            } catch (Throwable e) {
                log.error("Error!", e);
                e.printStackTrace();
            }
        } else {
            log.error("Нет картинки " + pathImage);
        }
        return "";
    }

    /**
     * Определение на картинке лица, а на нем глаз,
     * вырезание этих обблайстей и сохранение в файлы.
     * Возвращает путь у файлу или же пустую строку, если глаз не найдено.
     *
     * @param originMat Mat
     * @return String
     */
    private String detectAndSave(Mat originMat) {
        MatOfRect eyes = new MatOfRect();
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(originMat, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        //this.faceCascade.detectMultiScale(grayFrame, faces);
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.04, 2, 0, new Size(300, 300), new Size(0, 0) );

        Rect[] facesArray = faces.toArray();
        Arrays.sort(facesArray, (r1, r2) -> (int)(r2.area() - r1.area()));
        if (facesArray.length > 0) {
            for (Rect item : facesArray) {

                Mat faceMat = originMat.submat(item.y, item.y + item.height, item.x, item.x + item.width);
                Mat grayFaceFrame = new Mat();
                Imgproc.cvtColor(faceMat, grayFaceFrame, Imgproc.COLOR_BGR2GRAY);
                Imgproc.equalizeHist(grayFaceFrame, grayFaceFrame);

                this.eyeCascade.detectMultiScale(grayFaceFrame, eyes, 1.04, 2, 0, new Size(30, 30), new Size(0, 0) );
                Rect[] eyesArray = eyes.toArray();
                Arrays.sort(eyesArray, (r1, r2) -> (int)(r2.area() - r1.area()));
                if (eyesArray.length > 0) {
                    for (Rect eye : eyesArray) {
                        Mat eyeMat = faceMat.submat(eye.y, eye.y + eye.height, eye.x, eye.x + eye.width);
                        String eyeImageName = imageName + "_eye." + imageFormat;
                        String eyeImage = Paths.get(UPLOAD_FOLDER, eyeImageName).toString();
                        Imgcodecs.imwrite(eyeImage, eyeMat);
                        return eyeImageName;
                    }
                }
            }
        }

        return "";
    }
}
