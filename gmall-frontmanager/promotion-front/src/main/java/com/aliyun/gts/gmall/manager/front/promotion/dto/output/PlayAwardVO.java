package com.aliyun.gts.gmall.manager.front.promotion.dto.output;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/15 10:04
 */
@Data
public class PlayAwardVO extends AbstractOutputInfo {
    /**
     * 奖品ID
     */
    private Long id;
    /**
     *
     */
    private String message;
    /**
     * 奖品ID
     */
    private Long prizeDetailId;
    /**
     *奖品类型
     */
    private Integer prizeType;
    /**
     * 奖品名称
     */
    private String prizeName;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 状态
     */
    private Integer status;
    /**
     *扩展信息
     */
    private JSONObject feature;

    /**
     *奖品信息
     */
    private JSONObject prizeInfo;

    /**
     * 达到次数上线
     */
    private Boolean playLimit = Boolean.FALSE;
}
