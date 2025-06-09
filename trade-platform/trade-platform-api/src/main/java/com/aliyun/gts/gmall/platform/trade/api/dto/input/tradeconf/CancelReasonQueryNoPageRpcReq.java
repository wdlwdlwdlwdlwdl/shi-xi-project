package com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import lombok.Data;

import java.util.Date;

@Data
public class CancelReasonQueryNoPageRpcReq extends AbstractQueryRpcRequest {

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
    private int channel;
}
