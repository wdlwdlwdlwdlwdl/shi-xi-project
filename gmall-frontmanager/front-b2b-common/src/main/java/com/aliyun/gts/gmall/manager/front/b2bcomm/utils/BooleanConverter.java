package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import org.apache.commons.lang.BooleanUtils;

/**
 * @author GTS
 * @date 2021/03/02
 */
public class BooleanConverter {

    public static String convert(Boolean b) {
        if (BooleanUtils.isTrue(b)) {
            return I18NMessageUtils.getMessage("yes");  //# "是"
        }
        return I18NMessageUtils.getMessage("no");  //# "否"
    }

}