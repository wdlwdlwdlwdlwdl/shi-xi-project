package com.aliyun.gts.gmall.center.trade.domain.entity.point;

import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointRollbackParam;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PointRollbackExtParam extends PointRollbackParam {

    private Long minChangeCount;

    public PointRollbackExtParam(PointRollbackParam p) {
        BeanUtils.copyProperties(p, this);
    }
}
