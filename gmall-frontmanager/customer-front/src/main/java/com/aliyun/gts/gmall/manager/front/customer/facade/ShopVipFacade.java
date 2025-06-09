package com.aliyun.gts.gmall.manager.front.customer.facade;

import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.ShopVipJoinCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.ShopVipQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.ShopVipVO;

public interface ShopVipFacade {

    ShopVipVO queryShopVip(ShopVipQuery query);

    void joinShopVip(ShopVipJoinCommand join);
}
