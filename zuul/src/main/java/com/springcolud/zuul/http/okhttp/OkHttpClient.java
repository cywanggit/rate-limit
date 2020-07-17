package com.springcolud.zuul.http.okhttp;


import com.springcolud.zuul.http.AbstractRCSHttpClient;
import com.springcolud.zuul.http.okhttp.callback.IAsyncCallback;
import com.springcolud.zuul.http.okhttp.callback.IAsyncCallbackForDownload;
import com.springcolud.zuul.http.okhttp.callback.IAsyncCallbackForResponse;
import com.springcolud.zuul.http.okhttp.enums.CommonDataResultCodeEnum;
import com.springcolud.zuul.http.okhttp.exception.OkHttpClientException;
import com.springcolud.zuul.http.okhttp.upload.UploadByteFile;
import com.springcolud.zuul.http.okhttp.upload.UploadFile;
import com.springcolud.zuul.http.okhttp.upload.UploadFileBase;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @program: rcs-commonkit
 * @description: OkHttpClient实例对象，包含了客户端的post、get方法以及客户端的销毁、重置（当修改通道配置后需要重启通道时）。
 * @author: WangJinBo
 * @create: 2019-12-12 15:02
 **/
@Component
public final class OkHttpClient extends AbstractRCSHttpClient {

    private final static Logger logger = LoggerFactory.getLogger(OkHttpClient.class);
    private OkHttpClientConfig okHttpClientConfig = new OkHttpClientConfig();
    private CacheControl cacheControl;

    private okhttp3.OkHttpClient okHttpClient;

