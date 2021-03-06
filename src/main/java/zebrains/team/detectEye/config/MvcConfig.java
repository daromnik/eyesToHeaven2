package zebrains.team.detectEye.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import zebrains.team.detectEye.utils.UploadFileRequestInterceptor;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private UploadFileRequestInterceptor uploadFileRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(uploadFileRequestInterceptor)
                .addPathPatterns("/**/api/**/");
    }
}
