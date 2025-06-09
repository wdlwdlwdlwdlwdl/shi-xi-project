package com.aliyun.gts.gmall.manager.front.promotion.dto.output;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/14 9:56
 */
@Data
public class PlayPrizeVo extends AbstractOutputInfo {
    private Long id;
    /**
     * 总数
     */
    private Long totalCnt;
    /**
     * 已经发放的数量
     */
    private Long sendCnt;
    /**
     * 奖品名称
     */
    private String name;
    private String picUrl;
    /**
     * 奖品类型
     */
    private Integer type;
    /**
     * 奖品信息
     */
    private JSONObject prizeInfo;
    /**
     * 优惠券信息
     */
    private Object couponInfo;
}
