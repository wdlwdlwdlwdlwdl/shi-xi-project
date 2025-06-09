package com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
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
public class CancelReasonQueryRpcReq extends AbstractPageQueryRpcRequest {
    private static final long serialVersionUID=1L;
    private Long id;
    private String cancelReasonCode;
    private String cancelReasonName;
    private Integer deleted;
    private String remark;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private String operator;
    private Integer channel;
}
