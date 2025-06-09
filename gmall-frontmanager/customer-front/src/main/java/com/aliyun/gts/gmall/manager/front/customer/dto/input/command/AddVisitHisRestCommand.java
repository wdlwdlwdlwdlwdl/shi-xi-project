package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author GTS
 * @date 2021/03/12
 */
@Data
public class AddVisitHisRestCommand extends LoginRestCommand {

    /**
     * 卖家id;尽可能传过来
     */
//    @NotNull(message = "卖家ID不能为空")
    public  Long  sellerId;

    /**
     * 卖家id;尽可能传过来
     */
    @NotNull(message = "商品id不能为空")
    public  Long  itemId;

    /**
     * 卖家id;尽可能传过来
     */
    @NotNull(message = "商品skuId不能为空")
    public  Long  skuId;

    @Override
    public void checkInput() {
        super.checkInput();
    }
}