package com.aliyun.gts.gmall.platform.trade.domain.util;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.exception.GmallInvalidArgumentException;
import com.aliyun.gts.gmall.framework.server.util.DateTimeUtils;
import com.aliyun.gts.gmall.platform.trade.common.annotation.SearchMapping;
import com.aliyun.gts.gmall.platform.trade.common.searchconvertor.MappingConvertorHandler;
import com.aliyun.gts.gmall.platform.trade.domain.typehandler.MybatisJsonTypeHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SearchConverter {

    private static final Class[] JSON_TYPES;

    static {
        MappedTypes types = MybatisJsonTypeHandler.class.getAnnotation(MappedTypes.class);
        JSON_TYPES = types.value();
    }

    private static final DateTimeFormatter FORMATTER_24 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateTimeFormatter FORMATTER_23 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter FORMATTER_19 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_10 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 转换到DO对象，字段映射规则与数据库mapping相同，搜索表结构与db完全相同时用
     */
    public static void convertDO(Object object, Map<String,Object> map) {
        Method[] methods = object.getClass().getMethods();
        if (methods == null) {
            return;
        }
        for (Method method : methods) {
            if (method.getParameters() == null || method.getParameters().length != 1) {
                continue;
            }
            if (!method.getName().startsWith("set")) {
                continue;
            }
            String fieldName = method.getName().substring(3);
            if (StringUtils.isEmpty(fieldName)) {
                continue;
            }
            fieldName = camelToUnder(fieldName);
            Object value = map.get(fieldName);
            if (value == null) {
                continue;
            }
            Parameter parameter = method.getParameters()[0];
            value = convert(String.valueOf(value), parameter.getType());
            if (value == null) {
                continue;
            }
            try {
                method.invoke(object, value);
            } catch (Exception e) {
                throw new GmallInvalidArgumentException(e);
            }
        }
    }

    private static String camelToUnder(String name) {
        StringBuilder s = new StringBuilder();
        for (int i=0; i<name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c) && s.length() > 0) {
                s.append('_');
            }
            s.append(Character.toLowerCase(c));
        }
        return s.toString();
    }

    private static Object convert(String value, Class<?> type) {
        if (String.class.equals(type)) {
            return value;
        }
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (int.class.equals(type) || Integer.class.equals(type)) {
            return Integer.parseInt(value);
        }
        if (long.class.equals(type) || Long.class.equals(type)) {
            return Long.parseLong(value);
        }
        if (double.class.equals(type) || Double.class.equals(type)) {
            return Double.parseDouble(value);
        }
        if (float.class.equals(type) || Float.class.equals(type)) {
            return Float.parseFloat(value);
        }
        if (byte.class.equals(type) || Byte.class.equals(type)) {
            return Byte.parseByte(value);
        }
        if (short.class.equals(type) || Short.class.equals(type)) {
            return Short.parseShort(value);
        }
        if (char.class.equals(type) || Character.class.equals(type)) {
            return value.charAt(0);
        }
        if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            return "1".equals(value) || "true".equalsIgnoreCase(value);
        }
        if (Date.class.equals(type)) {
            if (NumberUtils.isDigits(value)) {
                return new Date(NumberUtils.toLong(value));
            }
            Date date =  null;
            try {
                switch (value.length()) {
                    case 24:
                        date = DateTimeUtils.parse(value, FORMATTER_24);
                        break;
                    case 23:
                        date = DateTimeUtils.parse(value, FORMATTER_23);
                        break;
                    case 19:
                        date = DateTimeUtils.parse(value, FORMATTER_19);
                        break;
                    case 10:
                        date = DateTimeUtils.parse(value, FORMATTER_10);
                        break;
                    default:
                        // 其他格式暂时不支持
                        break;
                }
            } catch (Exception e) {
            }
            return date;
        }
        if (List.class.equals(type) || ArrayList.class.equals(type)) {
            return JSON.parseArray(value, Object.class);
        }
        Class jsonType = Arrays.stream(JSON_TYPES).filter(clz -> clz.equals(type)).findFirst().orElse(null);
        if (jsonType != null) {
            return JSON.parseObject(value, jsonType);
        }
        return null;
    }


    // ======================================================
    // ======================================================


    static ConcurrentHashMap<String , MappingConvertorHandler> handlerMap = new ConcurrentHashMap();

    /**
     * 转换到任意对象, 目标对象字段用 @SearchMapping 进行配置
     */
    public static void convert(Object object, Map<String,Object> map) {
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field field : fields){
            SearchMapping searchMapping = field.getAnnotation(SearchMapping.class);
            if(searchMapping == null){
                continue;
            }
            if(searchMapping.mapChild()){
                reflectChild(field,map , object);
            }else {
                reflect(searchMapping, object, field, map);
            }
        }
    }

    private static void reflectChild(Field field , Map<String, Object> map , Object superObject){
        try {
            Field[] subFields = field.getType().getDeclaredFields();
            Object object = field.getType().newInstance();
            field.setAccessible(true);
            field.set(superObject , object);
            for (Field f : subFields) {
                SearchMapping searchMapping = f.getAnnotation(SearchMapping.class);
                if(searchMapping == null){
                    //c(f,map,object);
                    continue;
                }else{
                    reflect(searchMapping,object,f,map);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void reflect(SearchMapping searchMapping ,Object object, Field field , Map<String , Object> map){
        if(searchMapping.mapChild()){
            reflectChild(field , map , object);
            return;
        }

        String searchKey = searchMapping.value();
        String handlerName = searchMapping.handler().getName();
        MappingConvertorHandler handler = handlerMap.get(handlerName);
        try {
            if(handler == null){
                handler = searchMapping.handler().newInstance();
                handlerMap.put(handlerName, handler);
            }
            Object value = handler.convert(map.get(searchKey).toString() , field.getType());
            field.setAccessible(true);
            field.set(object , value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
