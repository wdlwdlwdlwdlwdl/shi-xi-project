package com.aliyun.gts.gmall.manager.front.item.dto.utils;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/7 16:41
 */
public enum DetailShowType  implements I18NEnum {
    MIAOSHA(1, "|flash.sale|", false,true),  //# "秒杀"
    ;

    private String script;
    private Integer type;
    private Boolean addCart = true;
    private Boolean toBuy = true;

    DetailShowType(Integer type, String desc){
        this.type = type;
        this.script =desc;
    }

    DetailShowType(Integer type, String desc,Boolean addCart,Boolean toBuy){
        this.type = type;
        this.script =desc;
        this.addCart = addCart;
        this.toBuy = toBuy;
    }

    public static DetailShowType find(Integer type){
        if(type == null){
            return null;
        }
        for(DetailShowType element: DetailShowType.values()){
            if(element.getType().equals(type)){
                return element;
            }
        }
        return null;
    }
    public Integer getType() {
        return type;
    }
    public Boolean getAddCart() {
        return addCart;
    }

    public Boolean getToBuy() {
        return toBuy;
    }
    public String getScript() {
        return script;
    }

    public String getDesc() {
        return getMessage();
    }
}
