package com.aliyun.gts.gmall.center.trade.deposit.ext;

import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonPriceCalcExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.price.PriceCalcExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubPrice;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

@Slf4j
@Extension(points = {PriceCalcExt.class})
public class DepositPriceCalcExt extends CommonPriceCalcExt {

    @Override
    public long getOrderFactorSum(MainOrder main){
        return NumUtils.getNullZero(main.getOrderPrice().getOrderPromotionAmt());
    }

    @Override
    public long getOrderFreightAmt(SubPrice sp){
        //2 == SubPrice.TYPE_FREIGHT
        if(sp.getType() == 2){
            return 0L;
        }else{
            return super.getOrderFreightAmt(sp);
        }
    }



}
