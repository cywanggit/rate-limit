package com.springcolud.zuul.http.apachehttp;

import org.springframework.stereotype.Service;

/**
 * @program: rcs-commonkit
 * @description:
 * @author: WangJinBo
 * @create: 2020-02-18 09:58
 **/
@Service
public class ApacheHttpClientFactory {

    public ApacheHttpClient createApacheHttpClient(ApacheHttpClientConfig httpClientConfig){
        if(httpClientConfig.isUnSafe()){
            return new ApacheHttpClientBuilder().buidlSimple(httpClientConfig);
        }else{
            return new ApacheHttpClientBuilder().buildSSL(httpClientConfig);
        }
    }

    /**
     * 异步调用客户端，目前只支持非证书，不支持证书签名安全模式
     * */
    public ApacheAsyncHttpClient createApacheAsyncHttpClient(ApacheHttpClientConfig httpClientConfig){
        if(httpClientConfig.isUnSafe()){
            return new ApacheHttpClientBuilder().buidlSimpleAsync(httpClientConfig);
        }else{
            return null;
        }
    }
}
