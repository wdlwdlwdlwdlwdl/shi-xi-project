package com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation;

import lombok.Data;

import java.util.Map;

@Data
public class EvaluationExtendModifyReq extends EvaluationIdReq {

    @Override
    public boolean isWrite() {
        return true;
    }

    private Map<String, Object> updateExtendMap;    // 更新扩展字段, 只更新传入的key值
}
