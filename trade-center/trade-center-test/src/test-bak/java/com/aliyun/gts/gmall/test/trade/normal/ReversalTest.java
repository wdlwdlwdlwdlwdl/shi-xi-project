package com.aliyun.gts.gmall.test.trade.normal;

import com.aliyun.gts.gmall.center.trade.api.dto.output.ReversalCombItemDTO;
import com.aliyun.gts.gmall.center.trade.common.constants.ItemConstants;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.common.util.JsonUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CheckReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalSubOrderInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalSubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.test.trade.base.BaseOrderTest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/25 17:10
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReversalTest extends BaseOrderTest {

    @Autowired
    protected ReversalWriteFacade reversalWriteFacade;

    @Autowired
    protected ReversalReadFacade reversalReadFacade;

    @Test
    public void t018_组合商品确认退款() {
        CheckReversalRpcReq check = new CheckReversalRpcReq();
        check.setCustId(100007L);
        check.setReversalChannel(OrderChannelEnum.H5.getCode());
        check.setPrimaryOrderId(13590020000007L);
        RpcResponse<List<ReversalSubOrderDTO>>  list = reversalReadFacade.checkReversal(check);
        System.out.print(list);
    }


    @Test
    public void t019_组合商品退款() {
        CreateReversalRpcReq create = new CreateReversalRpcReq();
        create.setCustId(100007L);
        create.setReversalChannel(OrderChannelEnum.H5.getCode());
        create.setPrimaryOrderId(13590020000007L);
        create.setReversalReasonCode(101);
        create.setReversalType(ReversalTypeEnum.REFUND_ONLY.getCode());
        create.setItemReceived(false);

        List<ReversalSubOrderInfo> subOrders = new ArrayList<>();
        ReversalSubOrderInfo info = new ReversalSubOrderInfo();
        info.setOrderId(13590020010007L);
        info.setCancelAmt(30000L);
        info.setCancelQty(0);
        info.setExtra(combineItem());
        subOrders.add(info);
        create.setSubOrders(subOrders);
        RpcResponse<Long> response = reversalWriteFacade.createReversal(create);
        System.out.print(response);
    }

    private static Map<String, String> combineItem(){
        List<ReversalCombItemDTO> list = new ArrayList<>();
        ReversalCombItemDTO dto = new ReversalCombItemDTO();
        dto.setCancelQty(1);
        dto.setSkuId(66778L);
        dto.setItemId(5796L);
        list.add(dto);
        Map<String, String> maps = new HashMap<>();
        maps.put(ItemConstants.COMBINE_ITEM, JsonUtils.toJSONString(list));
        return maps;
    }
}
