package com.aliyun.gts.gmall.manager.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/11/10 17:51
 */
public class JsonUtils {

    public static final String toJSONString(Object object) {
        if (object == null) {
            return null;
        }
        return JSON.toJSONString(object);
    }

    public static <T> T toJavaBean(String jsonStr, Class<T> clazz) {
        if (jsonStr.equals(null) || jsonStr.equals("") ) {
            return null;
        } else {
            try {
                return JSONObject.parseObject(jsonStr, clazz);
            } catch (Exception var3) {
                return null;
            }
        }
    }
}
