package com.aliyun.gts.gmall.generated;

import com.aliyun.gts.gmall.framework.generator.mapper.AbstractMybatisPlusGenerator;

public class IBatisPlusGenerator extends AbstractMybatisPlusGenerator {

    public static void main(String[] args) {
        IBatisPlusGenerator generator = new IBatisPlusGenerator();
        generator.generate();
    }

    @Override
    public String moduleName() {
        return "trade-server";
    }

    @Override
    public String packageName() {
        return "com.aliyun.gts.gmall.center.trade.server";
    }

    @Override
    public String[] tables() {
        return new String[] { "tc_reversal" };
    }

    @Override
    public String datasourceUrl() {
        return "jdbc:mysql://drdshbga328217k9public.drds.aliyuncs.com:3306/trade?useUnicode=true&characterEncoding=UTF8&allowMultiQueries=true";
    }

    @Override
    public String datasourceUsername() {
        return "trade";
    }

    @Override
    public String datasourcePassword() {
        return "Trade123";
    }

    /**
     * 自动生成代码默认不覆盖已存在的文件、如有需要可自行调整
     *
     * @return
     */
    public boolean overrideFile() {
        return false;
    }
}