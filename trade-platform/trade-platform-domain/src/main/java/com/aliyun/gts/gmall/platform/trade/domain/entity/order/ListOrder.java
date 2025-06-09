package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import lombok.Data;

@Data
public class ListOrder extends AbstractBusinessEntity {

    List<MainOrder> orderList = new ArrayList<>();

    Integer total;

    List<Map<String,Object>> searchList;

}
