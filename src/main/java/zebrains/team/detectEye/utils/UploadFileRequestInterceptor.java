package zebrains.team.detectEye.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

@Component
public class UploadFileRequestInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        StringBuilder requestInfo = new StringBuilder("Request info:\n");
        requestInfo.append(String.format("Request URL = %s%n", request.getRequestURL().toString()));

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            requestInfo.append(String.format("Header '%s' = %s%n", key, value));
        }

        requestInfo.append(String.format("IP = %s%n", request.getRemoteAddr()));

        System.out.println(requestInfo.toString());

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
    }
}