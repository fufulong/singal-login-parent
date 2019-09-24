package com.common;

/**
 * @ClassName ResultCode
 * @Author hzr
 * @Date 2018/7/26 14:59
 * @Version 1.0
 **/
public class ResultCode {

    public static final int OK = 200; //操作成功

    public static final int LATEST_VERSION = 201; //当前客户端最新版本

    public static final int NO_CONTENT = 204; //无内容

    public static final int ERROR_PARAM_CODE = 400; // Bad Request – 调用不合法，确切的错误应该在error payload中描述，例如：“JSON 不合法 ”

    public static final int ERROR_AUTH_CODE = 401; // 未认证，调用需要用户通过认证

    public static final int USER_NOT_FOUND = 402; //用户不存在

    public static final int PASSWORD_ERROR = 403; //密码输入错误

    public static final int INVALID_MOBILE_NUMBER = 404; //错误手机号码

    public static final int INVALID_PASS_WORD = 405; //密码格式非法

    public static final int NOT_FOUND = 406; //  请求资源未找到

    public static final int REQUEST_REJECT = 407; //  服务被拒绝
    public static final int UPLOAD_FAILED = 408; //  上传文件失败
    public static final int DAMAGE_FILE = 409; //  文件已经被损坏
    public static final int FILE_TOO_LARGE = 410; //  文件过大
    public static final int FROZEN_USER = 411; //  用户被冻结
    public static final int DUPLICATE_MOBILE_NUMBER = 412; //  号码重复注册
    public static final int UNKNOWN_BUSINESS_ID = 413; //  未知的业务ID

    public static final int INTERNAL_SERVER_ERROR = 500; //  服务器内部错误
    public static final int CHECK_VERSION_FAILED = 501; //  更新检查失败
    public static final int NETWORK_AUTHENTICATION_REQUIRED = 511; //  Network Authentication Required

    public static final int USER_REPEAT_LOGIN = 512; //  用户重复登录


    public static final int ERROR_NOT_FOUND_CODE = 504; // 未找到，指定的资源不存在


}
