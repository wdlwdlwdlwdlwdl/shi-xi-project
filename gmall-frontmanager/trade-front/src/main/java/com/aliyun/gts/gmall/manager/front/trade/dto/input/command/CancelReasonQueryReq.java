package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import lombok.Data;

import java.util.Date;

/**
 * @author yangl
 */
@Data
public class CancelReasonQueryReq extends AbstractPageQueryRpcRequest {

    private Long id;
    private String cancelReasonCode;
    private String cancelReasonName;
    private Integer deleted;
    private String remark;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private Integer channel;
}
