package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import lombok.Data;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/26 19:41
 */
@Data
public class TimeoutSetting extends AbstractPageQueryRpcRequest {

    private Long id;
    private String timeoutCode;
    private Integer orderStatus;
    private Integer payType;
    private String statusName;
    private String timeRule;
    private Integer timeType;

}
