package com.aliyun.gts.gmall.platform.trade.common.searchconvertor;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonHandler extends MappingConvertorHandler {


    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }
    }).create();



    @Override
    Object innerConvert(String source, Class type) {
        return gson.fromJson(source,type);
    }
}
