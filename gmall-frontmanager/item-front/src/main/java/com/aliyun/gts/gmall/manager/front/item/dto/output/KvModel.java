package com.aliyun.gts.gmall.manager.front.item.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KvModel <K, V>{
    private K key;
    private V value;
}
