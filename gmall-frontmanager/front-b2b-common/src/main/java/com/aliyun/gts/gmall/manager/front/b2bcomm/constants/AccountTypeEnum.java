package com.aliyun.gts.gmall.manager.front.b2bcomm.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import java.util.Objects;
import java.util.function.Predicate;

public enum AccountTypeEnum  implements I18NEnum {
    PURCHASER_ACCOUNT(0, "|buyer.account|",  (type) -> {  //# "采购方账号"
        return Objects.equals(type, 0);
    }),
    SUPPLIER_ACCOUNT(1, "|supplier.account|",  (type) -> {  //# "供应方账号"
        return Objects.equals(type, 1);
    }),
    SUPERVISOR_ACCOUNT(2, "|supervisor.account|",  (type) -> {  //# "监理账号"
        return Objects.equals(type, 2);
    }),
    EXPERT_ACCOUNT(3, "|expert.account|",  (type) -> {  //# "专家账号"
        return Objects.equals(type, 3);
    });

    private final Integer code;
    private final String script;
    private final Predicate<Integer> predicate;

    private AccountTypeEnum(Integer code,  String desc, Predicate<Integer> predicate) {
        this.code = code;
        this.script = desc;
        this.predicate = predicate;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getDesc() {
        return getMessage();
    }

    public boolean isThatType(Integer type) {
        return this.predicate.test(type);
    }

    public boolean isNotThatType(Integer type) {
        return !this.predicate.test(type);
    }

    public static boolean existsType(Integer type) {
        AccountTypeEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            AccountTypeEnum accountTypeEnum = var1[var3];
            if (accountTypeEnum.isThatType(type)) {
                return true;
            }
        }

        return false;
    }

    public static boolean haveRelatedId(Integer type) {
        return SUPPLIER_ACCOUNT.isThatType(type) || PURCHASER_ACCOUNT.isThatType(type);
    }

    public static boolean canLoginInPurchaseAdmin(Integer type) {
        return PURCHASER_ACCOUNT.isThatType(type) || EXPERT_ACCOUNT.isThatType(type) || SUPERVISOR_ACCOUNT.isThatType(type);
    }

    public static boolean canLoginInSupplyAdmin(Integer type) {
        return SUPPLIER_ACCOUNT.isThatType(type);
    }
    public String getScript() {
        return script;
    }


}

