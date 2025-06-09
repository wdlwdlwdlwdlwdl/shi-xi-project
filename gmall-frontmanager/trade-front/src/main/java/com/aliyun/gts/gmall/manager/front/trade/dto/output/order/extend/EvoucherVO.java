package com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.trade.dto.utils.EvoucherStatusDisplay;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

@ApiModel("电子凭证展现信息")
@Data
public class EvoucherVO extends OrderExtendVO {
    @ApiModelProperty("电子凭证列表")
    List<EvoucherInfoVO> evouchers;

    @ApiModelProperty("电子凭证可用数量")
    public Long getNotUseCount() {
        if(CollectionUtils.isEmpty(evouchers)) {
            return 0L;
        }
        return evouchers.stream().filter(v -> EvoucherStatusDisplay.NOT_USED.getCode().equals(v.getStatus())).count();
    }
}
