package zebrains.team.detectEye.utils;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Component
@Log4j
public class UploadFileRequestInterceptor extends HandlerInterceptorAdapter {

    /**
     * Обработка входящих запросов к API и вывод информации по ним
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler Object
     * @return boolean
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        StringBuilder requestInfo = new StringBuilder("Request info:\n");
        requestInfo.append(String.format("Request URL = %s%n", request.getRequestURL()));
        requestInfo.append(String.format("Request method = %s%n", request.getMethod()));

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            requestInfo.append(String.format("Header '%s' = %s%n", key, value));
        }

        requestInfo.append(String.format("IP = %s%n", request.getRemoteAddr()));

        log.info(requestInfo.toString());

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