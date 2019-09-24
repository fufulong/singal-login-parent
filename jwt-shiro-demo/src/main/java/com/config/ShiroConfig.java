package com.config;

import com.common.filter.JwtFilter;
import com.common.pojo.UserRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.DefaultCookieSerializer;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * shiro的配置文件
 */
@Configuration
public class ShiroConfig {

    //是否开启shiro拦截
    @Value("${demo.shiro}")
    private boolean isOpenShiro;

    /**
     * 配置 cookie 的序列化器
     *
     * @return
     */
    @Bean
    public DefaultCookieSerializer myCookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookieName("shiro:demo");
        cookieSerializer.setUseHttpOnlyCookie(true);
        return cookieSerializer;
    }

    /**
     * 配置集群的 session 的 cookie 信息 转存进
     *
     * @param cookieSerializer
     * @return
     */
    @Bean
    public RedisHttpSessionConfiguration sessionConfiguration(DefaultCookieSerializer cookieSerializer) {
        RedisHttpSessionConfiguration sessionConfiguration = new RedisHttpSessionConfiguration();
        sessionConfiguration.setCookieSerializer(cookieSerializer);
        sessionConfiguration.setMaxInactiveIntervalInSeconds(1 * 24 * 60 * 60);
        return sessionConfiguration;
    }

    /**
     * 集群环境，session交给spring-session管理
     * 通过配置文件里面的 配置项 demo.cluster = true/false 来标记是否集群环境,
     *
     * @ConditionOnProperty 注解 就是 springBoot 中通过判断配置项的真假来决定是否
     * 向 Spring 容器内加入这个对象
     */
    @Bean
    @ConditionalOnProperty(prefix = "demo", name = "cluster", havingValue = "true")
    public ServletContainerSessionManager servletContainerSessionManager() {
        return new ServletContainerSessionManager();
    }

    /**
     * 配置 securityManager
     *
     * @param userRealm:自定义的认证器
     * @param servletContainerSessionManager: 上面配置的
     * @return
     */
    @Bean("securityManager")
    public SecurityManager securityManager(UserRealm userRealm, ServletContainerSessionManager servletContainerSessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(servletContainerSessionManager);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    /**
     * 配置是否开启 shiro 的 开关
     *
     * @param securityManager:上面配置的
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "demo", name = "shiro", havingValue = "true")
    public AuthorizationAttributeSourceAdvisor sourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor sourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        sourceAdvisor.setSecurityManager(securityManager);
        return sourceAdvisor;
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        //设置没有认证就访问资源的跳转页面地址
        factoryBean.setUnauthorizedUrl("/");
        // 设置登录的页面地址
//        factoryBean.setLoginUrl("xxxx");

        factoryBean.setSecurityManager(securityManager);
        // 设置 登录成功的 跳转的页面地址.
//        factoryBean.setSuccessUrl("xxx");
        // 设置 不需要经过验证过滤的地址,和需要验证过滤的地址
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/swagger/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/swagger-resources/**", "anon");

        filterMap.put("/statics/**", "anon");
        filterMap.put("/login.html", "anon");

        // 设置 注册,登录 和登出 3个接口 直接通过,不用进过shiro 的拦截器
        filterMap.put("/sys/user/login", "anon");
        filterMap.put("/sys/user/register", "anon");
        filterMap.put("/sys/user/loginOut", "anon");
        filterMap.put("/favicon.ico", "anon");

        //配置自己的拦截器,并定义 key = "jwt"
        Map<String, Filter> jwtfilter = new HashMap<String, Filter>(1);
        jwtfilter.put("jwt", new JwtFilter());
        factoryBean.setFilters(jwtfilter);

        // 过滤链从上往下依次进行,所以 /** 的都放在最后面.这里不用 shiro 自带的 key = "anoc"的过滤器,用自己定义的
        filterMap.put("/**", isOpenShiro ? "jwt" : "anon");
        factoryBean.setFilterChainDefinitionMap(filterMap);
        return factoryBean;

    }

}
