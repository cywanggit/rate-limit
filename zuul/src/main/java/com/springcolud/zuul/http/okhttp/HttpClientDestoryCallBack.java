package com.springcolud.zuul.http.okhttp;

/**
 * @program: ws-cdms
 * @description: 客户端销毁后的回调
 * @author: WangJinBo
 * @create: 2019-09-06 13:49
 **/
public interface HttpClientDestoryCallBack {
    void remove(Long currentClientTimeStamp);
}
