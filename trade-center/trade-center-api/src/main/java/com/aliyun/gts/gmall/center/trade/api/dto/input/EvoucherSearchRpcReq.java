package com.aliyun.gts.gmall.center.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EvoucherSearchRpcReq extends AbstractPageQueryRpcRequest {
    public static final int SORT_WRITEOFF_DESC = 1; // 优先核销时间倒序、其次创建时间倒序
    public static final int SORT_CREATE_DESC = 2;   // 创建时间倒序

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("指定状态, EvoucherStatusEnum")
    private List<Integer> status;

    @ApiModelProperty("排序方式")
    private int sortType = SORT_WRITEOFF_DESC;

    @Override
    public void checkInput() {
        super.checkInput();
    }
}
