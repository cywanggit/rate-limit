package com.springcolud.zuul.http.invoke;

import com.springcolud.zuul.http.apachehttp.ApacheAsyncHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.springframework.stereotype.Service;

/**
 * @program: rcs-commonkit
 * @description: http请求类，调用okHttpClient执行http调用
 * @author: WangJinBo
 * @create: 2019-08-21 18:20
 **/
@Service
public class AsyncHttpClientInvoker {

    /**
     * 执行通道调用，并返回通道数据
     * */
    public void invoke(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback) {
        httpClientInvokeData.getMethodType().submit(httpClient, httpClientInvokeData,context,callback);
    }

    //请求方法类型post、get
    public enum MethodTypeEnums{
        /***/
        GET{
            @Override
            public void submit(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback) {
                httpClientInvokeData.getContentType().get(httpClient, httpClientInvokeData,context,callback);
            }
        },POST{
            @Override
            public void submit(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback) {
                httpClientInvokeData.getContentType().submit(httpClient, httpClientInvokeData,context,callback);
            }
        };
        public abstract void submit(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback);
        public static MethodTypeEnums valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }
    //数据提交方式
    public enum ContentTypeEnums{
        //提交form表单数据
        WWWForm{
            @Override
            public void submit(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback) {
                httpClient.apachepost(httpClientInvokeData.getRequestUrl(), httpClientInvokeData.getParamINMap(), httpClientInvokeData.getParamHEAD(),"application/x-www-form-urlencoded",context,callback);
            }
        },
        //同时提交文件和form表单数据
        FileAndWWWForm{
            @Override
            public void submit(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback) {
                httpClient.apachepost(httpClientInvokeData.getRequestUrl(), httpClientInvokeData.getParamINMap(), httpClientInvokeData.getParamHEAD(), httpClientInvokeData.getFiles(),context,callback);
            }
        },
        //提交json格式数据
        Json{
            @Override
            public void submit(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback) {
                httpClient.apachepost(httpClientInvokeData.getRequestUrl(), httpClientInvokeData.getParamINMap(), httpClientInvokeData.getParamINJson(), httpClientInvokeData.getParamHEAD(),"application/json","charset=UTF-8",context,callback);
            }
        },
        //提交xml格式数据
        XML{
            @Override
            public void submit(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback) {
                httpClient.apachepost(httpClientInvokeData.getRequestUrl(),null, httpClientInvokeData.getParamINXml(), httpClientInvokeData.getParamHEAD(),"application/xml","charset=UTF-8",context,callback);
            }
        },
        /**提交text/plain*/
        TEXTPLAIN{
            @Override
            public void submit(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback) {
                httpClient.apachepost(httpClientInvokeData.getRequestUrl(), httpClientInvokeData.getPostData(), httpClientInvokeData.getParamHEAD(),context,callback);
            }
        };
        public void get(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback){
            httpClient.get(httpClientInvokeData.getRequestUrl(), httpClientInvokeData.getParamHEAD(),context,callback);
        }
        public abstract void submit(ApacheAsyncHttpClient httpClient, AsyncHttpClientInvokeData httpClientInvokeData, HttpClientContext context, FutureCallback<HttpResponse> callback);
        public static ContentTypeEnums valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }
}
