package zebrains.team.detectEye.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.nio.file.FileSystems;

@Configuration
public class ApplicationConfig {

    private static final String nameFileProperties = "application.properties";

    /**
     * Загрузка внешнего файла с конфигами
     *
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();

        String jarDirectory = FileSystems.getDefault()
                .getPath("")
                .toAbsolutePath()
                .toString();

        System.out.println("JAR Directory: " + jarDirectory);

        String configFile = jarDirectory + File.separator + nameFileProperties;

        System.out.println("Config File: " + configFile);

        FileSystemResource fileSystemResource = new FileSystemResource(configFile);

        System.out.println("Config File is exists: " + fileSystemResource.exists());

        if (fileSystemResource.exists()) {
            properties.setLocation(new FileSystemResource(configFile));
            properties.setIgnoreResourceNotFound(false);
        }
        return properties;
    }

}
