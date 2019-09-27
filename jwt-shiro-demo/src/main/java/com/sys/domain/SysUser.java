package com.sys.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;



import com.baomidou.mybatisplus.annotations.Version;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * <p>
 * 
 * </p>
 *
 * @author ffl
 * @since 2019-09-19
 */
@Data
@Accessors(chain = true)
@ToString
@TableName("sys_user")
public class SysUser extends Model<SysUser> implements  Serializable {

    private static final long serialVersionUID =Integer.valueOf(RandomStringUtils.randomNumeric(15)) ;

    /**
     * 用户信息表的id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别, 0女,1:男
     */
    private Integer sex;

    /**
     * 密码,注册的时候,传来的密码是经过加密的
     */
    @TableField(value = "password")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String password;
    /**
     * 加密注册时候密码字符串用的盐
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String salt;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
