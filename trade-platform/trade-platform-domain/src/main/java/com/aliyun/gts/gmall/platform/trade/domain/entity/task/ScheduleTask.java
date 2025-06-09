package com.aliyun.gts.gmall.platform.trade.domain.entity.task;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class ScheduleTask extends AbstractBusinessEntity {

    @ApiModelProperty("任务类型")
    private String type;

    @ApiModelProperty("调度时间")
    private Date scheduleTime;

    @ApiModelProperty("任务参数")
    private Map<String, Object> params;

    @ApiModelProperty("关联的主订单ID")
    private Long primaryOrderId;


    private MainOrder mainOrder;

    public void putParam(String name, Object value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(name, value);
    }

    public <T> T getParam(String name) {
        return params == null ? null : (T) params.get(name);
    }

    public Long getParamLong(String name) {
        Number num = getParam(name);
        return num == null ? null : num.longValue();
    }
}
