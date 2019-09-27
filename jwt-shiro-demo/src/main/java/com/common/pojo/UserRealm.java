package com.common.pojo;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.common.JwtUtil;
import com.sys.domain.SysPermission;
import com.sys.domain.SysUser;
import com.sys.service.SysPermissionService;
import com.sys.service.SysUserService;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义的shiro 的认证和授权逻辑
 *
 * @author fufulong
 * @since 2019-09-20
 */
@Component
public class UserRealm extends AuthorizingRealm {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private JwtUtil jwtUtil;
    // 密码经过算法加密的次数
    public static final Integer ITERABLECOUNT = 10;

    public static final String ALGORITHMNAME = "SHA-256";

    public static final Integer ADMINE_ID = 1;

    /**
     * 修改支持的 登录信息来源,从表单来源改成 jwt token 来源,不写这个后面的操作会报错
     *
     * @param token: 登录信息 token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }


    /**
     * 执行授权的方法
     *
     * @param principalCollection: 已经登录并保存进 shiro 缓存的 subject
     * @return AuthorizationInfo: 可以包含权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 增加权限查找,缓存到 shiro的逻辑,注意这里我们登录的时候,保存的账号是 String jwtToken,密码也是 String jwtToken
        //需要解析一下
        String token = (String) principalCollection.getPrimaryPrincipal();
        Claims claims = jwtUtil.parssToken(token);
        Integer userid = (Integer) claims.get("id");
        Set<String> permisssions = new HashSet<>(10);
        //如果是超级管理员,直接让他有所有的权限
        if (userid.equals(ADMINE_ID)) {
            permisssions = permissionService.selectList(null).stream()
                    .map(SysPermission::getName)
                    .collect(Collectors.toSet());
        }else{
            permisssions = sysUserService.selectPermisssions(userid);
        }
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        if (permisssions == null || permisssions.isEmpty()) {
            System.out.println("userid = " + userid + "的用户没有权限信息");
        }
        authorizationInfo.setStringPermissions(permisssions);
        return authorizationInfo;
    }

    /**
     * 认证逻辑方法
     *
     * @param token: shiro 接受到的登录信息
     * @return AuthenticationInfo:
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) token;
        Claims claims = jwtUtil.parssToken(jwtToken.getToken());
        Integer loginId = (Integer) claims.get("id");
        String username = claims.get("username", String.class);
        String loginpassword = claims.get("password", String.class);
        EntityWrapper<SysUser> wrapper = new EntityWrapper<>();
        wrapper.eq("name", username);
        SysUser sysUser = sysUserService.selectOne(wrapper);
        if (sysUser == null) {
            throw new UnknownAccountException("账户名不存在");
        }
        loginpassword = new SimpleHash(ALGORITHMNAME, loginpassword, sysUser.getSalt(), ITERABLECOUNT).toString();
        if (!loginpassword.equals(sysUser.getPassword())) {
            throw new IncorrectCredentialsException("密码错误");
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), ByteSource.Util.bytes(sysUser.getSalt()), this.getName());
        return authenticationInfo;
    }
}
