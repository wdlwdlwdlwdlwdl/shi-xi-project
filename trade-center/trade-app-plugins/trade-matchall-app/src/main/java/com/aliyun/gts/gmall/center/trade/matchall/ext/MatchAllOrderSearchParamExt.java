package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtraMapKeyConstants;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderMethodEnum;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderStatusInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.SellerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchParamExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl.DefaultOrderSearchParamExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.pf4j.Extension;
import org.elasticsearch.action.search.SearchRequest;


import java.util.List;
import java.util.Map;

@Extension(points = {OrderSearchParamExt.class})
@Slf4j
public class MatchAllOrderSearchParamExt extends DefaultOrderSearchParamExt {
    @Override
    public TradeBizResult<SimpleSearchRequest> preProcess(SellerOrderQueryRpcReq req) {

        waitPayOrderStatus(req.getStatusList() );

        TradeBizResult<SimpleSearchRequest> res =  super.preProcess(req);
        //处理下单方式
        Map<String, String> extraMap = req.getExtra();
        if(extraMap!=null&&extraMap.containsKey(ExtraMapKeyConstants.ORDER_METHOD_SEARCH)) {
            String orderMethod = extraMap.get(ExtraMapKeyConstants.ORDER_METHOD_SEARCH);
            //opensearch的愚蠢特性，使用attribute特性时number类型的字段不需要加双引号,string text literal类型的字段需要添加
            //目前SimpleSearchRequest中的filter无法判断字段类型所以需要调用时自行添加
            if(OrderMethodEnum.HELP_ORDER.getName().equals(orderMethod)) {
                res.getData().addEqualFilter("main_tags", "\""+OrderMethodEnum.HELP_ORDER.getName()+"\"");
            } else {
                res.getData().addNotEqualFilter("main_tags", "\""+OrderMethodEnum.HELP_ORDER.getName()+"\"");
            }
        }
        return res;
    }

    @Override
    public TradeBizResult<SimpleSearchRequest> preProcess(CustomerOrderQueryRpcReq req) {
        waitPayOrderStatus(req.getStatusList() );
        return super.preProcess(req);
    }

    /**
     * 等待买家确认打款 也表示未支付
     * @param statusInfos
     */
    private void waitPayOrderStatus(List<OrderStatusInfo>  statusInfos){
        if(statusInfos != null){
            for(OrderStatusInfo statusInfo : statusInfos){
                if(statusInfo.getOrderStatus().equals(OrderStatusEnum.ORDER_WAIT_PAY.getCode())){
                    OrderStatusInfo buyerTransfered = new OrderStatusInfo();
                    buyerTransfered.setOrderStatus(OrderStatusEnum.WAIT_BUYER_TRANSFERED.getCode());
                    statusInfos.add(buyerTransfered);
                    break;
                }
            }
        }
    }

    @Override
    public TradeBizResult<SearchRequest> preEsProcess(CustomerOrderQueryRpcReq req) {
        waitPayOrderStatus(req.getStatusList());
        return super.preEsProcess(req);
    }

    @Override
    public TradeBizResult<SearchRequest> preEsProcess(SellerOrderQueryRpcReq req) {
        waitPayOrderStatus(req.getStatusList() );

        TradeBizResult<SearchRequest> res =  super.preEsProcess(req);
        //处理下单方式
        Map<String, String> extraMap = req.getExtra();
        if(extraMap!=null&&extraMap.containsKey(ExtraMapKeyConstants.ORDER_METHOD_SEARCH)) {
            String orderMethod = extraMap.get(ExtraMapKeyConstants.ORDER_METHOD_SEARCH);
            BoolQueryBuilder boolQueryBuilder = (BoolQueryBuilder) res.getData().source().query();
            if(OrderMethodEnum.HELP_ORDER.getName().equals(orderMethod)) {
                boolQueryBuilder.filter(new TermQueryBuilder("tags", OrderMethodEnum.HELP_ORDER.getName()));
            } else {
                boolQueryBuilder.mustNot(new TermQueryBuilder("tags", OrderMethodEnum.HELP_ORDER.getName()));
            }
        }
        return res;
    }
}
