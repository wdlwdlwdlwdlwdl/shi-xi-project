package com.aliyun.gts.gmall.platform.trade.domain.entity.reversal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReversalSearchResult {

    private List<MainReversal> list;

    private int total;
}
