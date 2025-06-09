package com.aliyun.gts.gmall.manager.front.sourcing.vo.pr;

import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingMaterialVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("采购单详情")
public class PurchaseRequestDetailVO extends PurchaseRequestBaseVO{

    @ApiModelProperty("收货地址")
    String address;
    @ApiModelProperty("预算")
    String budget;
    @ApiModelProperty("描述")
    String description;
    @ApiModelProperty("手机")
    String cellphone;
    @ApiModelProperty("座机")
    String telephone;
    @ApiModelProperty("邮箱")
    String email;

    List<SourcingMaterialVo> materialList;

}
