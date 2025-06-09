package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/26 19:25
 */
@Data
public class DeliveryFee extends AbstractPageQueryRpcRequest {

    private String feeCode;
    private Integer deleted;
    private String remark;
    private Integer deliveryRoute;
    private String categoryId;
    private String categoryName;
    private String merchantCode;
    private String merchantName;
    private Integer deliveryType;
    private Integer active;
    @ApiModelProperty("是否类目全部")
    private String isCategoryAll;

    @ApiModelProperty("是否卖家全部")
    private String isMerchantAll;

}
