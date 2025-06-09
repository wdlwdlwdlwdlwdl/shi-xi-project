package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class EvaluationRatePicVO {

    /** 商家id */
    private Long sellerId;

    private Long id;

    private String rateDesc;

    private Integer rateScore;

    private String picUrl;

    private Date gmtCreate;

    private Long replyId;
    private Long primaryOrderId;
    private Long orderId;
    private Long itemId;
    private Long custId;

    private String firstName;
    private String middleName;
    private String lastName;
}
