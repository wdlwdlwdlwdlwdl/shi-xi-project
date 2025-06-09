package com.aliyun.gts.gmall.platform.trade.api.dto.message;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OversaleMessageDTO implements Transferable {

    private Long primaryOrderId;

    // true: 库存异常 / false: 库存不足
    private boolean inventoryException;
}
