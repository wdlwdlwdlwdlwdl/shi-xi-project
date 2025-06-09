package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/* 
* @Description: 
* @Author: haibin.xhb 
* @Date:  2021/1/16 18:57
*/
public class JsonUtils {
    /**
     * java对象转为jsonObject
     *
     * @param object
     * @return
     */
    public static JSONObject toJsonObject(Object object) {
        return JSON.parseObject(JSON.toJSONString(object));
    }

    /**
     * json转成map
     *
     * @param json
     * @return
     */
    public static Map<String, Object> strToMap(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        JSONObject object = JSON.parseObject(json);
        return object;
    }

    /**
     * @param map
     * @return
     */
    public static String toJsonString(Object map) {
        if (map == null) {
            return null;
        }
        return JSON.toJSONString(map);
    }

    public static JSONArray toArray(String str) {
        return JSONArray.parseArray(str);
    }

    public static JSONObject parse(String str) {
        return JSONObject.parseObject(str);
    }

    public static <T> T toJavaBean(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr =="") {
            return null;
        } else {
            try {
                return JSONObject.parseObject(jsonStr, clazz);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    /**
     * java列表
     * @param jsonStr
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> toJavaArray(String jsonStr, Class<T> clazz) {
        if (StringUtils.isBlank(jsonStr)) {
            return Collections.emptyList();
        }
        try {
            return JSONObject.parseArray(jsonStr, clazz);
        } catch (Exception e) {
        }

        return Collections.emptyList();
    }

    /**
     * 根据map转换成javabean
     *
     * @param map
     * @param c
     * @return
     */
    public static <T> T createBean(Map map, Class<T> c) {
        try {
            String str = toJsonString(map);
            return JSONObject.parseObject(str, c);
        } catch (Exception e) {
            return null;
        }
    }
}
