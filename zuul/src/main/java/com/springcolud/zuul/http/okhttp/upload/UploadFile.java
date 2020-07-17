package com.springcolud.zuul.http.okhttp.upload;

import lombok.Data;
import org.apache.http.entity.mime.content.ByteArrayBody;

import java.io.File;
import java.io.Serializable;

@Data
public class UploadFile extends UploadFileBase implements Serializable {
    private File file;
    private String fileName;
    private ByteArrayBody byteArrayBody;

}