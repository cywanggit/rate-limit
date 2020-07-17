package com.springcolud.zuul.http.apachehttp;

import lombok.Data;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.springframework.core.io.Resource;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @program: rcs-commonkit
 * @description: ApacheHttpClient实例配置类。使用时，不设置属性则采用默认的。
 * 否则，需要用户手动调用set方法设置ApacheHttpClient实例的连接属性。
 * @author: WangJinBo
 * @create: 2020-02-18 09:58
 **/
@Data
public class ApacheHttpClientConfig {

    //客户端从服务端读取数据的超时时间
    private int socketTimeout = 35 * 1000;

    //客户端与服务器建立连接的超时时间
    private int connectTimeout = 35 * 1000;

    //客户端从连接池中获取连接的超时时间
    private int connectionRequestTimeout = 15 * 1000;

    //路由的默认最大连接
    private int defaultMaxPerRoute = 201;

    //整个连接池连接的最大值
    private int maxTotal = 1001;

    //连接闲置2分钟后需要重新检测
    /*@Value("${http.validateAfterInactivity:120000}")
    private int validateAfterInactivity;*/

    //utf8
    private Charset DEFAULT_CHARTSET = ContentType.APPLICATION_JSON.getCharset();

    private RequestConfig defaultRequestConfig;

    /*********以下为https相关参数,如果不请求https,或者要使用默认CA方式,可以不用设置************/
    /**isUnSafe : 是否使用不安全的方式(不对证书做任何效验)，默认不使用安全连接*/
    private boolean unSafe=true;

    /**isCheckHostname : 是否验证域名/IP, 仅对添加自签证书为信任时生效*/
    private boolean isCheckHostname=false;

    /**certificateFilePaths : 用含有服务端公钥的证书校验服务端证书(添加自签证书为信任证书)*/
    private List<URL> certificateFilePathList;

    private Resource[] certificateFilePathsArr;

    /**pkcsFile : 使用 指定 PKCS12 证书加密解密数据(应对支付宝,微信支付等)*/
    private String pkcsFile;

    /**pkcsFilePwd : PKCS12 证书的密码*/
    private String pkcsFilePwd;

    /**==================================================================================*/
    public ApacheHttpClientConfig(){
        //   设置获取连接超时时间、建立连接超时时间、从服务端读取数据的超时时间
        defaultRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout).build();
    }

}
