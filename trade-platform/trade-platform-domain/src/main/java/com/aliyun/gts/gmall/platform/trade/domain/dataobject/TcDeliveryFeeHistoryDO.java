package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tc_delivery_fee_history")
public class TcDeliveryFeeHistoryDO implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("code")
    private String feeCode;

    @ApiModelProperty("deliveryRoute")
    private Integer deliveryRoute;

    @ApiModelProperty("类目")
    private String categoryId;

    @ApiModelProperty("类目名称")
    private String categoryName;

    @ApiModelProperty("商户编码")
    private String merchantCode;

    @ApiModelProperty("商户名称")
    private String merchantName;

    @ApiModelProperty("物流类型")
    private Integer deliveryType;

    @ApiModelProperty("删除标记")
    private Integer deleted;

    @ApiModelProperty("操作类型")
    private Integer type;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("create_id")
    private String createId;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("update_id")
    private String updateId;

    private String operator;

    private Integer active;

    private String path;

    @ApiModelProperty("是否类目全部")
    private String isCategoryAll;

    @ApiModelProperty("是否卖家全部")
    private String isMerchantAll;

}
