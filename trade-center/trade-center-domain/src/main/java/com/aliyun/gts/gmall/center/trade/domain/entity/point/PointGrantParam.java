package com.aliyun.gts.gmall.center.trade.domain.entity.point;

import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointReduceParam;
import lombok.Data;

import java.util.Date;

@Data
public class PointGrantParam extends PointReduceParam {

    private Date invalidDate;

    private Integer reserveState;

    private Long acBookRecordId;

    private Date effectTime;

    /**
     * 备注
     */
    private String remark;
}
