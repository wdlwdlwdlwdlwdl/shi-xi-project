package com.aliyun.gts.gmall.manager.front.customer.facade;

import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddressOptCommand;

import java.util.Map;

/**
 * 页面访问接口
 *
 * @author shifeng
 */
public interface HtmlFacade {

    /**
     * PC 访问地址参数
     * @param
     * @return
     */
    Map<String, Object> toPcMap();

    /**
     * 手机 访问地址参数
     * @param
     * @return
     */
    Map<String, Object> toMobileMap();

}
