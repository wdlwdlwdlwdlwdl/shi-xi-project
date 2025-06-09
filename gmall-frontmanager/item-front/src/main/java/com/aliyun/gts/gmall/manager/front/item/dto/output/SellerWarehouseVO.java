package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.item.api.dto.output.warehouse.WarehouseBusinessHoursDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 商家仓库地址详情模型
 */
@Data
public class SellerWarehouseVO extends AbstractOutputInfo{

    @ApiModelProperty("仓库ID")
    private Long id;

    @ApiModelProperty("仓库名称")
    private String name;

    @ApiModelProperty("仓库地址")
    private String address;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("省")
    private String province;

    @ApiModelProperty("国家")
    private String country;

    @ApiModelProperty("销售点电话")
    private String telephone;

    @ApiModelProperty("状态,1:激活,0:停用")
    private Integer status;

    @ApiModelProperty("自提选项 1:启用,0:禁用")
    private Integer pickUp;

    @ApiModelProperty("商家主键")
    private Long sellerId;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("经度")
    private Double longitude;

    @ApiModelProperty("纬度")
    private Double latitude;

    List<WarehouseBusinessHoursVO> warehouseBusinessHoursList;

    @ApiModelProperty("营业时间")
    private String businessHours;
}
