/**
 * com.aliyun.gts.gmall
 * Copyright(c) 2012-2019 All Rights Reserved.
 */
package com.aliyun.gts.gmall.generated;

import com.aliyun.gts.gmall.framework.generator.AbstractGenerator;

/**
 * api接口mock代码自动生成
 *
 * @author auto-generated
 */
public class ApiMockGenerator extends AbstractGenerator {
    public static void main(String[] args) {
        ApiMockGenerator generator = new ApiMockGenerator();
        generator.generate();
    }

    @Override
    public boolean needApiMock() {
        return true;
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
    public boolean needTestng() {
        return false;
    }

    @Override
    public boolean needServiceTestng() {
        return false;
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
    public String serviceDefinitionPackageFullName() {
        return "com.aliyun.gts.gmall.center.trade.server.service";
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
