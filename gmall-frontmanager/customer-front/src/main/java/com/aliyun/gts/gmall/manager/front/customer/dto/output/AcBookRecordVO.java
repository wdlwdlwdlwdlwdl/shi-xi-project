package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 积分流水记录
 *
 * @author 俊贤
 * @date 2021/03/16
 */
@Data
public class AcBookRecordVO {
    /**
     * 客户编号
     */
    private Long    custId;
    /**
     * 账户类型: 1 积分账户;默认是1
     */
    private Integer accountType = 1;
    /**
     * 变动资产
     */
    private Long    changeAssets;
    /**
     * 对外展示的积分数量
     */
    private String displayPoint;

    /**
     * ChangeTypeEnum
     */
    private Integer changeType;

    /**
     * 变动类型名称
     */
    private String  changeName;
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
    private String  bizId;
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
     * 生效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date effectTime;

    /**
     * 失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invalidTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;

    /**
     * 初始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    /**
     * 积分,分摊字段
     */
    private Map<String, String> divideMap;
}