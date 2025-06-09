package com.aliyun.gts.gmall.platform.trade.api.dto.common;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PageQuery extends AbstractQueryRpcRequest {

    @NotNull
    @Min(1)
    @Max(200)
    Integer currentPage = 1;

    //pageSize 最大不超过50 - 可能修改部分
//    @Max(50)
    @NotNull
    @Min(1)
    @Max(100)
    Integer pageSize = 10;

    public Integer getOffset(){
        return (currentPage - 1) * pageSize;
    }

}
