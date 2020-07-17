package com.springcolud.zuul.http;

import com.springcolud.zuul.http.okhttp.HttpClientDestoryCallBack;
import com.springcolud.zuul.http.okhttp.callback.IAsyncCallback;
import com.springcolud.zuul.http.okhttp.upload.UploadFileBase;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public abstract class AbstractRCSHttpClient {
   /**
    * 关闭客户端
    */
    public abstract void shutdownAndAwaitTermination(ExecutorService pool);

   /**
    * 销毁一个httpClient
    */
   public abstract boolean destoryCallBack(HttpClientDestoryCallBack callBack, Long currentClientTimeStamp);

   /**
    * 销毁一个httpClient
    */
   public abstract boolean destroy();

   /**
    * get方式请求:url
    */
   public abstract String get(String url, Map<String, String> headerExt);
    /**
     * 提交:textplain
     */
    public String apachepost(String url, String postData, Map<String, String> headerExt, String dataMediaType, String chatSet){return null;}
   /**
    * 提交:json或xml
    */
   public String apachepost(String url, Map<String, Object> param, String xmlStr, Map<String, String> headerExt, String dataMediaType, String chatSet){return null;}


   /**
    * 提交:FileAndWWWForm
    */
   public <T extends UploadFileBase> String apachepost(String url, Map<String, Object> params, Map<String, String> headerExt, List<T> files){return null;}

   /**
    * 提交:WWWForm
    */
   public String apachepost(String url, Map<String, Object> params, Map<String, String> headerExt, String dataMediaType){return null;}

    /**
     * 提交:json或xml
     */
    public String okhttppost(String url,String postStr,Map<String, String> headerExt,String dataMediaType,String chatSet){return null;}

    /**
     * 异步回调方式提交:json或xml
     */
    public String okhttppost(String url,String postStr,Map<String, String> headerExt,String dataMediaType,String chatSet, IAsyncCallback callback){return null;}

    /**
     * 提交:FileAndWWWForm
     */
    public <T extends UploadFileBase> String okhttppost(String url, Map<String, String> params, Map<String, String> headerExt, List<T> files){return null;}

    /**
     * 提交:WWWForm
     */
    public String okhttppost(String url, Map<String, String> params, Map<String, String> headerExt, String dataMediaType){return null;}
}
