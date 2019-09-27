package com.common.annotation;

import java.lang.annotation.*;

/**
 *验证权限需要的注解
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface NeedPermissions {
    //需要的权限表示字符串
    String[] value() default {""};
    //各权限之间默认是 与关系,各个权限之间的逻辑关系值,0:与,1:或,2:非
    int loginValue() default 0;

}
