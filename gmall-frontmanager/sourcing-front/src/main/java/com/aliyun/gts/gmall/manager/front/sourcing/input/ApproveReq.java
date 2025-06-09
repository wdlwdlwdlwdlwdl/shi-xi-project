package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/6/2 10:24
 */
@Data
public class ApproveReq extends LoginRestQuery {
    //审核ID
    private Long id;
    //审核状态 1表示审核通过,0表示拒绝
    private Integer approveStatus;
    private String remark;

    public boolean isPass() {
        return approveStatus.equals(1);
    }
    public boolean forbid() {
        return approveStatus.equals(0);
    }
}
