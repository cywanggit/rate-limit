package com.springcolud.zuul.http.apachehttp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ByteArrayBody;

import java.io.InputStream;

/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description 代理公共参数
 * @FileName CommonParams
 * @auther cw
 * @create 2019-11-23 9:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonParams {

    private String url;

    private String method;

    private ContentType contentType;

    private String filePath;

    private String fileName;
    private String fileParamKey;

    private String fileTypeParamKey;

    private String fileType;

    private ByteArrayBody byteArrayBody;

}
