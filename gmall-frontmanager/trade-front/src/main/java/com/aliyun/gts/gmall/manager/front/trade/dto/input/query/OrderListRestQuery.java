package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.OrderTabEnum;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.OrderUtils;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderEvaluateEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 订单列表查询
 *
 * @author tiansong
 */
@Data
@ApiModel("订单列表查询")
public class OrderListRestQuery extends PageLoginRestQuery {
    @ApiModelProperty("TAB类型")
    private OrderTabEnum orderTab;
    @ApiModelProperty("商品名称关键字")
    private String   itemTitle;
    @ApiModelProperty("是否评价, OrderEvaluateEnum")
    private Integer  evaluate;
    @ApiModelProperty("状态列表")
    private List<String> statusList;
    @ApiModelProperty("主订单ID列表")
    private List<Long>  primaryOrderIds;

    @Override
    public void checkInput() {
        super.checkInput();
        // 针对搜索的内容进行特殊处理
        if (StringUtils.isNotBlank(itemTitle) && itemTitle.length() > BizConst.ORDER_ID_LIMIT_LENGTH
            && NumberUtils.isDigits(itemTitle)) {
            Long primaryOrderId = NumberUtils.toLong(itemTitle);
            if (primaryOrderId > 0L) {
                List<Long> orderIdList = Lists.newArrayList();
                orderIdList.add(primaryOrderId);
                this.setPrimaryOrderIds(orderIdList);
                this.setItemTitle(null);
            }
        }
        // 查询全部,后续只能设置和TAB相关的数据
        if (orderTab == null) {
            return;
        }
        if (OrderTabEnum.PENDING_EVALUATION.equals(orderTab)) {
            this.evaluate = OrderEvaluateEnum.NOT_EVALUATE.getCode();
        }
        this.statusList = OrderUtils.getOrderStatusList(orderTab.getCode());
    }
}
