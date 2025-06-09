/**
 * com.aliyun.gts.gmall
 * Copyright(c) 2012-2019 All Rights Reserved.
 */
package com.aliyun.gts.gmall.generated;

import com.aliyun.gts.gmall.framework.generator.AbstractGenerator;

/**
 * Dubbo Consumer接口自动注册器自动生成专用、请勿改动代码
 *
 * @author auto-generated
 */
public class DubboGenerator extends AbstractGenerator {
    public static void main(String[] args) {
        DubboGenerator generator = new DubboGenerator();
        generator.generate();
    }

    /**
     * Facade接口定义使用注解@Api后，自动生成dubbo-autoconfigure模块的文件
     *
     * @return
     */
    @Override
    public boolean needApiAutoConfiguration() {
        return true;
    }

    @Override
    public String dubboApiAutoConfigurationMavenModuleName() {
        return "trade-dubbo-autoconfigure";
    }

    @Override
    public boolean needApiMock() {
        return false;
    }

    @Override
    public boolean needTestng() {
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
    public String serverMavenModuleName() {
        return "trade-server";
    }

    @Override
    public String apiImplementationFacadePackageFullName() {
        return "com.aliyun.gts.gmall.center.trade.api.facade";
    }

}
