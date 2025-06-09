package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gcai.platform.sourcing.common.input.query.SourcingQuery;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
public class SourcingQueryReq extends LoginRestQuery {

    private PageParam page;
    private String purchaserName;
    private Long id;
    private String title;
    private List<Long> ids;
    private Integer status;
    private List<Integer> statusList;
    private Integer sourcingType;
    private Integer bizId;
    private Integer applyType;
    private Integer approveStatus;
    private Long categoryId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<Date> createTimes;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<Date> purchaseTimes;

    public SourcingQuery build() {
        SourcingQuery q = new SourcingQuery();
        BeanUtils.copyProperties(this, q);
        q.setPurchaserId(getCustId());
        return q;
    }
}
