package com.aliyun.gts.gmall.manager.front.item.dto.utils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

/**
 * 登录模块的响应码
 *
 * @author tiansong
 */
public enum ItemFrontResponseCode implements ResponseCode {
    ITEM_NOT_EXIST("60030001", "|product.not.exist|！",  0),  //# "商品不存在
    ITEM_EVOUCHER_EXPIRE("60030002", "|e.voucher.expired|！",  0),  //# "电子凭证已过期
    ADDRESS_NOT_EXIST("60030010", "|delivery.address.not.exist|",  0),  //# "用户收货地址不存在"
    PAGES_EXCEED("06003006", "|max.query.pages.exceed|,|narrow.search.range|", 0),   //# "超过最大可查询页数,请通过搜索条件缩小范围"
    OSS_BUCKET_EMPTY("60030090", "|file.upload.fail|，|check.business.type|！",  0),  //# "文件上传失败，请检查业务类型
    YU_SHOU_CAN_ONLY_BUY_ONE("60030092","|yu_shou can only buy one|",0), //后端校验 - 预售商品只能购买一件
    FLASH_ITEM_LIMIT("60030091", "|front.above.max.quantity.limit|",  0);  //#

    private String code;
    private String script;
    private int    args;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public int getArgs() {
        return this.args;
    }

    private ItemFrontResponseCode(String code, String script,  int args) {
        this.code = code;
        this.script = script;
        this.args = args;
    }
    public String getScript() {
        return script;
    }

    public String getDesc() {
        return getMessage();
    }
}
