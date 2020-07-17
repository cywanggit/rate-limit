package com.springcolud.zuul.http.apachehttp;

import com.alibaba.fastjson.JSON;

import com.springcolud.zuul.http.AbstractRCSHttpClient;
import com.springcolud.zuul.http.okhttp.HttpClientDestoryCallBack;
import com.springcolud.zuul.http.okhttp.upload.UploadFile;
import com.springcolud.zuul.http.okhttp.upload.UploadFileBase;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @program: rcs-commonkit
 * @description: 对Apache的HttpClient做了一层封装。
 * @author: WangJinBo
 * @create: 2020-02-18 09:57
 **/
@Data
public class ApacheHttpClient extends AbstractRCSHttpClient {
    private final static Logger logger = LoggerFactory.getLogger(ApacheHttpClient.class);
    private CloseableHttpClient httpClient;
    private ApacheHttpClientConfig httpClientConfig;
    PoolingHttpClientConnectionManager httpClientPoolManager;
    //用于监控空闲的连接池连接
    private IdleConnectionMonitorThread idleConnectionMonitor = null;
    //创建监控线程时的同步锁
    private final static Object syncLock = new Object();

    public ApacheHttpClient(ApacheHttpClientConfig httpClientConfig, CloseableHttpClient httpClient) {
        this.httpClientConfig = httpClientConfig;
        this.httpClient = httpClient;
    }

    /**启动监视器线程*/
    public void startIdleConnectionMonitorThread(PoolingHttpClientConnectionManager connManager){
        if (idleConnectionMonitor == null) {
            synchronized (syncLock) {
                if (idleConnectionMonitor == null) {
                    idleConnectionMonitor = new IdleConnectionMonitorThread(connManager);
                    idleConnectionMonitor.start();
                }
            }
        }
    }

    /**关闭监视器线程*/
    public void shutDownIdleConnectionMonitorThread(){
        if (idleConnectionMonitor != null) {
            synchronized (syncLock) {
                if (idleConnectionMonitor != null) {
                    idleConnectionMonitor.shutdown();
                }
            }
        }
    }
    /**
     * 对于空闲连接，使用一个专用的监视线程来驱逐由于长时间不活动而被认为过期的连接。
     * 监视器线程可以定期调用ClientConnectionManager#closeExpiredConnections()方法来关闭所有过期的连接，并从池中驱逐关闭的连接。
     * 还可以选择性地调用ClientConnectionManager#closeIdleConnections()方法来关闭在给定时间内空闲的所有连接。
     * */
    private final class IdleConnectionMonitorThread extends Thread {
        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        /*扫描间隔时间（毫秒）*/
        private static final int MONITOR_INTERVAL_MS = 100;
        /*空闲时间阈值（毫秒）*/
        private static final int IDLE_ALIVE_MS = 10000;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
            this.shutdown = false;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(MONITOR_INTERVAL_MS);
                        // 关闭无效的连接
                        connMgr.closeExpiredConnections();
                        // 关闭空闲时间超过IDLE_ALIVE_MS的连接
                        connMgr.closeIdleConnections(IDLE_ALIVE_MS, TimeUnit.MILLISECONDS);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 关闭后台连接
        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    private RequestBuilder buildRequestBuilder(String method, String url, Map<String, String> headers) {
        boolean isGet = HttpMethod.GET.name().equalsIgnoreCase(method);
        RequestBuilder requestBuilder = isGet ? RequestBuilder.get(url) : RequestBuilder.post(url);
        requestBuilder.setCharset(httpClientConfig.getDEFAULT_CHARTSET());
        addRequestHeaders(headers, requestBuilder);
        return requestBuilder;
    }

    /**
     * 关闭，并等待关闭完成。(暂不实现)
     */
    @Override
    public void shutdownAndAwaitTermination(ExecutorService pool) {
    }

    /**
     * 销毁一个httpClient后需要执行的回调(暂不实现)
     */
    @Override
    public boolean destoryCallBack(HttpClientDestoryCallBack callBack, Long currentClientTimeStamp) {
        return true;
    }

    /**
     * 销毁一个httpClient。
     */
    @Override
    public boolean destroy() {
        //关闭连接池监控线程
        this.shutDownIdleConnectionMonitorThread();
        //销毁httpclient连接池
        this.httpClientPoolManager.shutdown();
        return true;
    }

    /**
     * get方式请求:url
     */
    @Override
    public String get(String url, Map<String, String> headerExt) {
        CommonParams commonParams = CommonParams.builder().url(url).method("get").contentType(ContentType.APPLICATION_FORM_URLENCODED).build();
        return doGet(commonParams,headerExt,null);
    }

