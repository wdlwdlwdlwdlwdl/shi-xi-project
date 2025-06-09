package com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.framework.i18n.MultiLangText;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ItemEvaluationV2DTO extends AbstractOutputInfo {


    private Long id;
    /**
     * 主订单id
     */
    private Long orderNumber;

    /**
     * 商家评分
     */
    private Integer merchantRating;

    /**
     * 商家评论
     */
    private String merchantRateDesc;

    /**
     * 物流评分
     */
    private Integer deliveryRating;

    /**
     * 物流评论
     */
    private String deliveryRateDesc;

    /**
     * 订单商品名称(多语言)
     */
    private List<MultiLangText> productNameI8n;

    /**
     * 订单商品名称
     */
    private List<String> productName;

    /**
     * 消费者名称
     */
    private String customer;

    /**
     * 状态
     */
    private Integer approveStatus;

    /**
     * 状态名称
     */
    private String approveStatusDisplay;

    /**
     * 评论时间
     */
    private Date evaluationDate;

    /**
     * 卖家BIN\IIN
     */
    private String merchantBinIIn;

    /**
     * 卖家名称
     */
    private String merchantName;

    /**
     * 用户追评、卖家回复等, 按时间正序
     */
    private List<EvaluationData> evaluationData;

    @Data
    public static class EvaluationData extends AbstractOutputInfo {
        private EvaluationInfo evaluationInfo;
        private List<EvaluationInfo> replayList;
    }

    @Data
    public static class EvaluationInfo extends AbstractOutputInfo {
        private Integer evType;
        private Integer rate;
        private List<String> ratePic;
        private String rateDesc;
        private Date rateDate;
        private Integer approveStatus;
        private String approveStatusDisplay;
        private String approveContent;
        private Long orderId;
        private Long itemId;
        private Long skuId;
        private Long id;
        private MultiLangText productName;
    }


}
