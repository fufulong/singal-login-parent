package com.common.Interceptor;

import com.common.JwtUtil;
import com.common.pojo.JwtToken;
import com.common.pojo.UserRealm;
import com.sys.domain.SysUser;
import com.sys.service.SysUserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 自动登录拦截器
 */
@Component
public class AuthtizationInterceptor extends HandlerInterceptorAdapter {
    @Value("${demo.isOpenShiro}")
    private boolean isOpenShiro;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 重写springmvc接口之前的前置拦截,在测试环境下,没有登录的情况下,直接登录超级管理员的账号
     * 方便开发和测试,实际上线后,只要让 配置文件中 demo.isOpenShiro = false 就可以了
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean haveLogined = (SecurityUtils.getSubject().getPrincipal()!=null);
        //在测试环境下,没有登录的时候
        if (!isOpenShiro && !haveLogined){
            SysUser admin = sysUserService.selectById(UserRealm.ADMINE_ID);
            String token = jwtUtil.createJwt(admin, 1L, TimeUnit.DAYS);
            JwtToken jwtToken = new JwtToken(token);
            SecurityUtils.getSubject().login(jwtToken);
        }

        return super.preHandle(request, response, handler);
    }
}
