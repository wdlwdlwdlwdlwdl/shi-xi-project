package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.trade.core.converter.BuyOrdsConverter;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PromotionOrderLimitService;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcItemBuyLimitDO;
import com.aliyun.gts.gmall.center.trade.domain.entity.promotion.BuyOrdsLimit;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcItemBuyLimitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PromotionOrderLimitServiceImpl implements PromotionOrderLimitService {

    @Autowired
    private TcItemBuyLimitRepository tcItemBuyLimitRepository;

    @Autowired
    private BuyOrdsConverter buyOrdsConverter;

    @Value("${trade.buyOrds.tryAttempts:3}")
    private Integer tryAttempts;    // 乐观锁冲突重试次数

    @Override
    public Long queryBuyOrdCnt(BuyOrdsLimit uk) {
        TcItemBuyLimitDO query = buyOrdsConverter.toTcItemBuyLimitDO(uk);
        TcItemBuyLimitDO exist = tcItemBuyLimitRepository.queryByUk(query);
        return exist == null ? 0L : exist.getBuyOrdCnt();
    }

    @Override
    public boolean checkBuyOrds(BuyOrdsLimit limit) {
        limit.setSkuId(0L); // 限制到商品
        TcItemBuyLimitDO query = buyOrdsConverter.toTcItemBuyLimitDO(limit);
        TcItemBuyLimitDO exist = tcItemBuyLimitRepository.queryByUk(query);
        if (exist == null) {
            return true;
        }
        return exist.getBuyOrdCnt() < limit.getOrdsLimit();
    }

    @Override
    public boolean checkAndIncrBuyOrds(BuyOrdsLimit limit) {
        limit.setSkuId(0L); // 限制到商品
        TcItemBuyLimitDO query = buyOrdsConverter.toTcItemBuyLimitDO(limit);

        for (int i = 0; i < tryAttempts; i++) {
            TcItemBuyLimitDO exist = tcItemBuyLimitRepository.queryByUk(query);
            if (exist == null) {
                query.setBuyOrdCnt(1L);
                try {
                    tcItemBuyLimitRepository.create(query);
                    return true;
                } catch (Exception e) {
                    // 并发主键冲突, 重试
                    log.warn("create buyOrds failed {} ", query, e);
                    continue;
                }
            }

            if (exist.getBuyOrdCnt() >= limit.getOrdsLimit()) {
                return false;
            }
            exist.setBuyOrdCnt(exist.getBuyOrdCnt() + 1);
            int up = tcItemBuyLimitRepository.updateByUkVersion(exist);
            if (up > 0) {
                return true;
            } else {
                // 并发更新失败, 重试
                log.warn("update buyOrds failed {} ", exist);
                continue;
            }
        }
        return false;
    }

    @Override
    public void decrBuyOrds(BuyOrdsLimit limit) {
        TcItemBuyLimitDO query = buyOrdsConverter.toTcItemBuyLimitDO(limit);
        for (int i = 0; i < tryAttempts; i++) {
            TcItemBuyLimitDO exist = tcItemBuyLimitRepository.queryByUk(query);
            if (exist == null || exist.getBuyOrdCnt() <= 0) {
                return;
            }
            exist.setBuyOrdCnt(exist.getBuyOrdCnt() - 1);
            int up = tcItemBuyLimitRepository.updateByUkVersion(exist);
            if (up > 0) {
                return;
            } else {
                // 并发更新失败, 重试
                log.warn("update buyOrds failed {} ", exist);
            }
        }
    }
}
