package com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder;

import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.StepOrderFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import lombok.Data;

import java.util.Date;

@Data
public class StepOrder extends AbstractBusinessEntity {

    /**
     * 主订单号
     */
    private Long primaryOrderId;

    /**
     * 阶段序号
     */
    private Integer stepNo;

    /**
     * 阶段名称
     */
    private String stepName;

    /**
     * 阶段价格
     */
    private StepOrderPrice price;

    /**
     * @see StepOrderStatusEnum 阶段状态
     */
    private Integer status;

    /**
     * 扩展字段
     */
    private StepOrderFeatureDO features;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 版本号
     */
    private Long version;


    public StepOrderFeatureDO features() {
        if (features == null) {
            features = new StepOrderFeatureDO();
        }
        return features;
    }
}
