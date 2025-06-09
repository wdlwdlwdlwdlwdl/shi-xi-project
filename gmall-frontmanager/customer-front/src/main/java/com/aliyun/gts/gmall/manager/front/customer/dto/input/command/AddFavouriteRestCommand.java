package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author GTS
 * @date 2021/03/12
 */
@Data
public class AddFavouriteRestCommand extends LoginRestCommand {

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
    public Long  skuId;

    /**
     * 商品skuname不能为空
     */
    @NotNull(message = "商品skuname不能为空")
    private String skuName;

    /**
     *  商品主图
     */
    @NotNull(message = "商品主图")
    private String skuImages;

    /**
     *  商品评分
     */
    @ApiModelProperty(value = "商品评分")
    private String rateScore;

    /**
     *  评论
     */
    @ApiModelProperty(value = "评论")
    private String reviewNumber;

    /**
     *  分期
     */
    @ApiModelProperty(value="分期")
    private String skuInstalmentNumber;

    /**
     *  商品折扣
     */
    @ApiModelProperty(value = "商品折扣")
    private String skuDiscount;

    /**
     *  商品价格
     */
    @ApiModelProperty(value = "商品价格")
    private Long skuPrice;


    @Override
    public void checkInput() {
        super.checkInput();
    }
}