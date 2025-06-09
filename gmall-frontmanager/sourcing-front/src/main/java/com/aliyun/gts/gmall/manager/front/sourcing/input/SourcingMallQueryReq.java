package com.aliyun.gts.gmall.manager.front.sourcing.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.common.input.query.MaterialNumQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.SourcingQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.type.ApplyType;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingStatus;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractPageQueryRestRequest;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 采购需求大厅搜索条件
 */
@Data
public class SourcingMallQueryReq extends AbstractPageQueryRestRequest {

    private String title;               // 标题
    private List<Long> categoryList;    // 类目多选
    private String payTimeLimitType;    // see FulfillConstants
    private String quantityUnit;    // I18NMessageUtils.getMessage("purchase.qty")+"-"+I18NMessageUtils.getMessage("unit")  //# 采购量-单位
    private Long quantityMin;       // I18NMessageUtils.getMessage("purchase.qty")-最小  //# 采购量
    private Long quantityMax;       // I18NMessageUtils.getMessage("purchase.qty")-最大  //# 采购量
    private String receiveProv;     // 收货地址省
    private Integer sortType;       // see SourcingQuery

    public SourcingQuery build() {
        SourcingQuery query = new SourcingQuery();
        query.setApplyType(ApplyType.APPLY.getValue()); // 公开报名
        query.setStatusList(Lists.newArrayList(SourcingStatus.wait_apply.getValue() ,
                SourcingStatus.in_apply.getValue(), SourcingStatus.wait_quote.getValue(),
                SourcingStatus.in_quote.getValue(), SourcingStatus.tendering.getValue(),
                SourcingStatus.tender.getValue()));
        Date now = new Date();
        query.setStartTimeLe(now);
        query.setEndTimeGe(now);

        query.setPage(getPage());
        query.setTitle(title);
        query.setCategoryIds(categoryList);
        if (StringUtils.isNotBlank(payTimeLimitType)) {
            query.setPayTimeLimitType(Lists.newArrayList(payTimeLimitType));
        }
        if (StringUtils.isNotBlank(quantityUnit)) {
            MaterialNumQuery num = new MaterialNumQuery();
            num.setUnit(quantityUnit);
            num.setMinRangeStart(quantityMin);
            num.setSumRangeEnd(quantityMax);
            query.setMaterialNumQuery(num);
        }
        if (StringUtils.isNotBlank(receiveProv)) {
            query.setContactAddress(Lists.newArrayList(receiveProv));
        }
        query.setSortType(sortType);
        return query;
    }
}