    /**
     * get方式请求:url
     */
    public String get(String url, Map<String, String> headerExt, HttpClientContext context) {
        CommonParams commonParams = CommonParams.builder().url(url).method("get").contentType(ContentType.APPLICATION_FORM_URLENCODED).build();
        return doGet(commonParams,headerExt,context);
    }
    /**
     * 提交:textplain
     */
    @Override
    public String apachepost(String url, String postData, Map<String, String> headerExt, String dataMediaType, String chatSet) {
        return this.postTextPlain(url,postData,headerExt,null);
    }
    /**
     * 提交:json或xml
     */
    @Override
    public String apachepost(String url, Map<String, Object> param, String xmlStr, Map<String, String> headerExt, String dataMediaType, String chatSet) {
        String res = null;
        if (dataMediaType.equals("application/json")) {
            res = postJson(url, param, headerExt,null);
        }

        if (dataMediaType.equals("application/xml")) {
            res = postXml(url, xmlStr, headerExt,null);
        }
        return res;
    }
    public String apachepost(String url, Map<String, Object> param, String xmlStr, Map<String, String> headerExt, String dataMediaType, String chatSet, HttpClientContext context) {
        String res = null;
        if (dataMediaType.equals("application/json")) {
            res = postJson(url, param, headerExt,context);
        }

        if (dataMediaType.equals("application/xml")) {
            res = postXml(url, xmlStr, headerExt,context);
        }
        return res;
    }

    /**
     * 提交:FileAndWWWForm
     */
    @Override
    public <T extends UploadFileBase> String apachepost(String url, Map<String, Object> params, Map<String, String> headerExt, List<T> files) {
        return postFileAndWWWForm(url, params, headerExt, files,null);
    }
    public <T extends UploadFileBase> String apachepost(String url, Map<String, Object> params, Map<String, String> headerExt, List<T> files, HttpClientContext context) {
        return postFileAndWWWForm(url, params, headerExt, files,context);
    }
    /**
     * 提交:WWWForm
     */
    @Override
    public String apachepost(String url, Map<String, Object> params, Map<String, String> headerExt, String dataMediaType) {
        return postWWWForm(url, params, headerExt,null);
    }
    public String apachepost(String url, Map<String, Object> params, Map<String, String> headerExt, String dataMediaType, HttpClientContext context) {
        return postWWWForm(url, params, headerExt,context);
    }

    private String doGet(CommonParams commonParams, Map<String, String> headerExt, HttpClientContext context){
        RequestBuilder requestBuilder = buildRequestBuilder(commonParams.getMethod(), commonParams.getUrl(), headerExt);
        addParameter(commonParams,null,null, requestBuilder);
        CloseableHttpResponse response = null;
        String resData = null;
        try {
            if(context!=null){
                response = httpClient.execute(requestBuilder.build(),context);
            }else{
                response = httpClient.execute(requestBuilder.build());
            }
            if (response!=null && response.getStatusLine().getStatusCode() == 200) {
                resData = EntityUtils.toString(response.getEntity(), httpClientConfig.getDEFAULT_CHARTSET());
                /*logger.info("返回信息: {}", resData);*/
            }
        } catch (Exception e) {
            logger.error("执行apache http调用发生异常！", e);
        }finally {
            if(response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    logger.warn("释放apache http连接资源时发生异常！", e);
                }
            }
        }
        return resData;
    }

