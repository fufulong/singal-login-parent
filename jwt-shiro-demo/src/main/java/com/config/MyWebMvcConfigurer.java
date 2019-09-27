package com.config;

import com.common.Interceptor.AuthtizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    @Autowired
    private AuthtizationInterceptor authtizationInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决静态资源无法访问（可选）
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        // 直接在浏览器访问：根目录/swagger-ui.html
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // 需要用到的webjars（包含js、css等）
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

    /**
     * 重写 springmvc的拦截器库,加上自己定义的拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authtizationInterceptor)
                .addPathPatterns("/api/**")
                //排除不需要经过 shiro 控制的 swagger 相关的url和登录路径和注册路径
                .excludePathPatterns("/swagger-ui.html")
                .excludePathPatterns("/v2/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/configuration/**")
                .excludePathPatterns("/swagger/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/api/sys/user/login")
                .excludePathPatterns("/api/sys/user/register");

    }
}
