package zebrains.team.detectEye.config;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.nio.file.FileSystems;

@Configuration
@Log4j
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

        PropertyConfigurator.configure(nameFileProperties); // подгрузка настроект для Log4j

        String jarDirectory = FileSystems.getDefault()
                .getPath("")
                .toAbsolutePath()
                .toString();

        log.info("JAR Directory: " + jarDirectory);

        String configFile = jarDirectory + File.separator + nameFileProperties;

        log.info("Config File: " + configFile);

        FileSystemResource fileSystemResource = new FileSystemResource(configFile);

        if (fileSystemResource.exists()) {
            properties.setLocation(new FileSystemResource(configFile));
            properties.setIgnoreResourceNotFound(false);
            log.info("Config File (" + nameFileProperties + ") is exists.");
        } else {
            log.error("Config File (" + nameFileProperties + ") is not exists.");
        }
        return properties;
    }

}
