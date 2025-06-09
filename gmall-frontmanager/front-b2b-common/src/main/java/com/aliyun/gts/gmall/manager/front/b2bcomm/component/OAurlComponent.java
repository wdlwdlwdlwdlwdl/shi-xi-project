package com.aliyun.gts.gmall.manager.front.b2bcomm.component;

import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.SignatureUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OAurlComponent {

    @Value("${spring.application.domain:}")
    String domain;

    /**
     * http://127.0.0.1:8089/sourcing/home.html?_debug_&signToken=dXcgaGgnDsRnvYOv7ZA1D1UtTQ4fpbt3bLJ%2BFPpkg6M%3D
     * #/prList?prDetail=id%253D13_x_r%253D1
     *
     * http://purchase.hanghangohye.com/sourcing/home
     * .html?_debug_#/tenderingList?tenderDetail=sourcingId%253D134_x_r%253D1
     * @param id
     * @param path
     * @return
     */
    public String buildOAUrl(Long id , String path){
        StringBuffer sb = new StringBuffer();
        sb.append("http://").append(domain).append("/sourcing/home.html?signToken=").
            append(SignatureUtils.encrypt(id+"")).append(path).append(id).append("_x_r%253D1");
        return sb.toString();
    }

    public String buildPath(String path){
        StringBuffer sb = new StringBuffer();
        sb.append("http://").append(domain).append("/sourcing/home.html").append(path);
        return sb.toString();
    }

}
