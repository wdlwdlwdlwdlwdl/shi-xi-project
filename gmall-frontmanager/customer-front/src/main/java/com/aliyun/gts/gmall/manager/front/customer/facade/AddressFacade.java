package com.aliyun.gts.gmall.manager.front.customer.facade;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddressCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddressOptCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerAddressQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;

/**
 * 会员收货地址操作接口
 *
 * @author tiansong
 */
public interface AddressFacade {
    /**
     * 获取会员收货地址列表
     *
     * @param customerAddressQuery
     * @return
     */
    List<CustomerAddressDTO> queryList(CustomerAddressQuery customerAddressQuery);

    /**
     * 新增或编辑地址
     *
     * @param addressCommand
     * @return
     */
    CustomerAddressDTO addOrEdit(AddressCommand addressCommand);

    /**
     * 设置默认地址
     *
     * @param addressOptCommand
     * @return
     */
    Boolean setDefaultById(AddressOptCommand addressOptCommand);

    /**
     * 删除地址
     *
     * @param addressOptCommand
     * @return
     */
    Boolean delById(AddressOptCommand addressOptCommand);

    /**
     * 查看地址详情
     * @param addressOptCommand
     * @return
     */
    CustomerAddressDTO detail(AddressOptCommand addressOptCommand);
}
