package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * 取消订阅
 */
@Data
public class CustomerCampUnSubscribeCommand extends LoginRestCommand {

    /**
     * 订阅id
     */
    @NotNull(message = "subId cannot be null")
    private Long id;


    /**
     * 商品id
     */
//    @NotNull(message = "itemId cannot be null")
    private Long itemId;

    /**
     * sku_id
     */
//    @NotNull(message = "skuId cannot be null")
    private String skuId;

    /**
     * 活动id
     */
//    @NotNull(message = "campId cannot be null")
    private Long campId;


    /**
     * 卖家ID
     */
    private Long sellerId;




    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectTrue(null != id, I18NMessageUtils.getMessage("subscribeID is not allowed null"));
    }




}
