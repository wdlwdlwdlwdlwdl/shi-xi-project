package com.aliyun.gts.gmall.platform.trade.common.searchconvertor;

public abstract class MappingConvertorHandler {

    public Object convert(String source, Class type){
        if(source == null || "".equals(source.trim())){
            return null;
        }
        return innerConvert(source,type);

    }

    abstract Object innerConvert(String source, Class type);

}
