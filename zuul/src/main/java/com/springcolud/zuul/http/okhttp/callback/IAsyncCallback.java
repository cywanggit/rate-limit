package com.springcolud.zuul.http.okhttp.callback;

/**
 * @program: rcs-commonkit
 * @description: 调用okHttpClient时，如果需要异步回调，实现该接口
 * @author: WangJinBo
 * @create: 2019-12-12 13:42
 **/

@FunctionalInterface
public interface IAsyncCallback {

    /**
     * 异步回调接口的执行方法
     */
    void doCallback(String responseBody);
    
}
