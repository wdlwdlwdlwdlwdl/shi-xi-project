package com.aliyun.gts.gmall.center.trade.domain.entity.point;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import lombok.Data;

import java.util.Date;

@Data
public class FixedPointItem {

    // 主键
    private Long id;
    // 兑换目标
    private ItemSkuId targetId;
    // 消耗积分 数量 (原子积分)
    private Long pointCount;
    // 现金 (单元分)
    private Long realPrice;
    // 扩展字段
    private JSONObject features;
    // 创建时间
    private Date gmtCreate;
    // 修改时间
    private Date gmtModified;
}
