package com.springcolud.zuul.http.okhttp.enums;

/**
 * @program: rcs-commonkit
 * @description: 默认的返回结果枚举类（包含：返回代码、提示信息）
 * @author: WangJinBo
 * @create: 2019-12-12 13:42
 **/
public enum CommonDataResultCodeEnum {

    /***************通用枚举*********************/
    /** 200 成功  */
    RESULT_SUCCESS("200","请求成功"),

    /** 400 参数  */
    RESULT_ERROR_PARM("400","参数缺失"),
    RESULT_ERROR_PARM_PAGE("400","超过最大限制条数"),
    RESULT_ERROR_MANAGER_TYPE("400","参数错误"),
    RESULT_ERROR_COMPANY_KEY("400","companyKey格式错误"),
    RESULT_ERROR_NOTFOUND("404","路径不存在，请检查路径是否正确"),
    /** 500 系统错误  */
    RESULT_ERROR_SYS("500","系统异常"),
    RESULT_ERROR_DUPLICATE_KEY("501","数据库中已存在该记录"),
    /**权限*/
    RESULT_PERMISSION_NO("300","没有权限，请联系管理员授权"),



    /*************其他业务分类*****************/
    /***************************************/
    /** 兜底的。上面增加配置项就不用分号了  */
    RESULT_NO_UNUSED("cl008","我是卧底");

    private String code;
    private String message;

    CommonDataResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString(){
        return code+":"+message;
    }
}
