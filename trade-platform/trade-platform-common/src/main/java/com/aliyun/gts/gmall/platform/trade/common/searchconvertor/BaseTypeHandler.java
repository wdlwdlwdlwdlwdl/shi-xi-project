package com.aliyun.gts.gmall.platform.trade.common.searchconvertor;

import java.util.Date;

public class BaseTypeHandler extends MappingConvertorHandler {

    @Override
    Object innerConvert(String source, Class type) {
        if(source == null){
            return null;
        }
        if(String.class.equals(type)){
            return source;
        }
        if(Boolean.class.equals(type) ){
            return "1".equals(source);
        }
        if(Long.class.equals(type) ){
            return Long.parseLong(source);
        }
        if(Integer.class.equals(type) ){
            return Integer.parseInt(source);
        }
        if(Double.class.equals(type) ){
            return Double.parseDouble(source);
        }
        if(Float.class.equals(type) ){
            return Float.parseFloat(source);
        }
        if(Date.class.equals(type)){
            Long timeInMillSec = Long.valueOf(source);
            Date date = new Date();
            date.setTime(timeInMillSec);
            return date;
        }
        return null;
    }
}
