package com.springcolud.zuul.http.apachehttp.callback;

/**
 * @program: rcs-commonkit
 * @description: http异步回调接口
 * @author: WangJinBo
 * @create: 2020-03-18 09:57
 **/
public interface IApacheHttpAsyncCallbackForDownload {

    void doCallback(byte[] response);
    
}
