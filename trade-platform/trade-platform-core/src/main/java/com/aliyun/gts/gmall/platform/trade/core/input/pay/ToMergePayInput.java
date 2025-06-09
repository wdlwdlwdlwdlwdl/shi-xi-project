package com.aliyun.gts.gmall.platform.trade.core.input.pay;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.MergePayFlowInfo;
import com.aliyun.gts.gmall.platform.trade.common.domain.pay.CurrencyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.commons.compress.utils.Lists;
import org.testng.collections.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ToMergePayInput implements Serializable {


    private List<Long> primaryOrderIds;

    private Long custId;

    private String custName;

    private String orderChannel;

    private String orderType;

    private String payChannel;

    private Long totalOrderFee;

    private Long realPayFee;

    private Long pointAmount;

    private String recogCode;

    private String appId;

    private String openId;

    private Integer payType;

    private String payMethod;

    private CurrencyType currencyType;

    private Map<String, Object> extra = Maps.newHashMap();

    private List<MergePayFlowInfo> mergePayFlowInfos = Lists.newArrayList();

    private String returnPayData;

    /**
     * 是否立即回调,默认为false，表示需要异步回调
     */
    private boolean isDirectCallBack;

}
