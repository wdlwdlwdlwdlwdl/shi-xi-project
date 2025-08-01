package com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/25 10:55
 */
@Data
public class DeliveryFeeHistoryRpcReq extends AbstractCommandRpcRequest {
    private static final long serialVersionUID=1L;
    private Long id;
    private String feeCode;
    private Integer deliveryRoute;
    private Integer deleted;
    private Integer type;
    private Long fee;
    private String categoryId;
    private String categoryName;
    private String merchantCode;
    private String merchantName;
    private Integer deliveryType;
    private String path;
    private String remark;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private String operator;
    private Integer active;

    @ApiModelProperty("是否类目全部")
    private String isCategoryAll;

    @ApiModelProperty("是否卖家全部")
    private String isMerchantAll;
}
