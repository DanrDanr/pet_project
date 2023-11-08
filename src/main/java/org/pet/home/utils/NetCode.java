package org.pet.home.utils;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/27
 **/
public class NetCode {

    /**
     * 创建失败
     * “0x10”是错误代码的十六进制表示形式
     */
    public static final int CREATE_DEPARTMENT_ERROR = 0x10;


    /**
     * 移除部门失败
     */
    public static final int REMOVE_DEPARTMENT_ERROR = 0x11;

    /**
     * 更新部门失败
     */
    public static final int UPDATE_DEPARTMENT_ERROR = 0x12;

    /**
     * 手机号无效的错误
     */
    public static final int PHONE_INVALID = 0x13;
    /**
     * 邮箱无效的错误
     */
    public static final int EMAIL_NULL = 0X14;

    public static final int OPERATE_ERROR = 0X15;
    /**
     * 部门id异常
     */
    public static final int DEPARTMENT_ID_INVALID = 0X16;
    /**
     * 手机号被使用
     */
    public static final int PHONE_OCCUPANCY = 0X17;
    /**
     * 删除员工失败
     */
    public static final int REMOVE_EMPLOYEE_ERROR = 0x18;

    public static final int SHOP_NAME_NULL = 0x19;
    public static final int ADDRESS_NULL = 0X20;
    public static final int LOGO_NULL = 0X21;
    public static final int REMOVE_SHOP_ERROR = 0x22;
    public static final int USER_PASSWORD_NULL = 0X23;
    public static final int PHONE_NULL = 0X24;
    public static final int USERNAME_NULL = 0X25;

    public static final int PHONE_OCCUPATION = 0X26;
    public static final int TOKEN_LAPSE = 0X27;
    public static final int TOKEN_INVALID = 0X28;
}
