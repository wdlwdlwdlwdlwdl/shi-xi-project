package com.aliyun.gts.gmall.center.trade.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 多语言配置比较工具类，在项目中无用
 */
public class PropertiesUtil {

    public static void main(String[] args) throws Exception {
        List<String> langList = new ArrayList<>();
        langList.add("messages_mall_trade_center_en.properties");
        langList.add("messages_mall_trade_center_kk.properties");
        langList.add("messages_mall_trade_center_ru.properties");
        Properties defalutProp = new Properties();
        // 获取resources目录下的example.properties文件的输入流
        InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("messages_mall_trade_center_en.properties");
        if (inputStream != null) {
            // 加载输入流
            defalutProp.load(inputStream);
            // 关闭输入流
            inputStream.close();
        }
        // 所有的key
        Set<Object> defaultKey = defalutProp.keySet();
        for (String lang : langList) {
            System.out.println("----------------------" + lang + "-------------------------------");
            Properties langProp = new Properties();
            // 获取resources目录下的example.properties文件的输入流
            inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(lang);
            if (inputStream != null) {
                // 加载输入流
                langProp.load(inputStream);
                // 关闭输入流
                inputStream.close();
            }
            for (Object key : defaultKey) {
                if (langProp.get(key) == null) {
                    System.out.println(String.valueOf(key) + "=");
                }
            }
        }
    }
}
