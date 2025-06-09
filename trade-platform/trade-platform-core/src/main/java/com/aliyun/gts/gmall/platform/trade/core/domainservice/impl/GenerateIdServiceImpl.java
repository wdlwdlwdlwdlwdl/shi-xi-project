package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.sequence.Sequence;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.GenerateIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenerateIdServiceImpl implements GenerateIdService {

    private static final long CUST_NUM_MASK = 10000;
    private static final long IDX_NUM_MASK = 1000;

    @Autowired
    private Sequence sequence;

    // 生成规则: [seq] + [3位序列] + [4位custId尾号]
    @Override
    public List<Long> nextOrderIds(Long custId, int count) {
        long seq = sequence.nextValue("order_id");
        List<Long> list = new ArrayList<>();
        for (int i = 0; i <= count; i++) {
            long id = (seq * IDX_NUM_MASK + i) * CUST_NUM_MASK + (custId % CUST_NUM_MASK);
            list.add(id);
        }
        return list;
    }

    // 生成规则: [seq]
    @Override
    public String nextDisplayOrderIds() {
        long seq = sequence.nextValue("display_order_id");
        return String.format("%010d", seq);
    }

    // 生成规则: [seq] + [3位序列] + [4位custId尾号]
    @Override
    public List<Long> nextReversalIds(Long custId, int count) {
        long seq = sequence.nextValue("reversal_id");
        List<Long> list = new ArrayList<>();
        for (int i = 0; i <= count; i++) {
            long id = (seq * IDX_NUM_MASK + i) * CUST_NUM_MASK + (custId % CUST_NUM_MASK);
            list.add(id);
        }
        return list;
    }

    // 生成规则: [seq] + [4位custId尾号]
    @Override
    public Long nextRefundId(Long custId) {
        long seq = sequence.nextValue("refund_id");
        return seq * CUST_NUM_MASK + (custId % CUST_NUM_MASK);
    }

    // 生成规则: [seq] + [4位custId尾号]
    @Override
    public Long nextRefundDetailId(Long custId) {
        long seq = sequence.nextValue("refund_detail_id");
        return seq * CUST_NUM_MASK + (custId % CUST_NUM_MASK);
    }
}
