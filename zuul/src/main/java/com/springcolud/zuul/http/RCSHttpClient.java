package com.springcolud.zuul.http;



import com.springcolud.zuul.http.apachehttp.ApacheHttpClient;
import com.springcolud.zuul.http.apachehttp.ApacheHttpClientConfig;
import com.springcolud.zuul.http.apachehttp.ApacheHttpClientFactory;
import com.springcolud.zuul.http.okhttp.HttpClientDestoryCallBack;
import com.springcolud.zuul.http.okhttp.OkHttpClient;
import com.springcolud.zuul.http.okhttp.OkHttpClientConfig;
import com.springcolud.zuul.http.okhttp.OkHttpClientFactory;
import com.springcolud.zuul.http.okhttp.callback.IAsyncCallback;
import com.springcolud.zuul.http.okhttp.upload.UploadFileBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @program: rcs-commonkit
 * @description:
 * @author: WangJinBo
 * @create: 2020-02-19 14:53
 **/
public class RCSHttpClient {


    private ApacheHttpClient apacheHttpClient;
    private OkHttpClient okHttpClient;

    private ApacheHttpClientConfig apacheHttpClientConfig;
    private OkHttpClientConfig okHttpClientConfig;

    public RCSHttpClient(ApacheHttpClientFactory apacheHttpClientFactory, ApacheHttpClientConfig apacheHttpClientConfig,
                         OkHttpClientFactory okHttpClientFactory, OkHttpClientConfig okHttpClientConfig){

        this.apacheHttpClientConfig=apacheHttpClientConfig;
        if(apacheHttpClientConfig!=null){
            this.apacheHttpClient = apacheHttpClientFactory.createApacheHttpClient(apacheHttpClientConfig);
        }else{
            this.apacheHttpClient=null;
        }

        this.okHttpClientConfig=okHttpClientConfig;
        okHttpClientFactory.setOkHttpClientConfig(okHttpClientConfig);
        if(okHttpClientConfig!=null){
            this.okHttpClient=okHttpClientFactory.createOkHttpClient();
        }else {
            this.okHttpClient=null;
        }
    }

    public AbstractRCSHttpClient getHttpClient(){
        return (apacheHttpClient!=null)?apacheHttpClient:okHttpClient;
    }
    /**
     * 关闭客户端
     */
    public void shutdownAndAwaitTermination(ExecutorService pool) {
        AbstractRCSHttpClient rcsHttpClient = getHttpClient();
        rcsHttpClient.shutdownAndAwaitTermination(pool);
    }

    /**
     * 销毁一个httpClient
     */
    public boolean destoryCallBack(HttpClientDestoryCallBack callBack, Long currentClientTimeStamp) {
        AbstractRCSHttpClient rcsHttpClient = getHttpClient();
        return rcsHttpClient.destoryCallBack(callBack,currentClientTimeStamp);
    }

    /**
     * 销毁一个httpClient
     */
    public boolean destroy() {
        AbstractRCSHttpClient rcsHttpClient = getHttpClient();
        return rcsHttpClient.destroy();
    }

    /**
     * get方式请求:url
     */
    public String get(String url, Map<String, String> headerExt) {
        AbstractRCSHttpClient rcsHttpClient = getHttpClient();
        return rcsHttpClient.get(url,headerExt);
    }

    /**
     * 提交:json或xml
     */
    public String post(String url, Map<String, Object> param, String xmlStr, Map<String, String> headerExt, String dataMediaType, String chatSet) {
        return apacheHttpClient.apachepost(url,param,xmlStr,headerExt,dataMediaType,chatSet);
    }
    /**
     * 提交:FileAndWWWForm
     */
    public <T extends UploadFileBase> String post(String url, Map<String, Object> params, Map<String, String> headerExt, List<T> files) {

        if(apacheHttpClient!=null){
            return apacheHttpClient.apachepost(url,params,headerExt,files);
        } else{
            return okHttpClient.okhttppost(url,objValueToString(params),headerExt,files);
        }
    }

    /**
     * 提交:WWWForm
     */
    public String post(String url, Map<String, Object> params, Map<String, String> headerExt, String dataMediaType) {
        if(apacheHttpClient!=null){
            return apacheHttpClient.apachepost(url,params,headerExt,dataMediaType);
        } else{
            return okHttpClient.okhttppost(url,objValueToString(params),headerExt,dataMediaType);
        }
    }

    /**
     * 提交:json或xml
     */
    public String post(String url,String postStr,Map<String, String> headerExt,String dataMediaType,String chatSet){
        return okHttpClient.okhttppost(url,postStr,headerExt,dataMediaType,chatSet);
    }

    /**
     * 异步回调方式提交:json或xml
     */
    public String post(String url,String postStr,Map<String, String> headerExt,String dataMediaType,String chatSet, IAsyncCallback callback){
        return okHttpClient.okhttppost(url,postStr,headerExt,dataMediaType,chatSet,callback);
    }

    private Map<String, String> objValueToString(Map<String, Object> params){
        Map<String, String> paramsstr = new HashMap<>();
        if(params!=null){
            for (Map.Entry<String, Object> param : params.entrySet()) {
                paramsstr.put(param.getKey(), param.getValue().toString());
            }
        }
        return paramsstr;
    }
}
