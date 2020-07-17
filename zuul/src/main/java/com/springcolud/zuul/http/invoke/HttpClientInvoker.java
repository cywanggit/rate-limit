package com.springcolud.zuul.http.invoke;

import com.springcolud.zuul.http.apachehttp.ApacheHttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.springframework.stereotype.Service;

/**
 * @program: rcs-commonkit
 * @description: http请求类，调用okHttpClient执行http调用
 * @author: WangJinBo
 * @create: 2019-08-21 18:20
 **/
@Service
public class HttpClientInvoker {

    /**
     * 执行通道调用，并返回通道数据
     * */
    public String invoke(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context) {
        return httpClientInvokeData.getMethodType().submit(httpClient, httpClientInvokeData,context);
    }


    //请求方法类型post、get
    public enum MethodTypeEnums{
        /***/
        GET{
            @Override
            public String submit(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context) {
                return httpClientInvokeData.getContentType().get(httpClient, httpClientInvokeData,context);
            }
        },POST{
            @Override
            public String submit(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context) {
                return httpClientInvokeData.getContentType().submit(httpClient, httpClientInvokeData,context);
            }
        };
        public abstract String submit(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context);
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
            public String submit(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context) {
                return httpClient.apachepost(httpClientInvokeData.getRequestUrl(), httpClientInvokeData.getParamINMap(), httpClientInvokeData.getParamHEAD(),"application/x-www-form-urlencoded",context);
            }
        },
        //同时提交文件和form表单数据
        FileAndWWWForm{
            @Override
            public String submit(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context) {
                return httpClient.apachepost(httpClientInvokeData.getRequestUrl(), httpClientInvokeData.getParamINMap(), httpClientInvokeData.getParamHEAD(), httpClientInvokeData.getFiles(),context);
            }
        },
        //提交json格式数据
        Json{
            @Override
            public String submit(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context) {
                return httpClient.apachepost(httpClientInvokeData.getRequestUrl(), httpClientInvokeData.getParamINMap(), httpClientInvokeData.getParamINJson(), httpClientInvokeData.getParamHEAD(),"application/json","charset=UTF-8",context);
            }
        },
        //提交xml格式数据
        XML{
            @Override
            public String submit(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context) {
                return httpClient.apachepost(httpClientInvokeData.getRequestUrl(),null, httpClientInvokeData.getParamINXml(), httpClientInvokeData.getParamHEAD(),"application/xml","charset=UTF-8",context);
            }
        },
        /**提交text/plain*/
        TEXTPLAIN{
            @Override
            public String submit(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context) {
                return httpClient.apachepost(httpClientInvokeData.getRequestUrl(), httpClientInvokeData.getPostData(), httpClientInvokeData.getParamHEAD(),"text/plain","charset=UTF-8");
            }
        };
        public String get(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context){
            String resultData = httpClient.get(httpClientInvokeData.getRequestUrl(), httpClientInvokeData.getParamHEAD(),context);
            return resultData;
        }
        public abstract String submit(ApacheHttpClient httpClient, HttpClientInvokeData httpClientInvokeData, HttpClientContext context);
        public static ContentTypeEnums valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }
}
