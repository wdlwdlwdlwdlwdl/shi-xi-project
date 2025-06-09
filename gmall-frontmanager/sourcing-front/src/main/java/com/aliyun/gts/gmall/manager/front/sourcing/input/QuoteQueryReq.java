package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gcai.platform.sourcing.common.input.query.QuoteQuery;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class QuoteQueryReq extends LoginRestQuery {

    private PageParam page;
    private Long quoteId;
    private Long sourcingId;
    private Long supplierId;
    private Integer status;
    private List<Integer> statusList;
    private Integer awardStatus;
    private String sourcingName;
    private Boolean includeDetail = false;
    private Integer sourcingType;

    public QuoteQuery build() {
        QuoteQuery q = new QuoteQuery();
        BeanUtils.copyProperties(this, q);
        q.setPurchaserId(getCustId());
        return q;
    }
}
