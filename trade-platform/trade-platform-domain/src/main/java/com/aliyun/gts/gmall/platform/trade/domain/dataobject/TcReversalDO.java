package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.typehandler.MybatisArrayListTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("tc_reversal")
public class TcReversalDO implements Serializable {

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分区键, 值为 custId、primaryOrderId、orderId、primaryReversalId、reversalId 的末4位
     */
    private Integer routerId;

    /**
     * 订单业务id
     */
    private Long orderId;

    /**
     * 主订单业务id
     */
    private Long primaryOrderId;

    /**
     * 售后单业务id
     */
    private Long reversalId;

    /**
     * 售后主单业务id
     */
    private Long primaryReversalId;

    /**
     * 是否售后主单：0-否，1-是
     */
    private Boolean isReversalMain;

    /**
     * 购买用户的id，分库分表健
     */
    private Long custId;

    /**
     * 购买者姓名
     */
    private String custName;

    /**
     * 售后类型：1-仅退款、2-退货退款  ReversalTypeEnum
     */
    private Integer reversalType;

    /**
     * 售后单状态: 待审核,审核不通过,审核通过(审核通过状态去掉),回收中,退货完成,待退款,退款中,退款完成,售后完成,售后关闭
     * ReversalStatusEnum
     */
    private Integer reversalStatus;

    /**
     * 售后原因ID
     */
    private Integer reversalReason;

    /**
     * 退款备注，记录客户退款申请时的备注信息
     */
    private String custMemo;

    /**
     * 售后证据图片等媒体链接
     */
    @TableField(typeHandler = MybatisArrayListTypeHandler.class)
    private List<String> custMedias;

    /**
     * 商家ID
     */
    private Long sellerId;

    /**
     * 供应商名称
     */
    private String sellerName;

    /**
     * 卖家备注
     */
    private String sellerMemo;

    /**
     * 卖家举证图片等媒体链接
     */
    @TableField(typeHandler = MybatisArrayListTypeHandler.class)
    private List<String> sellerMedias;

    /**
     * 商品标识
     */
    private Long itemId;

    /**
     * 商品SKU
     */
    private Long skuId;

    /**
     * 售后单创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * 退款完成时间
     */
    private Date reversalCompletedTime;

    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    /**
     * 售后属性
     */
    private ReversalFeatureDO reversalFeatures;

    /**
     * 退换数量
     */
    private Integer cancelQty;

    /**
     * 取消金额
     */
    private Long cancelAmt;

    /**
     * 申请售后渠道编码,枚举同tc_order表order_channel
     * OrderChannelEnum
     */
    private String reversalChannel;

    /**
     * 版本号
     */
    @TableField(update="%s+1",updateStrategy=FieldStrategy.IGNORED)
    private Long version;

    private String firstName;

    private String lastName;

    private String binOrIin;
}
