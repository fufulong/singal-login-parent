package com.config;

import com.common.filter.JwtFilter;
import com.common.pojo.UserRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
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
    @Value("${demo.isOpenShiro}")
    private boolean isOpenShiro ;

    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost("127.0.0.1:6379");
        return redisManager;
    }

    //todo

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
    @ConditionalOnProperty(prefix = "demo" , name = "isCluster", havingValue = "true")
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
    @ConditionalOnProperty(prefix = "demo", name = "isOpenShiro", havingValue = "true")
    public AuthorizationAttributeSourceAdvisor sourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor sourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        sourceAdvisor.setSecurityManager(securityManager);
        return sourceAdvisor;
    }
/*

    下面配置了2个Bean 也不能支持 @RequirePermissions("vvvvv")注解,不知道怎么弄
    *//**
     * 下面两个 Bean 是为了开启 @RequirePermissions(value ="xxxx,xxx")有用.
     * 否则需要在每个需要权限的方法里面手动加上验证逻辑 SecurityUtils.getsubject().checkPermissions(xxxx),
     * 才会调用 UserRealm的 AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)方法之后再
     * 调用 AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) 验证授权逻辑
     * @return
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }



    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        //设置没有认证就访问资源的跳转页面地址
//        factoryBean.setUnauthorizedUrl("/");
        // 设置登录的页面地址
//        factoryBean.setLoginUrl("xxxx");

        factoryBean.setSecurityManager(securityManager);
        // 设置 登录成功的 跳转的页面地址.
//        factoryBean.setSuccessUrl("xxx");
        // 设置 不需要经过验证过滤的地址,和需要验证过滤的地址
        Map<String, String> filterMap = new LinkedHashMap<>();
    //为了swagger2的接口文档页面能够正常显示,通过shiro 必需设置下面的路径通过
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/v2/**", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/configuration/security", "anon");
        filterMap.put("/configuration/ui", "anon");


        filterMap.put("/statics/**", "anon");
        filterMap.put("/login.html", "anon");

        // 设置 注册,登录 和登出 3个接口 直接通过,不用进过shiro 的拦截器
        filterMap.put("/api/sys/user/login", "anon");
        filterMap.put("/api/sys/user/register", "anon");
        filterMap.put("/api/sys/user/loginOut", "anon");

        //配置自己的拦截器,并定义 key = "jwt"
        Map<String, Filter> jwtfilter = new HashMap<String, Filter>(1);
        jwtfilter.put("JWT", new JwtFilter());
        factoryBean.setFilters(jwtfilter);

        // 过滤链从上往下依次进行,所以 /** 的都放在最后面.这里不用 shiro 自带的 key = "anoc"的过滤器,用自己定义的
        filterMap.put("/api/**", isOpenShiro ? "JWT" : "anon");
        System.out.println("isOpenShiro =" + isOpenShiro + ",size = " + jwtfilter.size());
        factoryBean.setFilterChainDefinitionMap(filterMap);
        return factoryBean;

    }

}
