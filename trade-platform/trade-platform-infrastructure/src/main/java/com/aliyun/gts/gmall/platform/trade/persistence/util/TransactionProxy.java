package com.aliyun.gts.gmall.platform.trade.persistence.util;

import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class TransactionProxy {

    @Autowired
    private PlatformTransactionManager transactionManager;

    public void callTx(Runnable run) {
        TransactionStatus tr = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            run.run();
            transactionManager.commit(tr);
        } catch (Throwable t) {
            transactionManager.rollback(tr);
            ErrorUtils.throwUndeclared(t);
        }
    }
}
