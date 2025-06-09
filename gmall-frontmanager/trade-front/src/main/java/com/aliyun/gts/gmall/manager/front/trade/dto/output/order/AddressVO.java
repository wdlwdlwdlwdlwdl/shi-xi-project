package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 收货地址
 *
 * @author tiansong
 */
@Data
@ApiModel("收货地址")
public class AddressVO extends OrderExtendVO {
    @ApiModelProperty("自增ID")
    private Long id;
    @ApiModelProperty("买家ID")
    private Long custId;
    @ApiModelProperty("省")
    private String province;
    @ApiModelProperty("市")
    private String city;
    @ApiModelProperty("区")
    private String area;
    @ApiModelProperty("省ID")
    private Long provinceId;
    @ApiModelProperty("市ID")
    private Long cityId;
    @ApiModelProperty("区ID")
    private Long areaId;
    @ApiModelProperty("详细地址信息")
    private String addressDetail;
    @ApiModelProperty("完整地址")
    private String completeAddr;
    @ApiModelProperty("邮编")
    private String postCode;
    @ApiModelProperty("联系人姓名")
    private String name;
    @ApiModelProperty("联系人手机")
    private String phone;
    @ApiModelProperty("是否默认地址")
    private Boolean defaultYn;

    @ApiModelProperty("首名称")
    private String firstName;

    @ApiModelProperty("最后名称")
    private String lastName;

    @ApiModelProperty("中间名称")
    private String middleName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("纬度")
    private String latitude;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("邮箱地址")
    private String email;
    
}