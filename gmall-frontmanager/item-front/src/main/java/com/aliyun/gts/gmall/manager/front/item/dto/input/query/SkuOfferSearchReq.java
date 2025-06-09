package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractPageQueryRestRequest;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 
 * @Title: SkuOfferSearchReq.java
 * @Description: 商品sku引用列表
 * @author zhao.qi
 * @date 2024年9月25日 10:23:17
 * @version V1.0
 */
@Getter
@Setter
public class SkuOfferSearchReq extends AbstractPageQueryRestRequest {
    private static final long serialVersionUID = 1L;

    private static List<String> SORT_COLUMES = List.of("gmt_create", "gmt_modified");

    /** 商品引用ids */
    private List<Long> skuQuoteIdList;

    /** 商品ids */
    private List<Long> itemIdList;

    /** 商品sku名称 */
    private String title;

    /** 类目Id */
    private Long catagoryId;

    /** map状态 */
    private Integer mapStatus;

    /** 排序 */
    private List<SearchOrderThirdRest> orders;

    private Long skuId;

    private List<Long> skuIdList;

    private String cityCode;

    private Long cityId;
    @ApiModelProperty(value = "渠道信息", required = true)
    private String channel;
    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? null : user.getCustId();
    }

    public void setCustId(Long v) { }

    @ApiModelProperty("优惠来源")
    private String promotionSource;

    @Override
    public void checkInput() {
        if (CollectionUtils.isNotEmpty(orders)) {
            orders.forEach(order -> {
                if (!SORT_COLUMES.contains(order.getField())) {

                }
                if (!"asc".equals(order.getDirection()) || !"desc".equals(order.getDirection())) {

                }
            });
        }
    }
}
