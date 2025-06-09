package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

@ApiModel("客户收藏")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerFavouriteVO {

    @ApiModelProperty("用户id")
    Long custId;

    @ApiModelProperty("商家id")
    Long sellerId;

    @ApiModelProperty("商品id")
    Long itemId;

    @ApiModelProperty("sku id")
    Long skuId;

    @ApiModelProperty("创建时间")
    Date gmtCreate;

    @ApiModelProperty("原始价格")
    private Long origin;

    @ApiModelProperty("现在价格")
    private Long price;

    @ApiModelProperty("折扣百分比")
    private String discount;

    @ApiModelProperty("商品名称")
    private String title;

    @ApiModelProperty("商品图片")
    private String picture;

    @ApiModelProperty("贷款信息")
    private String badge;

    @ApiModelProperty("评分")
    private String star;

    @ApiModelProperty("评论数")
    private Long starNum;


    @ApiModelProperty("item状态")
    private Integer status;


    /**
     * 回显折扣
     * @return
     */
    public String getDiscount() {
        if (origin == null || origin == 0 || price == null
                || origin.equals(price)) {
            return null;
        }
        if (origin < price) {
            return null;
        }
        BigDecimal divide = BigDecimal.valueOf(origin - price).divide(BigDecimal.valueOf(origin), 4, RoundingMode.DOWN);
        DecimalFormat decimalFormat = new DecimalFormat("0%");
        return decimalFormat.format(divide.doubleValue());
    }


}
