package com.springcolud.zuul.http.okhttp.upload;

import java.io.Serializable;

public abstract class UploadFileBase implements Serializable {

    /**
     * @Fields prarmName : 文件参数名称
     */
    private String prarmName;
    
    /**
     * @Fields mediaType : 文件类型的 MediaType
     */
    private String mediaType;

    public String getPrarmName() {
        return prarmName;
    }

    public void setPrarmName(String prarmName) {
        this.prarmName = prarmName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    
    
}
