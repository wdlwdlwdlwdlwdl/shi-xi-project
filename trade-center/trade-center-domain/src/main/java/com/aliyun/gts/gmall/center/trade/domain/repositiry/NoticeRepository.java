package com.aliyun.gts.gmall.center.trade.domain.repositiry;

import com.aliyun.gts.gmall.center.trade.domain.entity.notice.NoticeMessage;

public interface NoticeRepository {

    void publish(NoticeMessage message);
}
