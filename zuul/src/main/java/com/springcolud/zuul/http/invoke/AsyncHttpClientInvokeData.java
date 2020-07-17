package com.springcolud.zuul.http.invoke;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @program: rcs-commonkit
 * @description: http请求时所需提交的参数等数据
 * @author: WangJinBo
 * @create: 2019-12-12 13:13
 **/
@Data
public class AsyncHttpClientInvokeData<T> {
    private String channelCode;
    private String postData;
    private String paramINJson;
    private String paramINXml;
    private Map<String,Object> paramINMap;
    private String paramURL;
    private Map<String,String> paramHEAD;
    private String requestUrl;
    private AsyncHttpClientInvoker.MethodTypeEnums methodType;
    private AsyncHttpClientInvoker.ContentTypeEnums contentType;
    private List<T> files;
}
