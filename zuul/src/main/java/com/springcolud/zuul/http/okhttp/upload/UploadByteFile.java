package com.springcolud.zuul.http.okhttp.upload;

import java.io.Serializable;

public class UploadByteFile extends UploadFileBase implements Serializable {

    /**
     * @Fields fileBytes : 文件的二进制数据
     */
    private byte[] fileBytes;
    
    /**
     * @Fields fileName : 文件名称
     */
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }
    
}
