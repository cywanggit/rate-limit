package com.springcolud.zuul.http;


import com.springcolud.zuul.http.apachehttp.ApacheHttpClientConfig;
import com.springcolud.zuul.http.apachehttp.ApacheHttpClientFactory;
import com.springcolud.zuul.http.okhttp.OkHttpClientConfig;
import com.springcolud.zuul.http.okhttp.OkHttpClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: rcs-commonkit
 * @description: 通过RCSHttpClientFactory工厂类，返回RCSHttpClient，这是一个http调用的代理类，提供了目前项目中使用的get/post方法。
 * 使用：
 * 使用apacheHttpClient时，构造函数OkHttpClientConfig传null；
 * 使用okHttpClient时，构造函数ApacheHttpClientConfig传null；
 * 在RCSHttpClient中post方法里的Map<String, Object> param，value为Object，如果传的是字符串，直接放字符串，如果是对象直接放对象。
 * 例如：
 * //===================================================================================================================
 * //声明工厂类
 * @Autowired
 * private RCSHttpClientFactory httpClientFactory;
 *
 * //创建RCSHttpClient实例
 * ApacheHttpClientConfig httpClientConfig = new ApacheHttpClientConfig();
 * //表示使用ApacheHttpClient
 * RCSHttpClient rcsHttpClient = httpClientFactory.createRCSHttpClient(httpClientConfig,null);
 *
 * OkHttpClientConfig okHttpClientConfig = new OkHttpClientConfig();
 * //表示使用OkHttpClientConfig
 * RCSHttpClient rcsHttpClient = httpClientFactory.createRCSHttpClient(null,okHttpClientConfig);
 * //===================================================================================================================
 * @author: WangJinBo
 * @create: 2020-02-19 09:52
 **/
@Service
public class RCSHttpClientFactory {
    @Autowired
    private ApacheHttpClientFactory apacheHttpClientFactory;
    @Autowired
    private OkHttpClientFactory okHttpClientFactory;

    public RCSHttpClient createRCSHttpClient(ApacheHttpClientConfig apacheHttpClientConfig, OkHttpClientConfig okHttpClientConfig){
        return new RCSHttpClient(apacheHttpClientFactory,apacheHttpClientConfig,okHttpClientFactory,okHttpClientConfig);
    }

}
