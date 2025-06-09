package com.aliyun.gts.gmall.platform.trade.core.sequence;

import com.aliyun.gts.gmall.framework.sequence.Sequence;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeqIdentifierGenerator implements IdentifierGenerator {

    @Autowired
    private Sequence shardSequence;

    @Override
    public Number nextId(Object entity) {
        return shardSequence.nextValue("common_id");
    }
}
