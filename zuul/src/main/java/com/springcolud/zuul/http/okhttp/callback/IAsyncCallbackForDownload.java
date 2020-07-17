package com.springcolud.zuul.http.okhttp.callback;

/**
 * @program: rcs-commonkit
 * @description: 调用okHttpClient时，如果需要异步回调处理流数据，实现该接口
 * @author: WangJinBo
 * @create: 2019-12-12 13:42
 **/
@FunctionalInterface
public interface IAsyncCallbackForDownload {

    /**
     * 异步回调接口的执行方法,传入 okhttp3.Response
     */
    public void doCallback(byte[] response);
    
}
