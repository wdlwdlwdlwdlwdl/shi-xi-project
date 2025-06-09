package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class PromotionDetailVO implements Serializable {

    /**
     * 活动ID
     */
    private Long campaignId;
    private Date preStartTime;
    private Date preEndTime;
    private Date startTime;
    private Date endTime;
    private String name;
    private String remark;
    private Map<String, Object> extras;
    private JSONObject display;
    private String promotionToolCode;
    private String sellerName;
    private Integer sellerKaTag;
}
