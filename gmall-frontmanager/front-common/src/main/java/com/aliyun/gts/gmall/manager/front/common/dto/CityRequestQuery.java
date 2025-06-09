package com.aliyun.gts.gmall.manager.front.common.dto;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("城市查询")
public class CityRequestQuery extends AbstractQueryRestRequest {

    // 搜索关键字
    private String keyWords;

}
