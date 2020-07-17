package com.springcolud.zuul.http.apachehttp;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * @program: rcs-commonkit
 * @description: 负责创建Apache的Httpclient。对外提供2个方法，分别返回非SSL认证、SSL认证的ApacheHttpClient实例。
 * @author: WangJinBo
 * @create: 2020-02-18 09:58
 **/
public class ApacheHttpClientBuilder {

    public ApacheHttpClient buidlSimple(ApacheHttpClientConfig httpClientConfig){
        PoolingHttpClientConnectionManager poolManager = this.createPoolingHttpClientConnectionManager(httpClientConfig);
        CloseableHttpClient httpClient = this.standardClient(httpClientConfig,poolManager);
        ApacheHttpClient apacheHttpClient = new ApacheHttpClient(httpClientConfig,httpClient);
        apacheHttpClient.setHttpClientPoolManager(poolManager);
        //启动httpclient资源回收监控线程
        apacheHttpClient.startIdleConnectionMonitorThread(poolManager);
        return apacheHttpClient;
    }

    public ApacheAsyncHttpClient buidlSimpleAsync(ApacheHttpClientConfig httpClientConfig){
        PoolingNHttpClientConnectionManager poolManager = this.createPoolingNHttpClientConnectionManager(httpClientConfig);
        CloseableHttpAsyncClient httpClient = this.standardAsyncClient(httpClientConfig,poolManager);
        ApacheAsyncHttpClient apacheHttpClient = new ApacheAsyncHttpClient(httpClientConfig,httpClient);
        apacheHttpClient.setHttpClientPoolManager(poolManager);
        //启动httpclient资源回收监控线程
        apacheHttpClient.startIdleConnectionMonitorThread(poolManager);
        return apacheHttpClient;
    }

    public ApacheHttpClient buildSSL(ApacheHttpClientConfig httpClientConfig) {
        CloseableHttpClient httpClient = this.sslClient(httpClientConfig);
        ApacheHttpClient apacheHttpClient = new ApacheHttpClient(httpClientConfig,httpClient);
        apacheHttpClient.setHttpClientPoolManager(createPoolingHttpClientConnectionManager(httpClientConfig));
        return apacheHttpClient;
    }

    /**
     *  创建一个非SSL安全的HttpClient
     * */
    private CloseableHttpClient standardClient(ApacheHttpClientConfig config, PoolingHttpClientConnectionManager connManager) {
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = createHttpRequestRetryHandler();

        return HttpClients.custom()
                .setConnectionManager(connManager)
                .setRetryHandler(httpRequestRetryHandler)
                .setDefaultRequestConfig(config.getDefaultRequestConfig())
                .build();
    }

    /**
     *  创建一个非SSL安全的HttpClient
     * */
    private CloseableHttpAsyncClient standardAsyncClient(ApacheHttpClientConfig config,PoolingNHttpClientConnectionManager connManager) {
        CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(config.getDefaultRequestConfig())
                .build();
        client.start();
        return client;
    }

    /**
     * 在调用SSL之前需要重写验证方法，取消检测SSL
     * 创建ConnectionManager，添加Connection配置信息
     *
     * @return RCSHttpClient 支持https
     */
    private CloseableHttpClient sslClient(ApacheHttpClientConfig config) {
        try {
            // 在调用SSL之前需要重写验证方法，取消检测SSL
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String str) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String str) {
                }
            };

            // 请求重试处理
            HttpRequestRetryHandler httpRequestRetryHandler = createHttpRequestRetryHandler();

            SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            ctx.init(null, new TrustManager[]{trustManager}, null);
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
            // 创建Registry
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(config.getConnectionRequestTimeout())
                    .setConnectTimeout(config.getConnectTimeout())
                    .setSocketTimeout(config.getSocketTimeout())
                    .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                    .setExpectContinueEnabled(Boolean.TRUE).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", socketFactory).build();
            // 创建ConnectionManager，添加Connection配置信息
            PoolingHttpClientConnectionManager connManager = createPoolingHttpClientConnectionManager(config);
            CloseableHttpClient closeableHttpClient = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .setRetryHandler(httpRequestRetryHandler)
                    .setDefaultRequestConfig(requestConfig).build();
            return closeableHttpClient;
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     *  根据配置参数创建一个连接管理器
     * */
    private PoolingNHttpClientConnectionManager createPoolingNHttpClientConnectionManager(ApacheHttpClientConfig config){

        //配置io线程
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
                setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setSoKeepAlive(true)
                .build();
        //设置连接池大小
        ConnectingIOReactor ioReactor=null;
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            e.printStackTrace();
        }

        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        connManager.setMaxTotal(config.getMaxTotal());
        //设置
        connManager.setDefaultMaxPerRoute(config.getDefaultMaxPerRoute());
        return connManager;
    }

    /**
     *  根据配置参数创建一个连接管理器
     * */
    private PoolingHttpClientConnectionManager createPoolingHttpClientConnectionManager(ApacheHttpClientConfig config){
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(config.getMaxTotal());
        //设置
        connManager.setDefaultMaxPerRoute(config.getDefaultMaxPerRoute());
        /*这里要确定是否使用永久检查的连接后，再放开。
        另外参照官方建议，弄清楚是否需要关闭
        （http://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/connmgmt.html#d5e418）*/
        /*connManager.setValidateAfterInactivity(config.getValidateAfterInactivity());*/
        return connManager;
    }

    private HttpRequestRetryHandler createHttpRequestRetryHandler(){
        return new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= 3) {// 如果已经重试了3次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
    }
}
