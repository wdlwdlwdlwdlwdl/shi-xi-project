package com.aliyun.gts.gmall.platform.trade.common.searchconvertor;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import com.google.gson.Gson;

public class IntegerEnumHandler extends MappingConvertorHandler {

    Gson gson = new Gson();

    @Override
    Object innerConvert(String source, Class type) {
        return GenericEnum.fromCode(type , Integer.parseInt(source));
    }
}
