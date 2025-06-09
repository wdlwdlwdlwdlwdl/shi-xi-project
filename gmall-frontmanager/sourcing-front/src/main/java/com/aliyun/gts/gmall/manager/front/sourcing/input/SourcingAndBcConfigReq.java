package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("寻源id和评标配置id")
@Data
public class SourcingAndBcConfigReq extends AbstractRequest {

    Long sourcingId;

    Long configId;

    @Override
    public boolean isWrite() {
        return false;
    }
}