    private CacheControl initCacheControl(){
        switch (okHttpClientConfig.getCacheType()){
            case OkHttpCacheType.MAXAGE:
                return new CacheControl.Builder().maxAge(okHttpClientConfig.getMaxAge(), TimeUnit.MILLISECONDS).build();
            case OkHttpCacheType.NOCACHE:
                return new CacheControl.Builder().noCache().build();
            default:
                return new CacheControl.Builder().noStore().build();
        }
    }
    OkHttpClient() {

        cacheControl = initCacheControl();
        okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder();
        builder.connectTimeout(okHttpClientConfig.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(okHttpClientConfig.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(okHttpClientConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
                .followRedirects(okHttpClientConfig.isFollowRedirects())
                .followSslRedirects(okHttpClientConfig.isFollowSslRedirects())
                .connectionPool(new ConnectionPool(okHttpClientConfig.getMaxIdleConnections(), okHttpClientConfig.getKeepAliveDuration(), TimeUnit.MILLISECONDS))
                .pingInterval(okHttpClientConfig.getPingInterval(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(okHttpClientConfig.isRetryOnConnectionFailure());
        okHttpClient = builder.build();
    }

    OkHttpClient(OkHttpClientConfig okHttpClientConfig) {
        cacheControl = initCacheControl();
        okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder();
        builder.connectTimeout(okHttpClientConfig.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(okHttpClientConfig.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(okHttpClientConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
                .followRedirects(okHttpClientConfig.isFollowRedirects())
                .followSslRedirects(okHttpClientConfig.isFollowSslRedirects())
                .connectionPool(new ConnectionPool(okHttpClientConfig.getMaxIdleConnections(), okHttpClientConfig.getKeepAliveDuration(), TimeUnit.MILLISECONDS))
                .pingInterval(okHttpClientConfig.getPingInterval(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(okHttpClientConfig.isRetryOnConnectionFailure());
        okHttpClient = builder.build();
        logger.info("初始化okHttpClient：keep_alive={}、max_idle_conn={}",okHttpClientConfig.getKeepAliveDuration(),okHttpClientConfig.getMaxIdleConnections());
    }


    OkHttpClient(long readTimeout, long writeTimeout, long connectTimeout, HttpsUtils.SSLParams sslParams) {
        cacheControl = initCacheControl();

        okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder();
        builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);

        // sslParams 如果为null只是不设置证书相关的参数,而使用默认的CA认证方式
        if (sslParams != null) {
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            if (sslParams.hostnameVerifier != null) {
                builder.hostnameVerifier(sslParams.hostnameVerifier);
            }
        }
        okHttpClient = builder.build();
    }

    /**
     * 关闭客户端
     * */
    @Override
    public void shutdownAndAwaitTermination(ExecutorService pool) {
        //关闭dispatcher,禁止提交新任务
        pool.shutdown();
        try {
            //等待现有任务终止
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                //取消当前正在执行的任务
                pool.shutdownNow();
                //等待任务响应被取消
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)){
                    logger.info("无法取消正在执行的任务");
                    throw new OkHttpClientException(CommonDataResultCodeEnum.RESULT_ERROR_SYS,"无法取消正在执行的任务！");
                }
            }
        } catch (InterruptedException ie) {
            //如果当前线程也中断，则取消
            pool.shutdownNow();
            //保存中断状态
            Thread.currentThread().interrupt();
        }
    }
    /**
     * 销毁一个OkHttpClient
     * */
    @Override
    public boolean destoryCallBack(HttpClientDestoryCallBack callBack, Long currentClientTimeStamp){
        try {
            //关闭客户端，不再接受新的任务
            shutdownAndAwaitTermination(okHttpClient.dispatcher().executorService());
        } catch (Exception e) {
            e.printStackTrace();
            throw new OkHttpClientException(CommonDataResultCodeEnum.RESULT_ERROR_SYS,"关闭client-dispatcher失败！");
        }

        try {
            //释放连接池中的空闲连接
            okHttpClient.connectionPool().evictAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new OkHttpClientException(CommonDataResultCodeEnum.RESULT_ERROR_SYS,"关闭client-connectionPool失败！");
        }
        try {
            //关闭客户端的缓存，并释放资源
            okHttpClient.cache().close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new OkHttpClientException(CommonDataResultCodeEnum.RESULT_ERROR_SYS,"关闭client-cache失败！");
        }
        okHttpClient=null;
        callBack.remove(currentClientTimeStamp);
        return true;
    }

    /**
     * 销毁一个OkHttpClient
     * */
    @Override
    public boolean destroy(){
        try {
            //关闭客户端，不再接受新的任务
            shutdownAndAwaitTermination(okHttpClient.dispatcher().executorService());
        } catch (Exception e) {
            e.printStackTrace();
            throw new OkHttpClientException(CommonDataResultCodeEnum.RESULT_ERROR_SYS,"关闭client-dispatcher失败！");
        }

        try {
            //释放连接池中的空闲连接
            okHttpClient.connectionPool().evictAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new OkHttpClientException(CommonDataResultCodeEnum.RESULT_ERROR_SYS,"关闭client-connectionPool失败！");
        }
        try {
            //关闭客户端的缓存，并释放资源
            if(okHttpClient.cache()!=null){
                okHttpClient.cache().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OkHttpClientException(CommonDataResultCodeEnum.RESULT_ERROR_SYS,"关闭client-cache失败！");
        }
        okHttpClient=null;
        return true;
    }

    public String get(String url){
        Request request = new Request.Builder().cacheControl(cacheControl).get().url(url).build();
        return (String) sendRequest(request, false, null, null);
    }

    @Override
    public String get(String url, Map<String, String> headerExt){
        Request.Builder reqBuilder = new Request.Builder().get().url(url);
        if (headerExt != null && headerExt.size() > 0) {
            headerExt.forEach((key, value) -> {
                reqBuilder.addHeader(key, value);
            });
        }
        Request request = reqBuilder.cacheControl(cacheControl).build();
        return (String)sendRequest(request, false, null, null);
    }

    /**
     * @param url
     * @param callback
     * @return
     * @Title: get
     * @Description: 发送 get 请求, 有 callback为异步,callback传null为同步;异步时返回null
     */
    public String get(String url, IAsyncCallback callback) {
        Request request = new Request.Builder().cacheControl(cacheControl).get().url(url).build();
        return (String) sendRequest(request, false, callback, null);
    }

    /**
     * @param url
     * @param callback
     * @return
     * @Title: get
     * @Description: 发送 get 请求并返回okhttp3.Response, 有 callback为异步,callback传null为同步;异步时返回null
     */
    private Response get(String url, Map<String, String> headerExt, IAsyncCallbackForResponse callback) {
        Request.Builder reqBuilder = new Request.Builder().get().url(url);
        if (headerExt != null && headerExt.size() > 0) {
            headerExt.forEach((key, value) -> {
                reqBuilder.addHeader(key, value);
            });
        }
        Request request = reqBuilder.cacheControl(cacheControl).build();
        return (Response) sendRequest(request, true, null, callback);
    }

    /**post:WWWForm*/
    @Override
    public String okhttppost(String url, Map<String,String> params, Map<String, String> headerExt, String dataMediaType) {
        return (String) doPost(url, params, headerExt,null, dataMediaType, null, false, null, null);
    }

    /**post:WWWForm*/
    /**支持异步模式**/
    public String post(String url,Map<String,String> params,Map<String, String> headerExt,String dataMediaType, IAsyncCallback callback) {
        return (String) doPost(url, params, headerExt,null, dataMediaType, null, false, callback, null);
    }

    /**post:FileAndWWWForm*/
    @Override
    public <T extends UploadFileBase> String okhttppost(String url, Map<String, String> prarm, Map<String, String> headerExt, List<T> files) {
        return post(url,prarm,headerExt,files,null);
    }

    /**post:Json/Xml*/
    @Override
    public String okhttppost(String url, String postStr, Map<String, String> headerExt, String dataMediaType, String chatSet) {
        return (String) doPost(url,null,headerExt,postStr,dataMediaType,chatSet,false,null,null);
    }

    /**post:Json/Xml*/
    /**支持异步模式*/
    @Override
    public String okhttppost(String url, String postStr, Map<String, String> headerExt, String dataMediaType, String chatSet, IAsyncCallback callback) {
        return (String) doPost(url,null,headerExt,postStr,dataMediaType,chatSet,false,callback,null);
    }


    /**
     * @param url
     * @param params
     * @param files
     * @param callback
     * @return
     * @Title: post
     * @Description: 文件上传(支持多文件), 有 callback为异步,callback传null为同步;异步时返回null
     */
    public <T> String post(String url, Map<String, String> params,Map<String, String> headerExt, List<T> files, IAsyncCallback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (params != null) {
            params.forEach((k, v) -> builder.addFormDataPart(k, v));
        }
        if(files!=null){
            files.stream().forEach((file) -> {
                if (file instanceof UploadFile) {
                    UploadFile fileTmp = (UploadFile) file;
                    builder.addFormDataPart(fileTmp.getPrarmName(), fileTmp.getFile().getName(), RequestBody.create(MediaType.parse(fileTmp.getMediaType()), fileTmp.getFile()));
                } else if (file instanceof UploadByteFile) {
                    UploadByteFile fileTmp = (UploadByteFile) file;
                    builder.addFormDataPart(fileTmp.getPrarmName(), fileTmp.getFileName(), RequestBody.create(MediaType.parse(fileTmp.getMediaType()), fileTmp.getFileBytes()));
                }
            });
        }

        MultipartBody uploadBody = builder.build();
        Request.Builder reqBuilder = new Request.Builder().cacheControl(cacheControl)
                .post(uploadBody)
                .url(url);
        if (headerExt != null && headerExt.size() > 0) {
            headerExt.forEach((key, value) -> {
                reqBuilder.addHeader(key, value);
            });
        }
        Request request = reqBuilder.build();
        return (String) sendRequest(request, false, callback, null);
    }

    /**
     * @param url
     * @param prarm             传统参数方式
     * @param postStr           需要post的字符串
     * @param dataMediaType     需要post的字符串对应的格式: application/json; application/xml; application/text 等
     * @param callback          异步的回调方法,传null为同步
     * @param isNeedResponse    是否需要Response对象
     * @param callback4Response 传入Response对象的回调
     * @param headerExt         加到请求的header里的参数
     * @return
     * @Title: doPost
     * @Description: 执行post
     */
    private Object doPost(String url, Map<String, String> prarm, Map<String, String> headerExt, String postStr, String dataMediaType,
                          String charSet, boolean isNeedResponse, IAsyncCallback callback, IAsyncCallbackForResponse callback4Response) {
        RequestBody body = okhttp3.internal.Util.EMPTY_REQUEST;
        if (StringUtils.isNotBlank(postStr)) {
            body = RequestBody.create(MediaType.parse(dataMediaType +";"+ charSet), postStr);
        } else if (!CollectionUtils.isEmpty(prarm)) {
            FormBody.Builder builder = new FormBody.Builder();
            prarm.forEach((k, v) -> builder.add(k, v));
            body = builder.build();
        }
        Request.Builder reqBuilder = new Request.Builder().post(body).url(url);
        if (headerExt != null && headerExt.size() > 0) {
            headerExt.forEach((key, value) -> {
                reqBuilder.addHeader(key, value);
            });
        }
        Request request = reqBuilder.cacheControl(cacheControl).build();
        return sendRequest(request, isNeedResponse, callback, callback4Response);
    }

    /**
     * @param request
     * @param isNeedResponse    是否需要返回okhttp3.Response
     * @param callback          不需要 okhttp3.Response 的 callback
     * @param callback4Response
     * @return
     * @Title: sendRequest
     * @Description: 发送请求
     */
    private Object sendRequest(Request request, boolean isNeedResponse, IAsyncCallback callback, IAsyncCallbackForResponse callback4Response) {
        if (callback == null && callback4Response == null) {
            // 同步
            try {
                Response response = okHttpClient.newCall(request).execute();
                if (isNeedResponse) {
                    return response;
                } else {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 异步
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isNeedResponse) {
                        callback4Response.doCallback(null);
                    } else {
                        callback.doCallback(null);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (isNeedResponse) {
                            callback4Response.doCallback(response);
                        } else {
                            callback.doCallback(response.body().string());
                        }
                    } catch (IOException e) {
                        callback.doCallback(null);
                        // e.printStackTrace();
                    }
                }
            });
        }
        return null;
    }

    /**
     * @param url               下载地址
     * @param isNeedResponse    是否需要返回okhttp3.Response
     * @param headerExt         扩展的请求头信息
     * @param callback          返回byte[]的回调(有callback为异步,没有为同步)
     * @param callback4Response 返回okhttp3.Response 的回调
     * @return
     * @Title: download
     * @author klw
     * @Description: 下载文件, 有 callback为异步,callback传null为同步;异步时返回null
     */
    private Object download(String url, boolean isNeedResponse, Map<String, String> headerExt, IAsyncCallbackForDownload callback, IAsyncCallbackForResponse callback4Response) {
        Request.Builder reqBuilder = new Request.Builder().get().url(url);
        if (headerExt != null && headerExt.size() > 0) {
            headerExt.forEach((key, value) -> {
                reqBuilder.addHeader(key, value);
            });
        }
        Request request = reqBuilder.build();

        if (callback == null && callback4Response == null) {
            // 同步
            try {
                Response response = okHttpClient.newCall(request).execute();
                if (isNeedResponse) {
                    return response;
                } else {
                    return response.body().bytes();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 异步
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isNeedResponse) {
                        callback4Response.doCallback(null);
                    } else {
                        callback.doCallback(null);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (isNeedResponse) {
                            callback4Response.doCallback(response);
                        } else {
                            callback.doCallback(response.body().bytes());
                        }
                    } catch (IOException e) {
                        callback.doCallback(null);
                    }
                }
            });
        }
        return null;
    }

    /**
     * @param url       下载地址
     * @param headerExt 扩展的请求头信息
     * @param callback  返回byte[]的回调(有callback为异步,没有为同步)
     * @return
     * @Title: download
     * @author klw
     * @Description: 下载文件并返回文件 byte[], 有 callback为异步,callback传null为同步;异步时返回null
     */
    public byte[] download(String url, Map<String, String> headerExt, IAsyncCallbackForDownload callback) {
        return (byte[]) download(url, false, headerExt, callback, null);
    }

    /**
     * @param url       下载地址
     * @param headerExt 扩展的请求头信息
     * @param callback  返回okhttp3.Response 的回调
     * @return
     * @Title: download
     * @author klw
     * @Description: 下载文件并返回okhttp3.Response(可以通过response获取文件类型, 文件大小, InputStream等, 有 callback为异步, callback传null为同步 ; 异步时返回null
     */
    public Response download(Map<String, String> headerExt, IAsyncCallbackForResponse callback, String url) {
        return (Response) download(url, true, headerExt, null, callback);
    }

    /**
     * @param url      下载地址
     * @param callback 返回byte[]的回调(有callback为异步,没有为同步)
     * @return
     * @Title: download
     * @author klw
     * @Description: 下载文件并返回文件 byte[], 有 callback为异步,callback传null为同步;异步时返回null
     */
    public byte[] download(String url, IAsyncCallbackForDownload callback) {
        return (byte[]) download(url, false, null, callback, null);
    }

    /**
     * @param url      下载地址
     * @param callback 返回okhttp3.Response 的回调
     * @return
     * @Title: download
     * @author klw
     * @Description: 下载文件并返回okhttp3.Response(可以通过response获取文件类型, 文件大小, InputStream等, 有 callback为异步, callback传null为同步 ; 异步时返回null
     */
    public Response download(IAsyncCallbackForResponse callback, String url) {
        return (Response) download(url, true, null, null, callback);
    }

}
