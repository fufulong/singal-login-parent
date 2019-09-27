package com.common;

import com.sys.domain.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 关于jwt的util,生成 token,解析token,校验token中保存的密码是否和用户的密码一致
 *
 * @author fufulong
 * @since 2019-09-20
 */
@Component
public class JwtUtil {
    @Autowired
    private HashOperations<String, String, Object> hashOperations;
    @Autowired
    private ValueOperations<String,Object> valueOperations;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 设置 auth:token:${userId} 的默认过期时间
     */
    private static final Long DEFAULT_AUTHTOKEN_EXPAIRE = 24 * 60 * 60L;

    public static final String AUTH_TOKEN_KEY_PREFIX = "auth:token:";

    /**
     * 根据已经注册过的登录成功的用户对象,和过期时间,创建token,并把token
     * 保存进 redis, key = auth:token:${userId},value 就是token 字符串
     * 因为 jwt自己的过期时间不太好使,所以这里使用redis的过期时间
     *
     * @param user    :登录成功的用户
     * @param expire: redis 中token 的过期时间,如果设置成负数,表示不过期
     * @param unit:   过期时间单位
     * @return  token: 成功登陆后,创建的JWT token 字符串
     */
    public String createJwt(SysUser user, Long expire, TimeUnit unit) {
        if (user == null || user.getId() == null || user.getId() < 1) {
            throw new RuntimeException("创建JWT的时候,传入user不可为null");
        }

        // 创建 map claim ,保存 用户的 id,用户名 和密码
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("id", user.getId());
        claim.put("username", user.getName());
        claim.put("password", user.getPassword());

        JwtBuilder builder = Jwts.builder()
                //这里设置的信息如果在标准信息之后申明,会覆盖标准信息声明
                .setClaims(claim)
                //代表jwt的主体,就是创建这个jwt的主体,只能存字符串,可以使 userId ,userInfoStr之类的
                .setSubject(user.getName())
                //jwt的唯一标识,必须保证每个jwt的唯一标识唯一,主要是为了避免重放攻击
                .setId(UUID.randomUUID().toString())
                //jwt的创建时间
                .setIssuedAt(new Date())
                //生成jwt的加密方式,私有秘钥,私有秘钥一定不能被用户知道,不然用户就可以自我签发jwt了
                .signWith(SignatureAlgorithm.HS256, "ffl-fufulong");
        String token = builder.compact();
        String key = AUTH_TOKEN_KEY_PREFIX + String.valueOf(user.getId());
        valueOperations.set(key,token);
        if (expire == null || expire < 0){
           redisTemplate.expire(key,DEFAULT_AUTHTOKEN_EXPAIRE,unit);
        }else{
            redisTemplate.expire(key,expire,unit);
        }

        return token;
    }


    /**
     * 根据 toKen 和 输入的用户信息解析
     * @param token: 登录之后,会返回一个token 给前段保存,前端在访问需要登录权限的
     * 接口的时候,必需把 toKen放进 请求头 authitication
     * @return claim:私有声明对象
     */
    public Claims parssToken(String token){
        if (StringUtils.isEmpty(token) ){
            throw new RuntimeException("解析token的参数不可以为null");
        }

        Claims claims = Jwts.parser().setSigningKey("ffl-fufulong").parseClaimsJws(token).getBody();
        return claims;
    }

    /**
     * 判断 token 中的保存的登录这的私有信息是否和 user中一致
     * @param token
     * @param user
     * @return
     */
    public Boolean verify(String token ,SysUser user){
        if (StringUtils.isEmpty(token) || user == null){
            throw new RuntimeException("参数不可以为null");
        }
        Claims claims = this.parssToken(token);
        Integer tokenId =(Integer) claims.get("id");
        String tokenuserName =(String) claims.get("username");
        String tokenpassword = (String) claims.get("passWord");
        if (tokenuserName.equals(user.getName()) && tokenpassword.equals(user.getPassword())){
            return  true;
        }else{
            return false;
        }

    }

    /**
     * 默认一些用户的token的有效期是1天,可能当用户选择记住我的时候,前端可以刷新token 的过期时间,
     * 一般建议1个星期等,然后对记住我的用户,直接根据用户id,查询得到redis中的token
     * @param user:登录用户
     * @param expire:过期时间
     * @param timeUnit:时间单位
     */
    public void refreshToken(SysUser user ,Long expire,TimeUnit timeUnit){
        String key = AUTH_TOKEN_KEY_PREFIX + String.valueOf(user.getId());
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey){
            redisTemplate.expire(key,expire,timeUnit);
        }else{
            this.createJwt(user,expire,timeUnit);
        }
    }

}
