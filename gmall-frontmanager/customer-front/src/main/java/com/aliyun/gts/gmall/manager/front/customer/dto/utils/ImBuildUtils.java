package com.aliyun.gts.gmall.manager.front.customer.dto.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.platform.gim.api.dto.output.UserDTO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/9 11:54
 */
public class ImBuildUtils {
    /**
     * 参数
     *
     * @param users
     * @param counts
     * @return
     */
    public static List<JSONObject> buildNoticeTab(List<UserDTO> users, Map<Long, Long> counts) {
        List<JSONObject> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(users)) {
            list.add(buildTab(I18NMessageUtils.getMessage("all"), 0L, 0L));  //# "全部"
            return list;
        }
        Long sum = 0L;
        for (UserDTO userDTO : users) {
            Long count = counts.get(userDTO.getId());
            JSONObject object = buildTab(userDTO.getNickname(), count, userDTO.getId());
            if (count != null) {
                sum = count + sum;
            }
            list.add(object);
        }
        list.add(0, buildTab(I18NMessageUtils.getMessage("all"), sum, 0L));  //# "全部"
        return list;
    }

    /**
     * @param name
     * @param count
     * @param sendId
     * @return
     */
    private static JSONObject buildTab(String name, Long count, Long sendId) {
        JSONObject tab = new JSONObject();
        tab.put("name", name);
        tab.put("unRead", count == null ? 0 : count);
        tab.put("value", sendId);
        return tab;
    }

    public static String imUrl(String token, String openUrl, String redirect) {
        JSONObject param = new JSONObject();
        param.put("token", token);
        String paramSting =  ParseUtils.urlDecode(param.toJSONString());
        String direct = ParseUtils.urlDecode(redirect);
        StringBuilder builders = new StringBuilder();
        builders.append(openUrl)
                .append("?app=seller")
                .append("&redirect=")
                .append(direct)
                .append("&param=")
                .append(paramSting);
        return builders.toString();
    }
}
