package com.aliyun.gts.gmall.platform.trade.domain.entity.reversal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

@Data
public class ReversalDbQuery {

    @ApiModelProperty("顾客ID")
    private Long custId;

    @ApiModelProperty(value = "主订单id")
    private Long primaryOrderId;

    @ApiModelProperty(value = "主售后单id")
    private Long primaryReversalId;

    @ApiModelProperty(value = "售后单状态集, ReversalStatusEnum, filter")
    private List<Integer> reversalStatus;

    @ApiModelProperty(value = "分页num, 1开始")
    private Integer pageNum;

    @ApiModelProperty(value = "分页size")
    private Integer pageSize;

    @ApiModelProperty(value = "卖家ID, filter")
    private Long sellerId;

    @ApiModelProperty("客户姓氏")
    private String customerLastName;

    @ApiModelProperty("客户名字")
    private String customerFirstName;

    @ApiModelProperty("卖家BIN")
    private String sellerBin;

    @ApiModelProperty("卖家名称")
    private String sellerName;

    @ApiModelProperty(value = "售后单创建时间范围")
    private DateRangeParam createTime;


    // =============== 搜索查询条件 --> db查询条件 =================

    private static final Set<String> IGNORE_FIELDS = Sets.newHashSet("includeOrderInfo",
            "custId", "pageNum", "pageSize", "primaryOrderId", "primaryReversalId", "customerLastName",
            "customerFirstName", "sellerBin", "sellerName", "createTime", "reversalStatus");

    public static boolean isDbSupport(ReversalSearchQuery q) {
        if (q.getPrimaryReversalId() != null) {
            return true;
        }
        JSONObject json = (JSONObject) JSON.toJSON(q);
        for (Entry<String, Object> en : json.entrySet()) {
            if (isNotEmpty(en.getValue()) && !IGNORE_FIELDS.contains(en.getKey())) {
                return false;
            }
        }
        return q.getCustId() != null || q.getPrimaryOrderId() != null || q.getPrimaryReversalId() != null;
    }

    private static boolean isNotEmpty(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String) {
            return StringUtils.isNotBlank((String) value);
        }
        return true;
    }

    public static ReversalDbQuery toDbQuery(ReversalSearchQuery q) {
        ReversalDbQuery dbQuery = new ReversalDbQuery();
        BeanUtils.copyProperties(q, dbQuery);
        return dbQuery;
    }
}
