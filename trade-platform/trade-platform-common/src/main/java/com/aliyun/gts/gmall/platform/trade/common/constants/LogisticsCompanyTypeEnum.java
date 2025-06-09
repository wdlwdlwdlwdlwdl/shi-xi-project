package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

/**
 *
 */
public enum LogisticsCompanyTypeEnum implements GenericEnum {

    //
    SF( 1 , "|sf.express|", "SF"),  //# "顺丰速运"
    HTKY( 2 , "|best.express|", "HTKY"),  //# "百世快递"
    ZTO( 3 , "|zto.express|", "ZTO"),  //# "中通快递"
    STO( 4 , "|sto.express|", "STO"),  //# "申通快递"
    YTO( 5 , "|yto.express|", "YTO"),  //# "圆通速递"
    YD( 6 , "|yunda.express|", "YD"),  //# "韵达速递"
    YZPY( 7 , "|china.post|", "YZPY"),  //# "邮政快递包裹"
    EMS( 8 , "\"EMS\"", "EMS"),
    HHTT( 9 , "|tt.express|", "HHTT"),  //# "天天快递"
    JD( 10 , "|jd.express|", "JD"),  //# "京东快递"
    UC( 11 , "|uspeed.express|", "UC"),  //# "优速快递"
    DBL( 12 , "|deppon.express|", "DBL");  //# "德邦快递"


    // ========================

    private final Integer code;
    
    private String script;

    private final String companyCode;

    LogisticsCompanyTypeEnum(Integer code, String name, String companyCode) {
        this.code = code;
        this.script = script;
        this.companyCode = companyCode;
    }

    public static LogisticsCompanyTypeEnum codeOf(Integer code) {
        return Arrays.stream(LogisticsCompanyTypeEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getName() {
        return getMessage();
    }

    public String getCompanyCode(){
        return companyCode;
    }
    public String getScript() {
        return script;
    }
}
