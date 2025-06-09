package com.aliyun.gts.gmall.center.trade.domain.dataobject.point;


import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @author FaberWong
 * @description:
 * @date 2021/1/27 19:02
 */
@Data
public class AcBookRecordDO {

    private Long id;

    /**
     * 客户编号
     */
    private Long custId;

    /**
     * 账本编号
     */
    private Long bookId;
    /**
     * 账户类型: 1 积分账户;默认是1
     */
    private Integer accountType = 1;
    /**
     * 变动资产:原子资产
     */
    private Long changeAssets;
    /**
     * ChangeTypeEnum
     */
    private Integer changeType;

    /**
     * 变动类型名称
     */
    private String changeName;
    /**
     *
     */
    private Integer changeStep;

    /**
     * 变动方向： 1-赋予 加，2-使用 减
     */
    private Integer changeDirection = 1;

    /**
     * 唯一幂等ID
     */
    private String bizId;
    /**
     * 0:待生效/1:生效/9:失效
     * 变动状态，1-使用进行中，2-成功,结束，3-取消，4-退货，5-系统失败补偿
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Long createId;

    /**
     * 创建人
     */
    private String creator;


    /**
     * 备注
     */
    private String remark;

    /**
     * 失效时间
     * 精确到日
     */
    private Date invalidTime;
    /**
     * 积分,分摊字段
     *
     */
    private Map<String,String> divideMap;
    /**
     * 展示积分;仅仅前端展示使用
     */
    private String displayPoint;
    
    /**
     *
     */
    private Date gmtModified;//修改时间
    private Date gmtCreate;//初始时间

    /**
     *   保留状态  默认是0:不保留  1:保留中 2:保留取消 3:保留转正 9:保持处理中
     */
    private Integer reserveState;
}
