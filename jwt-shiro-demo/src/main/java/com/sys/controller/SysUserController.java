package com.sys.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.common.JwtUtil;
import com.common.R;
import com.common.pojo.JwtToken;
import com.common.pojo.UserRealm;
import com.sys.VO.SysUserCreateVO;
import com.sys.dao.SysUserMapper;
import com.sys.domain.SysUser;
import com.sys.service.SysUserService;
import io.jsonwebtoken.Jwt;
import io.swagger.annotations.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ffl
 * @since 2019-09-19
 */
@Api(tags = {"用户接口"},consumes = "application/json",produces = "application/json")
@RestController
@RequestMapping("/api/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 登录
     *
     * @param userName:登录的用户名
     * @param password:登录的密码
     * @return
     */
    @ApiOperation(value ="登录接口")
    @GetMapping(value = "/login")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name ="userName",value = "登录用户名"),
            @ApiImplicitParam(name ="password",value="登录密码")
    })
    public R login(@RequestParam String userName, @RequestParam String password) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return R.error("登录用户名或者密码不能为空");
        }

        if (SecurityUtils.getSubject().getPrincipal() != null) {
            return R.error("已经登录,不要重复登录");
        }

        SysUser user = sysUserService.selectByUserName(userName);
        if (user == null) {
            throw new UnknownAccountException("账号不存在");
        }

        // 账号和密码进过验证匹配之后,调用Shiro 的方法,将登录信息缓存进shiro
        SysUser loginuser = new SysUser();
        loginuser.setId(user.getId());
        loginuser.setName(userName);
        //注意这里放进 jwt 的是登录的原始密码,不要放 存在数据库的密码
        loginuser.setPassword(password);
        String tokenStr = jwtUtil.createJwt(loginuser, 1L, TimeUnit.DAYS);
        JwtToken token = new JwtToken(tokenStr);
        /*
            下面的方法,配置 Shiro的 securityManager.setRealm(userRealm) 之后,就会走
            AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            方法的逻辑.验证不通过的话会抛出异常
         */
        SecurityUtils.getSubject().login(token);
        // 登录成功之后,创建token,返回token信息给调用者,默认登录之后,保存一天的登录信息

        HashMap<String, Object> map = new HashMap<>();
        map.put("token", tokenStr);
        map.put("lastLoginTime", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));

        return R.ok("登录成功").put(map);
    }

    /**
     * 注册的方法
     *
     * @param createVO
     * @return
     */
    @ApiOperation(value ="注册接口")
    @PostMapping(value = "/register")
    public R register(@RequestBody @Valid @ApiParam(value = "注册信息表单") SysUserCreateVO createVO, BindingResult bindingResult) {
        String errMessage = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .distinct()
                .collect(Collectors.joining(",  "));
        if (!StringUtils.isEmpty(errMessage)) {
            throw new RuntimeException(errMessage);
        }
        sysUserService.insert(createVO.parseToUser());
        return R.ok("注册成功");
    }

    /**
     * 用户退出登录的接口
     *
     * @param userId:退出登录的用户的id
     * @return
     */

    @ApiOperation(value ="登出接口")
    @GetMapping(value = "/loginOut/{userId}")
    public R loginOut(@PathVariable @ApiParam(value ="登出的用户id") Integer userId) {
        if (userId == null || userId <= 0) {
            throw new RuntimeException("退出登录的用户名不能为null");
        }

        SysUser loginUser = (SysUser) (SecurityUtils.getSubject().getPrincipal());
        if (loginUser == null) {
            throw new RuntimeException("登录之后没有操作太久,登录信息已经失效或者已经登出,不要重复操作");
        }

        SecurityUtils.getSubject().logout();
        // 记得清除 redis 中缓存的 token
        redisTemplate.delete(JwtUtil.AUTH_TOKEN_KEY_PREFIX + String.valueOf(userId));
        return R.ok("登出成功");

    }

   @RequiresPermissions(value = "sys:user:selectByName")
    @ApiOperation(value ="根据用户名查找用户")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "name")
        }
    )
    @GetMapping(value = "/selectByName/{name}")
    public R selectByName(@PathVariable String name) {
        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException("查找的用户名不能为空");
        }
        EntityWrapper<SysUser> wrapper = new EntityWrapper<>();
        wrapper.like("name", name);
        List<SysUser> sysUsers = sysUserService.selectList(wrapper);
        return R.ok("按照用户模糊名  " + name + "  查询成功").put(sysUsers);

    }

    @ApiOperation("查找全部的用户")
    @GetMapping(value = "/selectAll")
    public R selectAll() {
        System.out.println("测试无权限限制方法");

        List<SysUser> users = sysUserService.selectAll();
        users.stream().forEach(item -> {
            System.out.println(item);
        });

        return R.ok("测试成功").put(users);
    }

}
