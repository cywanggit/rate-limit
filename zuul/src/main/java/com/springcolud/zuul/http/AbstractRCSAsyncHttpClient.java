package com.springcolud.zuul.http;

import com.springcolud.zuul.http.apachehttp.callback.IApacheHttpAsyncCallback;
import com.springcolud.zuul.http.okhttp.upload.UploadFileBase;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public abstract class AbstractRCSAsyncHttpClient {
   /**
    * 关闭客户端
    */
    public abstract void shutdownAndAwaitTermination(ExecutorService pool);

   /**
    * 销毁一个httpClient
    */
   public abstract boolean destoryCallBack(IApacheHttpAsyncCallback callBack, Long currentClientTimeStamp);

   /**
    * 销毁一个httpClient
    */
   public abstract boolean destroy() throws IOException;

   /**
    * get方式请求:url
    */
   public abstract void get(String url, Map<String, String> headerExt, FutureCallback<HttpResponse> callback);

    /**
     * 提交:textplain
     */
    public abstract void apachepost(String url, String postData, Map<String, String> headerExt, HttpClientContext context, FutureCallback<HttpResponse> callback);

    /**
    * 提交:json或xml
    */
   public void apachepost(String url, Map<String, Object> param, String xmlStr, Map<String, String> headerExt, String dataMediaType, String chatSet, FutureCallback<HttpResponse> callback){}


   /**
    * 提交:FileAndWWWForm
    */
   public <T extends UploadFileBase> void apachepost(String url, Map<String, Object> params, Map<String, String> headerExt, List<T> files, FutureCallback<HttpResponse> callback){}

   /**
    * 提交:WWWForm
    */
   public void apachepost(String url, Map<String, Object> params, Map<String, String> headerExt, String dataMediaType, FutureCallback<HttpResponse> callback){}

    /**
     * 提交:json或xml
     */
    public void okhttppost(String url,String postStr,Map<String, String> headerExt,String dataMediaType,String chatSet, FutureCallback<HttpResponse> callback){}

    /**
     * 异步回调方式提交:json或xml
     */
    public void okhttppost(String url,String postStr,Map<String, String> headerExt,String dataMediaType,String chatSet, IApacheHttpAsyncCallback callback, FutureCallback<HttpResponse> futureCallback){}

    /**
     * 提交:FileAndWWWForm
     */
    public <T extends UploadFileBase> void okhttppost(String url, Map<String, String> params, Map<String, String> headerExt, List<T> files, FutureCallback<HttpResponse> callback){}

    /**
     * 提交:WWWForm
     */
    public void okhttppost(String url, Map<String, String> params, Map<String, String> headerExt, String dataMediaType, FutureCallback<HttpResponse> callback){}
}
