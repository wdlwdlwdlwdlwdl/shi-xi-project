package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import lombok.Data;

@Data
public class OssQueryReq extends AbstractQueryRestRequest {
    /**
     * oss://bucket.endpoint/key
     */
    String ossUrl;

}
