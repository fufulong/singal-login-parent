package com.common.exception;

import com.common.R;
import com.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    @ExceptionHandler(RuntimeException.class)
    public R handleRRException(RuntimeException e) {
        R r = new R();
        r.put("code", 500);
        r.put("msg", e.getMessage());

        return r;
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        log.error(e.getMessage(), e);
        return R.error();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public R handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return R.error("数据库中已存在该记录");
    }

    @ExceptionHandler(AuthorizationException.class)
    public R handleAuthorizationException(AuthorizationException e) {
        log.error(e.getMessage(), e);
        if (SecurityUtils.getSubject().getPrincipal() == null) {
            return R.error(ResultCode.ERROR_AUTH_CODE, "授权过期，请重新登录");
        }
        return R.error("没有权限，请联系管理员授权");
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public R handleUnauthenticatedException(UnauthenticatedException e) {
        log.error(e.getMessage(), e);
        if (SecurityUtils.getSubject().getPrincipal() == null) {
            return R.error(ResultCode.ERROR_AUTH_CODE, "授权过期，请重新登录");
        }
        return R.error("没有权限，请联系管理员授权");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R handleHttpMessageNotReadableException(Exception e) {
        log.error(e.getMessage(), e);
        return R.error(400, "参数格式不正确，无法读取");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        if (e.getRequiredType().getName().equals("java.lang.Integer")) {
            return R.error(ResultCode.ERROR_PARAM_CODE, "输入错误，【" + e.getValue() + "】不是整型");
        }
        return R.error(ResultCode.ERROR_PARAM_CODE, "参数类型错误");
    }

    /**
     * 处理@Validate实体校验不通过异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R validationError(MethodArgumentNotValidException ex) {
        return R.error(HttpStatus.BAD_REQUEST.value(), ex.getBindingResult().getAllErrors().stream().map(
                DefaultMessageSourceResolvable::getDefaultMessage).distinct().collect(Collectors.joining(" 、 ")));
    }


}
