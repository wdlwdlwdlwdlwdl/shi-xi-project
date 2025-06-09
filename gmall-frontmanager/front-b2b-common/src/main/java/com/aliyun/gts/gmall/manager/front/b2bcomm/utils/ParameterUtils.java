package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/1/23 18:25
 */
public class ParameterUtils {
    public static PageParam getPage(JSONObject param) {
        JSONObject page = param.getJSONObject("page");
        if (page == null) {
            return null;
        }
        PageParam pageParam = new PageParam();
        pageParam.setPageSize(page.getInteger("pageSize"));
        pageParam.setPageNo(page.getInteger("pageNo"));
        return pageParam;
    }

    public static List<Long> getIds(JSONObject param, String key) {
        JSONArray list = param.getJSONArray(key);
        if (list == null) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            result.add(list.getLong(i));
        }
        return result;
    }
}
