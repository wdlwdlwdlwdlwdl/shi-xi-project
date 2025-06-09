package com.aliyun.gts.gmall.center.trade.core.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author 张新
 */

public enum SaleTypeEnum  implements I18NEnum {
    /**
     * 直销模式
     */
    DIRECT(0, "|direct.sales|",  "01","sell"),  //# "直销"
    /**
     * 代销模式
     */
    CONSIGNMENT(1, "|consignment|",  "02","proxy");  //# "代销"

    private Integer code;
    
    private String script;

    private String mallCode;
    private String mallName;

    SaleTypeEnum(int code, String name,  String mallCode, String mallName) {
        this.code = code;
        this.script = script;
        this.mallCode = mallCode;
        this.mallName = mallName;
    }

    public static SaleTypeEnum buildSaleType(String saleType) {
        if (NumberUtils.isCreatable(saleType)) {
            for (SaleTypeEnum typeEnum : values()) {
                if (typeEnum.getCode().equals(Integer.parseInt(saleType))) {
                    return typeEnum;
                }
            }
        }
        return null;
    }

    public static SaleTypeEnum buildSaleType(Integer saleType) {
        for (SaleTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(saleType)) {
                return typeEnum;
            }
        }
        return null;
    }

    public static String getNameByCode(String mallCode) {
        for (SaleTypeEnum typeEnum : values()) {
            if (typeEnum.getMallCode().equals(mallCode)) {
                return typeEnum.mallName;
            }
        }
        return StringUtils.EMPTY;
    }

    public static String getCodeByName(String mallName) {
        for (SaleTypeEnum typeEnum : values()) {
            if (typeEnum.getMallName().equals(mallName)) {
                return typeEnum.mallCode;
            }
        }
        return StringUtils.EMPTY;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return getMessage();
    }

    public String getMallCode() {
        return mallCode;
    }

    public String getMallName() {
        return mallName;
    }

    public String getCodeStr() {
        return String.valueOf(code);
    }
    public String getScript() {
        return script;
    }
}
