package com.aliyun.gts.gmall.platform.trade.domain.dataobject.extra;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemEvaluationExtra implements Serializable {

    private List<TcEvaluationDO> itemList;

    private Long total;

}
