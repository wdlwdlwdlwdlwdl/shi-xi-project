package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.StepOrderFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.StepOrderFeeDO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tc_step_order")
public class TcStepOrderDO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 无意义主键
     */
    private Long id;

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
    private StepOrderFeeDO priceAttr;

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
}