    private String doPost(CommonParams commonParams, Map<String, Object> params, String postData, Map<String, String> headers, HttpClientContext context) {
        RequestBuilder requestBuilder = buildRequestBuilder(commonParams.getMethod(), commonParams.getUrl(), headers);
        addParameter(commonParams, params, postData, requestBuilder);
        logger.info("apache client logger isDebugEnabled={}",logger.isDebugEnabled());
        if(logger.isDebugEnabled()){
            logger.info("apache client:"+commonParams.getUrl());
            logger.info("apache client post params:{}", JSON.toJSONString(params));
        }
        CloseableHttpResponse response = null;
        String resData = null;
        try {
            if(context!=null){
                response = httpClient.execute(requestBuilder.build(),context);
            }else{
                response = httpClient.execute(requestBuilder.build());
            }

            if (response!=null && response.getStatusLine().getStatusCode() == 200) {
                resData = EntityUtils.toString(response.getEntity(), httpClientConfig.getDEFAULT_CHARTSET());
                //这里要关闭InputStream，否则连接不会被复用
                response.getEntity().getContent().close();
                /*logger.info("返回信息: {}", resData);*/
            }
        } catch (Exception e) {
            logger.error("执行apache http调用发生异常！", e);
        } finally {
            if(response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    logger.warn("释放apache http连接资源时发生异常！", e);
                }
            }
        }
        return resData;
    }

    /**
     * 提交text/plain
     */
    private String postTextPlain(String url, String postData, Map<String, String> headerExt, HttpClientContext context) {
        CommonParams commonParams = CommonParams.builder().url(url).method("post")
                .contentType(ContentType.TEXT_PLAIN).build();
        return doPost(commonParams, null, postData, headerExt,context);
    }

    /**
     * 提交json
     */
    private String postJson(String url, Map<String, Object> param, Map<String, String> headerExt, HttpClientContext context) {
        CommonParams commonParams = CommonParams.builder().url(url).method("post")
                .contentType(ContentType.APPLICATION_JSON).build();
        return doPost(commonParams, param, null, headerExt,context);
    }

    /**
     * 提交xml
     */
    private String postXml(String url, String xmlStr, Map<String, String> headerExt, HttpClientContext context) {
        CommonParams commonParams = CommonParams.builder().url(url).method("post")
                .contentType(ContentType.APPLICATION_XML).build();
        return doPost(commonParams, null, xmlStr, headerExt,context);
    }

    /**
     * 提交Form表单和文件
     */
    private <T extends UploadFileBase> String postFileAndWWWForm(String url, Map<String, Object> params, Map<String, String> headerExt, List<T> files, HttpClientContext context) {
        UploadFile file = (UploadFile) files.get(0);
        CommonParams commonParams = CommonParams.builder().url(url).method("post")
                .contentType(ContentType.MULTIPART_FORM_DATA)
                .fileParamKey("file").filePath(file.getFile().getAbsolutePath()).build();
        return doPost(commonParams, params, null, headerExt,context);
    }

    /**
     * 提交Form表单
     */
    private String postWWWForm(String url, Map<String, Object> params, Map<String, String> headerExt, HttpClientContext context) {
        CommonParams commonParams = CommonParams.builder().url(url).method("post")
                .contentType(ContentType.APPLICATION_FORM_URLENCODED).build();
        return doPost(commonParams, params, null, headerExt,context);
    }

    private void addParameter(CommonParams commonParams, Map<String, Object> params, String postData, RequestBuilder requestBuilder) {
        //提交xml
        if (commonParams.getContentType() == ContentType.APPLICATION_XML) {
            requestBuilder.addHeader("Content-Type", "application/xml");
            StringEntity entity = new StringEntity(postData, ContentType.create("text/xml", httpClientConfig.getDEFAULT_CHARTSET()));
            requestBuilder.setEntity(entity);
            return;
        }
        //提交text/plain
        if(commonParams.getContentType() == ContentType.TEXT_PLAIN){
            requestBuilder.addHeader("Content-Type","text/plain");
            StringEntity entity = new StringEntity(postData, ContentType.create("text/plain", httpClientConfig.getDEFAULT_CHARTSET()));
            requestBuilder.setEntity(entity);
        }
        if (!StringUtils.isEmpty(params)) {
            //提交json
            if (commonParams.getContentType() == ContentType.APPLICATION_JSON) {
                requestBuilder.addHeader("Content-Type", "application/json");
                StringEntity entity = new StringEntity(JSON.toJSONString(params), commonParams.getContentType());
                requestBuilder.setEntity(entity);
            }
            //提交form表单+文件
            else if (commonParams.getContentType() == ContentType.MULTIPART_FORM_DATA) {
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                multipartEntityBuilder.setCharset(httpClientConfig.getDEFAULT_CHARTSET());
                File file = new File(commonParams.getFilePath());
                multipartEntityBuilder.addBinaryBody(commonParams.getFileParamKey(), file);
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    multipartEntityBuilder.addTextBody(param.getKey(), param.getValue().toString());
                }
                HttpEntity entity = multipartEntityBuilder.build();
                requestBuilder.setEntity(entity);
            }
            //提交form表单
            else {
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    requestBuilder.addParameter(param.getKey(), param.getValue().toString());
                }
            }

        }

    }

    private static void setPostParams(HttpPost httpost,
                                      Map<String, Object> params) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void addRequestHeaders(Map<String, String> headers, RequestBuilder requestBuilder) {
        if (null != headers && headers.size() > 0) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (!("Content-Length".equalsIgnoreCase(header.getKey()) || "content-type".equalsIgnoreCase(header.getKey()))) {
                    requestBuilder.addHeader(header.getKey(), header.getValue().toString());
                }
            }
        }
    }
}
