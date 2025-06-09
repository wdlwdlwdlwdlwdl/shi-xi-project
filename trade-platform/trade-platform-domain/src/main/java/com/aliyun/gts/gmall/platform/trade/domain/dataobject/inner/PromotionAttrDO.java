package com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner;

import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PromotionAttrDO extends ExtendComponent {

    @ApiModelProperty("营销返回的活动分摊明细")
    private List<ItemDivideDetail> itemDivideDetails;
}
