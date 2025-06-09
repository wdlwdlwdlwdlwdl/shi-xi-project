package com.aliyun.gts.gmall.generated;

import com.aliyun.gts.gmall.framework.generator.allinone.AllInOneGenerator;

public class TradeGenerator extends AllInOneGenerator {
    @Override
    public String[] tables() {
        String tables = "tc_order_check_error";
        return tables.split("\n");
    }

    public static void main(String[] args) {
        TradeGenerator tradeGenerator = new TradeGenerator();

        String[] tables = tradeGenerator.tables();

        for (String table : tables) {
            tradeGenerator.gen(table);
        }
    }

    @Override
    public String apiModuleName() {
        return "trade-platform-api";
    }

    @Override
    public String reqPackageName() {
        return "com.aliyun.gts.gmall.platform.trade.api.dto.input";
    }

    @Override
    public String moduleName() {
        return "trade-platform-domain";
    }

    @Override
    public String dtoModuleName() {
        return "trade-platform-api";
    }

    @Override
    public String packageModuleName() {
        return null;
    }

    @Override
    public String packageMapperName() {
        return "com.aliyun.gts.gmall.platform.trade.persistence.mapper";
    }

    @Override
    public String parentPackageName() {
        return null;
    }

    @Override
    public String entityPackageName() {
        return "com.aliyun.gts.gmall.platform.trade.domain.dataobject";
    }

    @Override
    public String dtoPackageName() {
        return "com.aliyun.gts.gmall.platform.trade.api.dto.output";
    }

    @Override
    public String datasourceUrl() {
        return "jdbc:mysql://drdshbgaj1bgr06qpublic.drds.aliyuncs.com:3306/trade_uat?useUnicode=true&characterEncoding=UTF8&allowMultiQueries=true&serverTimezone=Asia/Shanghai";
    }

    @Override
    public String datasourceUsername() {
        return "trade_uat";
    }

    @Override
    public String datasourcePassword() {
        return "Gmall1234";
    }

    /**
     * 是否覆盖文件
     *
     * @return
     */
    public boolean replaceFile() {
        return true;
    }
}
