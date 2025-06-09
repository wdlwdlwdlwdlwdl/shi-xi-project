package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.LogisticsDetailDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 物流详情
 *
 * @author tiansong
 */
@Data
@ApiModel("物流详情")
public class LogisticsDetailVO extends LogisticsDetailDTO {
    @ApiModelProperty("物流投递状态")
    public String getDeliveryStatusName() {
        if (CollectionUtils.isEmpty(this.getFlowList())) {
            return null;
        }
        return this.getFlowList().get(0).getRemark();
    }
}
