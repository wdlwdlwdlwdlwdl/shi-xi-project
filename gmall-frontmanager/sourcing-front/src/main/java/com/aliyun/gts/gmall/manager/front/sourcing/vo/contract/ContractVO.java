package com.aliyun.gts.gmall.manager.front.sourcing.vo.contract;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.aliyun.gts.gcai.platform.contract.common.model.FileDO;
import com.aliyun.gts.gcai.platform.contract.common.model.inner.ContractFeature;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractRestRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("合同")
@Data
public class ContractVO extends AbstractCommandRestRequest {

    @ApiModelProperty("合同名称")
    private String name;

    @ApiModelProperty("合同编码")
    private String code;

    @ApiModelProperty("合同模板id")
    private String templateId;

    @ApiModelProperty("模板变量{\"k\":\"v\"}")
    private Map<String,String> templateVariables;

    @ApiModelProperty("合同文档 oss地址")
    private FileDO document;

    @ApiModelProperty("合同状态 0 已生成  -1 已删除  1 已生效")
    private Integer status;

    @ApiModelProperty("合同签署状态 按位与 采购方已签署1、已寄出2、已收到3， 供应商已签署4、已寄出5、已收到6")
    private Long signStatus;

/*    @ApiModelProperty("协议价")
    private PriceAgreement priceAgreement;*/

    @ApiModelProperty("买方用户id")
    private Long buyerId;

    @ApiModelProperty("买方用户名称")
    private String buyerName;

    @ApiModelProperty("卖方用户id")
    private Long sellerId;

    @ApiModelProperty("卖方用户名称")
    private String sellerName;

    ContractFeature feature;

/*    ContractUserFeature buyerFeature;

    ContractUserFeature sellerFeature;*/

    String signStatusDisplay;

    @ApiModelProperty("主健")
    private Long id;
    @ApiModelProperty("更新时间")
    private Date gmtModified;

    Map<String , Boolean> buttons = new HashMap<>();



    protected void addButton(String key){
        buttons.put(key , true);
    }

    @ApiModelProperty("仅同于创建合同时接收前端参数")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date[] validTime;

}
