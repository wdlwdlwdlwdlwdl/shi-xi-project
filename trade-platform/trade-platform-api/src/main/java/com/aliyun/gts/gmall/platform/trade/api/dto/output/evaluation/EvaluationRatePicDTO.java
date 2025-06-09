package com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EvaluationRatePicDTO extends AbstractOutputInfo {

    /** 商家id */
    private Long sellerId;

    private Long id;

    private String rateDesc;

    private Integer rateScore;

    private List<String> ratePic;

    private Date gmtCreate;

    private Long replyId;
    private Long primaryOrderId;
    private Long orderId;
    private Long itemId;
    private Long custId;
    private String firstName;
    private String middleName;
    private String lastName;

    private int count = 0;
}
