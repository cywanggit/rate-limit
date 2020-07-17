package com.springcolud.zuul.http.okhttp.exception;



import com.springcolud.zuul.http.okhttp.enums.CommonDataResultCodeEnum;

import java.io.Serializable;

/**
 * @program: rcs-commonkit
 * @description: 调用okHttpClient发生异常时，需要抛出的异常类
 * @author: WangJinBo
 * @create: 2019-12-12 13:42
 **/
public class OkHttpClientException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -6437002196224360953L;
    private CommonDataResultCodeEnum rcEnum;//异常对应枚举

    private String detailMsg = "";//详细信息

    /**
     * 可替换变量,相当于占位符
     */
    private String[] variables = null;

    /**
     * 使用错误码定义BusinessException
     */
    public OkHttpClientException(CommonDataResultCodeEnum rcEnum) {
        super(rcEnum.getMessage());
        this.rcEnum= rcEnum;
    }

    public OkHttpClientException(CommonDataResultCodeEnum rcEnum, String detailmsg) {
        super(rcEnum.getMessage());
        this.rcEnum = rcEnum;
        this.detailMsg = detailmsg;
    }


    /**
     * 使用错误码、默认提示、提示信息的变量定义BusinessException
     * @param variables    用于在提示信息中展示的参数
     */
    public OkHttpClientException(CommonDataResultCodeEnum rcEnum, String[] variables) {
        super(rcEnum.getMessage());
        this.rcEnum = rcEnum;
        this.variables = (variables == null ? null : variables.clone());
    }

    public CommonDataResultCodeEnum getRcEnum() {
        return rcEnum;
    }

    public String getDetailMsg() {
        return detailMsg;
    }

    /**
     * 异常堆栈增加错误代码和绑定变量
     *
     * @return 错误信息
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("errorCode[").append(this.rcEnum.getCode()).append("]");
        if (variables != null && variables.length != 0) {
            sb.append("variables[");
            for (int i = 0; i < variables.length; i++) {
                sb.append(variables[i]);
            }
            sb.append("]\n");
        }
        sb.append(super.toString());
        return sb.toString();
    }
}
