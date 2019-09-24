package com.common.pojo;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * 创建一个实现了 AuthenticationToken的JwtToken类,是用来把 shiro的验证token
 * 变成 验证jwt
 */
@Data
public class JwtToken implements AuthenticationToken {
    private String token;

    public JwtToken(String token){
        this.token  = token;
    }

    /**
     * 重写得到用户名册方法
     * @return
     */
    @Override
    public Object getPrincipal() {
        return this.token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
