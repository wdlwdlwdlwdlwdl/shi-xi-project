package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 商品详情页面信息
 *
 * @author tiansong
 */
@Data
@ApiModel("商品分页信息")
public class ItemPageVO extends AbstractOutputInfo {

    @ApiModelProperty(value = "商品id")
    private Long id;
    @ApiModelProperty(value = "商家id")
    private Long sellerId;
    @ApiModelProperty(value = "sku主键")
    private Long skuId;
    @ApiModelProperty(value = "商品价格")
    private Long price;

    @ApiModelProperty(value = "商品原价")
    private Long originalP;

    @ApiModelProperty(value = "商品标题")
    private String content;

    @ApiModelProperty(value = "商品折扣")
    private String discount;

    @ApiModelProperty(value = "商品轮播图片")
    private List<String> merchandiseList;

    @ApiModelProperty(value = "商品点赞")
    private Long star;

    @ApiModelProperty(value = "评论")
    private String review;

    @ApiModelProperty(value="分期")
    private String badge;

    @ApiModelProperty(value="商品图片")
    private String picture;



}
