package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import lombok.Data;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/26 19:25
 */
@Data
public class FeeRules extends AbstractPageQueryRpcRequest {

    private String feeRulesCode;
    private Long custAmount;
    private Long merchantAmount;
    private Long interCustAmount;
    private Long interMerchantAmount;
    private Integer deleted;
    private String remark;

}
