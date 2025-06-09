package com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
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
public class CustDeliveryFeeHistoryRpcReq extends AbstractCommandRpcRequest {
    private static final long serialVersionUID=1L;
    private Long id;
    private String custFeeCode;
    private Integer deliveryRoute;
    private Integer deliveryType;
    private Integer deleted;
    private Integer active;
    private Long fee;
    private Integer type;
    private String remark;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private String operator;
}
