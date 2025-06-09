package com.aliyun.gts.gmall.center.trade.domain.ext.cartAdd;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCartDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUk;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUkBatch;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcCartRepository;

import java.util.List;

public interface TcCartRepositoryExt extends TcCartRepository {

    /**
     * 查询购物车
     * @param uk
     * @return
     */
    TcCartDO queryByUk(CartItemUk uk);

    /**
     * 查询购物车
     * @param ukBatch
     * @return
     */
    List<TcCartDO> queryBatch(CartItemUkBatch ukBatch);
}
