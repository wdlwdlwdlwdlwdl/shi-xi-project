package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PromBaseVO extends AbstractOutputInfo {

    private Long id;
    /**
     * 优惠工具code
     */
    private String promotionToolCode;

    /**
     * 优惠级别：商品、店铺、自由簇
     */
    private Integer promotionLevel;

    /**
     * 优惠行为类型，如：减钱、折扣、优惠价
     */
    private Integer promotionActionType;

    /**
     * 资产类型0表示无，1表示券，未来可增加
     */
    private Integer assetsType;

    /**
     * 非结构化的条件元数据，结构化的明细条件存在明细表
     */
    private JSONObject conditionRule;

    /**
     * 优惠规则
     */
    private DiscountRuleVO discountRule;

    /**
     * 其他扩展信息元数据
     */
    private JSONObject feature;

    /**
     * 预热期开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preStartTime;

    /**
     * 预热期结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preEndTime;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 总的优惠数量（冗余）
     */
    private Integer totalCnt;

    /**
     * 0初始化，1已发布，-1下线，9过期
     */
    private Integer status;

    /**
     * 创建人id
     */
    private Long creatorId;

    /**
     * 修改人id
     */
    private Long editorId;

    /**
     * 更新时间
     */
    private Date gmtCreate;

    /**
     * 创建时间
     */
    private Date gmtModified;

    /**
     * 备注
     */
    private String remark;

    /**
     * 渠道
     */
    private Integer channel;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 优惠对象类型;这个对象只有前端使用
     */
    private Integer promTargetType;

}
