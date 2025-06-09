package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;

import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import lombok.Data;

import java.util.List;

@Data
public class CusNoticeQuery extends LoginRestQuery {

    private Long id;
    private Long receiveId;
    private PageParam page;
    private Long sendId;
    private List<Long> sendIds;
    private Integer status;
    private Integer type;
    private List<Long> ids;

}
