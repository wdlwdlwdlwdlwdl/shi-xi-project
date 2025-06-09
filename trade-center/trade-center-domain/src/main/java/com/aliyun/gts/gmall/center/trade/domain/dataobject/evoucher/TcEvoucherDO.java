package com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tc_evoucher")
public class TcEvoucherDO {

    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("子订单ID")
    private Long orderId;

    @ApiModelProperty("生效时间")
    private Date startTime;

    @ApiModelProperty("失效时间")
    private Date endTime;

    @ApiModelProperty("电子凭证码")
    private Long evCode;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("版本号")
    @TableField(update="%s+1",updateStrategy= FieldStrategy.IGNORED)
    private Integer version;

    @ApiModelProperty("扩展字段")
    private String features;

    @JSONField(serialize = false)
    public EvoucherFeatureDO getFeaturesDO() {
        return EvoucherFeatureDO.parse(features);
    }

    @JSONField(deserialize = false)
    public void setFeaturesDO(EvoucherFeatureDO features) {
        this.features = features.toFeatures();
    }


    // 买卖方信息

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家（店铺）名称")
    private String sellerName;

    @ApiModelProperty("顾客ID")
    private Long custId;

    @ApiModelProperty("顾客名称")
    private String custName;
}
