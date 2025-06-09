package com.aliyun.gts.gmall.platform.trade.domain.wrapper;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OrderQueryWrapper {

    private Long custId;

    private Long sellerId;

    private Integer status;

    private Integer evaluate;

    private Set<Integer> statusList;

    private List<Long> primaryOrderIds;

    private Boolean primaryOrderFlag;

    Integer currentPage = 1;

    Integer pageSize = 10;

    public Integer getOffset(){
        return (currentPage - 1) * pageSize;
    }

}
