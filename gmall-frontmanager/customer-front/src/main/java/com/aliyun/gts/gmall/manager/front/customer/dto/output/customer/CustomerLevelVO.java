package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户等级
 *
 * @author tiansong
 */
@ApiModel("用户等级")
@Data
public class CustomerLevelVO {
    @ApiModelProperty("是否用户当前等级")
    private Boolean currentLevel;
    @ApiModelProperty("用户等级")
    private Integer level;
    @ApiModelProperty("用户等级展示名称")
    private String  name;
    @ApiModelProperty("用户等级有效期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date    levelExpireDate;
    @ApiModelProperty("用户成长值")
    private Integer custGrowthSum;

    /**
     * 用户等级配置
     */
    @ApiModelProperty("保持等级的最小成长值")
    private Long                      minGrowth;
    @ApiModelProperty("保持等级的最大成长值")
    private Long                      maxGrowth;
    @ApiModelProperty("用户等级规则描述")
    private String                    descUrl;
    @ApiModelProperty("会员权益列表")
    private List<CustomerPromotionVO> customerPromotionVO;
    @ApiModelProperty("会员权益-优惠券列表")
    private List<Long> coupons;
}
