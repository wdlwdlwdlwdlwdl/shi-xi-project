package com.aliyun.gts.gmall.manager.front.common.consts;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.dto.PageParam;

import java.util.concurrent.TimeUnit;

/**
 * 业务初始化变量
 *
 * @author tiansong
 */
public class BizConst {
    /**
     * 系统配置
     */
    public static final String  REGEX_MOBILE             = "^7\\d{10}$";
    public static final Integer ORDER_ID_LIMIT_LENGTH    = 6;
    public static final String  EXTEND_KEY_ORDER_INVOICE = "invoice";
    public static final boolean FORCE_LOGIN              = false;
    /**
     * 商家配置
     */
    public static final String  SELLER_NICK              = I18NMessageUtils.getMessage("bs.merchant");  //# "百视猫商家"

    /**
     * 用户信息
     * CUSTOMER_NICK_HIDDEN_LEN 不能小于三
     */
    public static final String  CUSTOMER_NICK            = I18NMessageUtils.getMessage("bs.member");  //# "百视猫会员"
    public static final String  CUSTOMER_NICK_HIDDEN     = "***";
    public static final Integer CUSTOMER_NICK_HIDDEN_LEN = 3;
    
    public static final Long    LOGIN_EXPIRE_H5          = TimeUnit.HOURS.toMillis(12L);
    public static final Long    LOGIN_EXPIRE_WX          = TimeUnit.DAYS.toMillis(7L);
    public static final String  CUSTOMER_TEMP_NICK       = I18NMessageUtils.getMessage("bs.temp.member");  //# "百视猫临时会员"

    /**
     * Address 默认地址
     */
    public static final Long    ADDRESS_PROVINCE_ID = 31L;
    public static final Long    ADDRESS_CITY_ID     = 31001L;
    public static final Long    ADDRESS_AREA_ID     = 31001001L;
    public static final String  ADDRESS_PROVINCE    = I18NMessageUtils.getMessage("zhejiang.province");  //# "浙江省"
    public static final String  ADDRESS_CITY        = I18NMessageUtils.getMessage("hangzhou.city");  //# "杭州市"
    public static final String  ADDRESS_AREA        = I18NMessageUtils.getMessage("yuhang.district");  //# "余杭区"
    /**
     * page 默认分页
     */
    public static final Integer PAGE_NO             = PageParam.DEFAULT_PAGE_NO;
    public static final Integer PAGE_SIZE           = 10;
    public static final Integer HOT_EVALUATION_SIZE = 3;
    public static final Integer ADDRESS_PAGE_SIZE   = 50;
    public static final Integer INVOICE_PAGE_SIZE   = 50;
    public static final int     MAX_SEARCH_SIZE     = 500;
    /**
     * SKU展示分隔符
     */
    public static final String  SKU_PROP_SPLIT      = ";";
    public static final String  SKU_PV_SPLIT        = ":";

    /**
     * 支付渠道LOGO
     */
    public static final String ALIPAY_LOGO = "http://mall-dev.ibbtv.cn/image/pay/alipay_logo.png";
    public static final String WECHAT_LOGO = "http://mall-dev.ibbtv.cn/image/pay/weixin_logo.png";

    public static final String CPT_LOGO = "https://gmall-myth-fd-dev.oss-cn-hangzhou.aliyuncs.com/logo/gz.png";
    public static final String ACCOUNT_PERIOD_LOGO = "https://gmall-myth-fd-dev.oss-cn-hangzhou.aliyuncs.com/logo/sj.png";

    /**
     * 交易配置
     */
    public static final int PAY_MAX_SELLER_COUNT = 5;

    /**
     * 隐藏发票申请按钮
     */
    public static String HIDE_APPLY_BUTTON = "hide_apply_button";

    public static String TRUE_STR = "true";

    public static String INVOICE_ID = "invoice_id";

    public static String REVERSAL_ORDER_STATUS = "reversalOrderStatus";

    public static String HAS_SPECIAL_INVOICE = "20880007";
}
