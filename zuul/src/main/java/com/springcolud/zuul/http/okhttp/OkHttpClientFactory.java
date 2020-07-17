
package com.springcolud.zuul.http.okhttp;


import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * @program: rcs-commonkit
 * @description: 通过工厂方法获取okhttpclient实例
 * @author: WangJinBo
 * @create: 2019-12-12 15:02
 **/
@Service
public class OkHttpClientFactory extends AbstractFactoryBean<OkHttpClient> {
    private OkHttpClientConfig okHttpClientConfig=new OkHttpClientConfig();

    private boolean isSimple = true;

    /**
     * @Fields readTimeoutMilliSeconds : 读超时
     */
    private long readTimeoutMilliSeconds = 100000;

    /**
     * @Fields writeTimeout : 写超时
     */
    private long writeTimeout = 10000;

    /**
     * @Fields connectTimeout : 连接超时
     */
    private long connectTimeout = 15000;


    //=========以下为https相关参数,如果不请求https,或者要使用默认CA方式,可以不用设置==============

    /**
     * @Fields isUnSafe : 是否使用不安全的方式(不对证书做任何效验), 如果此参数为默认值,并且没有添加信人证书,则使用默认CA方式验证
     */
    boolean isUnSafe = false;

    /**
     * @Fields isCheckHostname : 是否验证域名/IP, 仅对添加自签证书为信任时生效
     */
    boolean isCheckHostname = true;

    /**
     * @Fields certificateFilePaths : 用含有服务端公钥的证书校验服务端证书(添加自签证书为信任证书)
     */
    private Resource[] certificateFilePaths;


    /**
     * @Fields pkcsFile : 使用 指定 PKCS12 证书加密解密数据(应对支付宝,微信支付等)
     */
    private String pkcsFile = null;

    /**
     * @Fields pkcsFilePwd : PKCS12 证书的密码
     */
    private String pkcsFilePwd = null;

    public OkHttpClient createOkHttpClient(){
        return createInstance();
    }
    @Override
    protected OkHttpClient createInstance() {
        if(isSimple){
            return new OkHttpClientBuilder().buildSimple(okHttpClientConfig);
        }else{
            return new OkHttpClientBuilder(readTimeoutMilliSeconds, writeTimeout, connectTimeout, isUnSafe,
                    isCheckHostname, certificateFilePaths, pkcsFile, pkcsFilePwd).build();
        }
    }

    @Override
    public Class<?> getObjectType() {
        return OkHttpClient.class;
    }

    public void setReadTimeoutMilliSeconds(long readTimeoutMilliSeconds) {
        this.readTimeoutMilliSeconds = readTimeoutMilliSeconds;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setUnSafe(boolean isUnSafe) {
        this.isUnSafe = isUnSafe;
    }

    public void setCertificateFilePaths(Resource... certificateFilePaths) {
        this.certificateFilePaths = certificateFilePaths;
    }

    public void setPkcsFile(String pkcsFile) {
        this.pkcsFile = pkcsFile;
    }

    public void setPkcsFilePwd(String pkcsFilePwd) {
        this.pkcsFilePwd = pkcsFilePwd;
    }

    public void setCheckHostname(boolean isCheckHostname) {
        this.isCheckHostname = isCheckHostname;
    }

    public boolean isSimple() {
        return isSimple;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
    }

    public OkHttpClientConfig getOkHttpClientConfig() {
        return okHttpClientConfig;
    }

    public void setOkHttpClientConfig(OkHttpClientConfig okHttpClientConfig) {
        this.okHttpClientConfig = okHttpClientConfig;
    }
}
