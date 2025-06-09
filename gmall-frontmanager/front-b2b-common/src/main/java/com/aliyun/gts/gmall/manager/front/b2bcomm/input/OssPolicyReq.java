package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 生成oss上传凭证的入参
 */
@Data
public class OssPolicyReq extends AbstractQueryRestRequest {

    @NotEmpty
    String fileName;

}
