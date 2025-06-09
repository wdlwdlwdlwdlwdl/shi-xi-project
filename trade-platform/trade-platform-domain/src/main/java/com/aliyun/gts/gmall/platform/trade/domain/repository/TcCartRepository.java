package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCartDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;

import java.util.List;

public interface TcCartRepository {

    /**
     * 全量查询
     * @param custId
     * @param cartType
     * @return
     */
    List<TcCartDO> queryAll(Long custId, Integer cartType);

    /**
     * 全量查询
     * @param custId
     * @param cartType
     * @return
     */
    List<TcCartDO> queryPayMode(Long custId, Integer cartType, String payMode);

    /**
     * 用户购物车卡数量总和
     * @param custId
     * @param cartType
     * @return int
     */
    int queryCount(Long custId, Integer cartType);

    /**
     * 单商品查询 -- 是否已经加车
     * @param custId
     * @param cartType
     * @param itemSkuId
     * @return TcCartDO
     */
    TcCartDO queryByUk(Long custId, Integer cartType, ItemSkuId itemSkuId);

    List<TcCartDO> queryBatch(Long custId, Integer cartType, List<ItemSkuId> itemSkuId);

    List<TcCartDO> queryBatch(Long custId, Long sellerId, String payMode, List<ItemSkuId> itemSkuId);

    void create(TcCartDO cart);

    void updateById(TcCartDO cart);

    void deleteById(Long id);

    /**
     * 批量删除
     * @param deleteCartIds
     */
    void deleteByIds(List<Long> deleteCartIds);

}
