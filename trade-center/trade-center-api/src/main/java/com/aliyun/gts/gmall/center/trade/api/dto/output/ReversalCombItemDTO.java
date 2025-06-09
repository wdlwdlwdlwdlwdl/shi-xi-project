package com.aliyun.gts.gmall.center.trade.api.dto.output;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/25 16:24
 */
@Data
public class ReversalCombItemDTO extends CombineItemDTO {

    @ApiModelProperty("退换数量")
    private Integer cancelQty;
}
