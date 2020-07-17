package com.springcolud.zuul.http.apachehttp.callback;

import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * @program: rcs-commonkit
 * @description: http异步回调接口
 * @author: WangJinBo
 * @create: 2020-03-18 09:57
 **/
public interface IApacheHttpAsyncCallbackForResponse {

    void doCallback(CloseableHttpResponse response);
    
}
