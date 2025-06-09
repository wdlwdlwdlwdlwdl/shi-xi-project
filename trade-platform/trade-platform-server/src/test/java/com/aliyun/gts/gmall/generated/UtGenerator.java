/**
 * com.aliyun.gts.gmall
 * Copyright(c) 2012-2019 All Rights Reserved.
 */
package com.aliyun.gts.gmall.generated;

import com.aliyun.gts.gmall.framework.generator.AbstractGenerator;

/**
 * 单元测试自动生成专用
 *
 * @author auto-generated
 */
public class UtGenerator extends AbstractGenerator {
    public static void main(String[] args) {
        UtGenerator generator = new UtGenerator();
        generator.generate();
    }

    @Override
    public boolean needTestng() {
        return true;
    }

    @Override
    public boolean needApiMock() {
        return false;
    }

    @Override
    public boolean needApiAutoConfiguration() {
        return false;
    }

    @Override
    public String dubboApiAutoConfigurationMavenModuleName() {
        return "trade-dubbo-autoconfigure";
    }

    @Override
    public String dubboApiAutoMockMavenModuleName() {
        return "trade-dubbo-automock";
    }

    @Override
    public String apiMavenModuleName() {
        return "trade-server-api";
    }

    @Override
    public String apiDefinitionFacadePackageFullName() {
        return "com.aliyun.gts.gmall.center.trade.api.facade";
    }

    @Override
    public String serverMavenModuleName() {
        return "trade-server";
    }

    @Override
    public String apiImplementationFacadePackageFullName() {
        return "com.aliyun.gts.gmall.center.trade.api.facade";
    }

}
