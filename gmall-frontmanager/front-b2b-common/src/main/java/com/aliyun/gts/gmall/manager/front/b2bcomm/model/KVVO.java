package com.aliyun.gts.gmall.manager.front.b2bcomm.model;

import lombok.Data;

@Data
public class KVVO<K,V> {
    K key ;
    V value;

    public KVVO(K key , V value){
        this.key = key;
        this.value = value;
    }
    public KVVO(){

    }
}
