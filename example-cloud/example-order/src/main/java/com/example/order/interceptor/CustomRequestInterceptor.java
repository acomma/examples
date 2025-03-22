package com.example.order.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 自定义 Feign 请求拦截器，实现授权用户信息中继功能。这个拦截器需要定义在每一个需要中继功能的服务中，可能定义在公共模块更好一点，这里偷个懒儿。
 */
@Component
public class CustomRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            // 在实践中这里可以是已授权的用户信息
            String user = request.getHeader("X-User");
            if (user != null) {
                template.header("X-User", user);
            }
            String canary = request.getHeader("X-Canary");
            if (canary != null) {
                template.header("X-Canary", canary);
            }
        }
    }
}
