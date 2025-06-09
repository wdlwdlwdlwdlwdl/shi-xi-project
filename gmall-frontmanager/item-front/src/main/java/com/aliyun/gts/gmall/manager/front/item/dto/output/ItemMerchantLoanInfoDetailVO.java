package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.Response;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "商品商家分期详细信息返回对象")
public class ItemMerchantLoanInfoDetailVO implements Response {


    @ApiModelProperty(value = "分期价格")
    private Long  loanPrice;

    @ApiModelProperty(value = "折扣价格")
    private Double  discountPrice;
    @ApiModelProperty(value = "类型")
    private String  type;
}
