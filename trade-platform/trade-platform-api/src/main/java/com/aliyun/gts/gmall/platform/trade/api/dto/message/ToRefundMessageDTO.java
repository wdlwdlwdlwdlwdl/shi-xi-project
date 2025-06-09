package com.aliyun.gts.gmall.platform.trade.api.dto.message;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToRefundMessageDTO implements Transferable {

    private Long primaryReversalId;
}
