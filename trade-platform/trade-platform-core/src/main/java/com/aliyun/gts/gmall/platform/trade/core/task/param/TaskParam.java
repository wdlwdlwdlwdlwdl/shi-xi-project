package com.aliyun.gts.gmall.platform.trade.core.task.param;

import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskParam {

    private Long sellerId;
    private Long primaryOrderId;
    private List<BizCodeEntity> bizCodes;
}
