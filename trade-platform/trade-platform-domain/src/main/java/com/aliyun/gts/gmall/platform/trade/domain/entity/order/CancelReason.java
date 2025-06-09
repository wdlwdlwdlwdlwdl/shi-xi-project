package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/26 19:25
 */
@Data
public class CancelReason extends AbstractPageQueryRpcRequest {

    private String cancelReasonCode;
    private String cancelReasonName;
    private Integer deleted;
    private String remark;
    private Integer channel;

}
