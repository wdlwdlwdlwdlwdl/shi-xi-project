package com.aliyun.gts.gmall.manager.front.item.adaptor;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.aliyun.gts.gmall.framework.server.util.PublicFileHttpUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FileAdaptor {

    @Resource
    private PublicFileHttpUrl publicFileHttpUrl;

    @Value("#{'${minio.enabled}' == 'true' ? '${minio.bucket.item:itemBucket}' : '${oss.bucket.item}'}")
    private String itemBucket;

    @Value("${minio.bucket.itemdesc}")
    private String itemDescBucket;
       /**
         * 获取商品描述（文件转字符）
         * @param path 文件路径
         * @return str
         */
        public String getFileToText(String path){
            if(StringUtils.isBlank(path)){
                return "";
            }
            return  publicFileHttpUrl.getContent(itemDescBucket, path);
        }
}
