package com.aliyun.gts.gmall.platform.trade.domain.entity.reversal;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 售后子单
 */
@Data
public class SubReversal extends AbstractBusinessEntity {

    @ApiModelProperty("售后主单ID")
    private Long primaryReversalId;

    @ApiModelProperty("售后子单ID")
    private Long reversalId;

    @ApiModelProperty("子订单信息")
    private SubOrder subOrder;

    @ApiModelProperty("退换商品数量")
    private Integer cancelQty;

    @ApiModelProperty("退款金额")
    private Long cancelAmt;

    @ApiModelProperty("扩展字段")
    private ReversalFeatureDO reversalFeatures;

    @ApiModelProperty("版本号")
    private Long version;

    @ApiModelProperty("firstName")
    private String firstName;

    @ApiModelProperty("lastName")
    private String lastName;

    @ApiModelProperty("bin_or_iin")
    private String binOrIin;

    public ReversalFeatureDO reversalFeatures() {
        if (reversalFeatures == null) {
            reversalFeatures = new ReversalFeatureDO();
        }
        return reversalFeatures;
    }
}
