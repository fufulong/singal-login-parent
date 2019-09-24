package com.sys.VO;

import com.common.pojo.UserRealm;
import com.sys.domain.SysUser;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * 创建 用户表单对象,注意没有验
 */
@Data
public class SysUserCreateVO {
    /**
     * 姓名
     */
    @NotEmpty(message = "注册时候账号不能为空")
    @Length(message = "账号不能超过20个字符",max = 20)
    private String name;

    /**
     * 年龄
     */
    @Min(message = "年龄不能小于20岁",value = 20L)
    private Integer age;

    /**
     * 性别, 0女,1:男
     */
    private Integer sex;

    /**
     * 密码,注册的时候,传来的密码是经过前端加密的,之后再后端还要经过加密,才能存入数据库
     */
    @NotEmpty(message = "注册密码不能为空")
    private String loginPassword;

    /**
     * 根据创建表单对象,得到SysUser对象
     * @return SysUser对象
     */
    public SysUser getUser(){
        SysUser sysUser = new SysUser();
        // 先处理 和 SysUser的一样的属性
        BeanUtils.copyProperties(this,sysUser);
        //然后处理 加密盐值 和 加密后存进数据库的密码.注意这里的密码处理方式一定要和 Userrealm中的认证逻辑一致
        String salt = RandomStringUtils.randomAlphanumeric(10);
        String algorithmname = UserRealm.ALGORITHMNAME;
        Integer iterablecount = UserRealm.ITERABLECOUNT;
        String passWord = new SimpleHash(algorithmname,this.loginPassword,salt,iterablecount).toString();
        sysUser.setPassword(passWord);
        sysUser.setSalt(salt);
        return  sysUser;
    }
}
