package com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import lombok.Data;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/25 10:55
 */
@Data
public class TimeoutSettingQueryRpcReq extends AbstractPageQueryRpcRequest {

    private static final long serialVersionUID=1L;
    private Long id;
    private String timeoutCode;
    private Integer orderStatus;
    private String statusName;
    private String timeRule;
    private Integer payType;
    private Integer timeType;
    private Integer deleted;
    private String remark;
    private String createId;
    private String updateId;
    private String operator;
}
