package com.aliyun.gts.gmall.manager.front.common.util;

import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractRestRequest;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 业务处理
 *
 * @author tiansong
 */
public class FrontUtils {
    public static String showNickHidden(String nickname) {
        if (StringUtils.isBlank(nickname)) {
            return BizConst.CUSTOMER_NICK;
        }
        if (nickname.length() < BizConst.CUSTOMER_NICK_HIDDEN_LEN) {
            return BizConst.CUSTOMER_NICK_HIDDEN;
        }
        return nickname.charAt(0) + BizConst.CUSTOMER_NICK_HIDDEN + nickname.charAt(nickname.length() - 1);
    }


    private static final String LIST_EMPTY = "[]";

    public static List<String> toList(String list) {
        if (StringUtils.isBlank(list) || LIST_EMPTY.equals(list)) {
            return Collections.EMPTY_LIST;
        }
        return JSON.parseArray(list, String.class);
    }

    public static Long getLoginUserId(AbstractRestRequest request) {
        return request.getUser() == null ? 0L : NumberUtils.toLong(request.getUser().getUserId());
    }
}
