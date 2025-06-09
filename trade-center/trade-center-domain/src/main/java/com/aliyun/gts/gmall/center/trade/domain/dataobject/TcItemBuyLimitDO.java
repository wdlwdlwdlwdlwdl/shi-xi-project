package com.aliyun.gts.gmall.center.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tc_item_buy_limit")
public class TcItemBuyLimitDO {

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private Long itemId;

    /**
     *
     */
    private Long skuId;

    /**
     *
     */
    private Long campId;

    /**
     *
     */
    private Long custId;

    /**
     *
     */
    private Long buyOrdCnt;

    /**
     *
     */
    private Date gmtCreate;

    /**
     *
     */
    private Date gmtModified;

    /**
     *
     */
    private String features;

    /**
     *
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long uselessKey;

    /**
     *
     */
    private Long version;
}
