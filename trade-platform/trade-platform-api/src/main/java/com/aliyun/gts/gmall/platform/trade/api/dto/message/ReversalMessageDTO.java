package com.aliyun.gts.gmall.platform.trade.api.dto.message;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReversalMessageDTO implements Transferable {

    @ApiModelProperty("售后单ID")
    private Long primaryReversalId;

    @ApiModelProperty("售后单状态")
    private Integer reversalStatus;

    @ApiModelProperty("售后单状态变更消息 -- 原状态")
    private Integer fromReversalStatus;

    @ApiModelProperty("版本号")
    private Long version;
}
