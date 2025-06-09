package com.aliyun.gts.gmall.manager.front.sourcing.input.pr;

import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import lombok.Data;

import java.util.Date;

@Data
public class PurchaseRequestReq extends AbstractQueryRestRequest {

    PageParam page;

    Long prId;

    String prName;

    String prUser;

    String prDep;

    Date[] prTime;

    Integer status;

    @Override
    public boolean isWrite() {
        return false;
    }
}
