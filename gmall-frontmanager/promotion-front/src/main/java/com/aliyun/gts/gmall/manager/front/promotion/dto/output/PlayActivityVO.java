package com.aliyun.gts.gmall.manager.front.promotion.dto.output;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/13 18:38
 */
@Data
public class PlayActivityVO extends AbstractOutputInfo {
    /**
     * 优惠行为类型，如：减钱、折扣、优惠价
     */
    private Integer playType;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 活动状态
     */
    private Integer status;


    private List<PlayPrizeVo> prizes;

    /**
     * 自己分享裂变相关信息
     */
    private ShareFissionVO share;

    /**
     * 其他人的分享数据
     */
    private ShareFissionVO otherShare;

    /**
     *距离结束时间
     */
    private Long remainTime;
    /**
     * 助力人叔
     */
    private JSONObject playRule;
    /**
     * 参与次数
     */
    private Long participateCnt;

    /**
     * 无奖励剩余
     */
    private Boolean shareNoPrizeLeft = Boolean.FALSE;

}
