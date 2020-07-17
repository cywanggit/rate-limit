package com.springcolud.zuul.http.okhttp.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class UploadFileUtil {

    private static Logger logger = LoggerFactory.getLogger(UploadFileUtil.class);

    @Value("${wsurm.auth.config.filename}")
    private static String uploadMaxSize;

    public static <T> List<String> base64(List<T> files){
        List<String> base64List = new ArrayList<>();
        List<byte[]> byteStreamList = byteStream(files);
        for(byte[] byteArray : byteStreamList){
            base64List.add(Base64Utils.encodeToString(byteArray));
        }
        return base64List;
    }

    public static <T> byte[] byteStream(T file){
        if(file instanceof String){
            return file == null ? "".getBytes() : ((String) file).getBytes();
        }else if(file instanceof File){
            return byteFileStream((File) file);
        }else if(file instanceof MultipartFile){
            return byteUploadStream((MultipartFile) file);
        }else{
            return "".getBytes();
        }
    }
    public static <T> List<byte[]> byteStream(List<T> files){
        List<byte[]> resultList = new ArrayList<>();
        for(T file : files){
            if(file instanceof String){
                resultList.add(file == null ? "".getBytes() : ((String) file).getBytes());
            }else if(file instanceof File){
                resultList.add(byteFileStream((File) file));
            }else if(file instanceof MultipartFile){
                resultList.add(byteUploadStream((MultipartFile) file));
            }else{
                resultList.add("".getBytes());
            }
        }
        return resultList;
    }

    private static byte[] byteFileStream(File file){
        if(!file.exists() || !file.isFile()){
            return "".getBytes();
        }
        try {
            byte[] fileAllBytes = Files.readAllBytes(file.toPath());
            return fileAllBytes.length > Integer.valueOf(uploadMaxSize) ? "".getBytes() : fileAllBytes;
        }catch (Exception exception){
            logger.error("读取文件出错，详情如下：" + exception.getMessage());
            return "".getBytes();
        }
    }

    private static byte[] byteUploadStream(MultipartFile multipartFile){
        try{
            byte[] uploadAllBytes = multipartFile.getBytes();
            return uploadAllBytes.length > Integer.valueOf(uploadMaxSize) ? "".getBytes():uploadAllBytes;
        }catch (Exception exception){
            return "".getBytes();
        }
    }

}
