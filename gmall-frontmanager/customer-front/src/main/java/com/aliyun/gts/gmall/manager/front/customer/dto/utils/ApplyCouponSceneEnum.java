package com.aliyun.gts.gmall.manager.front.customer.dto.utils;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;


/**
 * 领取优惠券的场景枚举
 *
 * @author tiansong
 */
public enum ApplyCouponSceneEnum  implements I18NEnum {
    NORMAL(1, "|product.details.receive.scene|"),   //# "商品详情等领取场景"
    SHARE(2, "|external.share.receive.scene|"),   //# "对外分享领取场景"
    AWARDS(3, "|level.benefit.receive.scene|"), //# 等级权益领取场景
    SKU(4, "|sku.details.receive.scene|");  //# skuId不能为空

    private Integer id;
    private String  script;

    ApplyCouponSceneEnum(Integer id,  String desc) {
        this.id   = id;
        this.script = desc;
    }

    public Integer getId() {
        return id;
    }
    public String getScript() {
        return script;
    }

    public String getDesc() {
        return getMessage();
    }
}
