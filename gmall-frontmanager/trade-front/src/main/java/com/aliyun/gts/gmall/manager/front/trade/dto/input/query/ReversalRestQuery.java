package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 售后服务列表请求
 *
 * @author tiansong
 */
@ApiModel("售后服务列表请求")
@Data
public class ReversalRestQuery extends PageLoginRestQuery {
    @ApiModelProperty("商品名称关键字")
    private String        itemTitle;
    @ApiModelProperty("主订单id")
    private Long          primaryOrderId;
    @ApiModelProperty("查询结果是否包含订单数据")
    private Boolean       includeOrderInfo;
    @ApiModelProperty("售后单状态集, ReversalStatusEnum")
    private List<Integer> reversalStatus;

    @Override
    public void checkInput() {
        super.checkInput();
        // 针对搜索的内容进行特殊处理
        if (StringUtils.isNotBlank(itemTitle) && itemTitle.length() > BizConst.ORDER_ID_LIMIT_LENGTH
            && NumberUtils.isDigits(itemTitle)) {
            Long primaryOrderId = NumberUtils.toLong(itemTitle);
            if (primaryOrderId > 0L) {
                this.setPrimaryOrderId(primaryOrderId);
                this.setItemTitle(null);
            }
        }
        // reset
        this.getPage().setPageSize(BizConst.PAGE_SIZE);
        if (includeOrderInfo == null) {
            // 默认包含订单信息
            includeOrderInfo = Boolean.TRUE;
        }
    }
}
