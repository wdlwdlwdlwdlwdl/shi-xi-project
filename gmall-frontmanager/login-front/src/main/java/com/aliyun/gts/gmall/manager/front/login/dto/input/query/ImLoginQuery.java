package com.aliyun.gts.gmall.manager.front.login.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/10 20:21
 */
@ApiModel(description = "im聊天免登")
@Data
public class ImLoginQuery extends AbstractQueryRestRequest {
    @ApiModelProperty(value = "外部接口反查token", required = false)
    private String token;
}
