package com.aliyun.gts.gmall.platform.trade.common.domain;

import lombok.Data;

@Data
public class KVDO<K,V> {
    K key ;
    V value;
}
