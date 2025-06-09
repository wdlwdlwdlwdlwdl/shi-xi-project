package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.platform.promotion.common.constant.SubscribeConsts;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 订阅
 */
@Data
public class CustomerCampSubscribeCommand extends LoginRestCommand {

    @NotNull(message = "itemId cannot be null")
    private Long itemId;

    /**
     * sku_id
     */
    @NotNull(message = "skuId cannot be null")
    private String skuId;

    /**
     * 活动id
     */
    @NotNull(message = "campId cannot be null")
    private Long campId;

    /**
     * 活动类型 （预售，固定价，秒杀，折扣 等）
     */
    private String campType;

    /**
     * 卖家ID
     */
    private Long sellerId;


    /**
     * 订阅类型: 活动开始，活动结束
     *
     *
     */
    private String subType = SubscribeConsts.SUB_TYPE_START;


    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectTrue(null != itemId, I18NMessageUtils.getMessage("itemId is not allowed null"));
        ParamUtil.expectTrue(null != skuId, I18NMessageUtils.getMessage("skuId is not allowed null"));
        ParamUtil.expectTrue(null != campId, I18NMessageUtils.getMessage("campId is not allowed null"));
    }


}
