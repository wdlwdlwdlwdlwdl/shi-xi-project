package com.aliyun.gts.gmall.manager.front.customer.facade.impl;

import java.util.List;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.AddressAdapter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddressCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddressOptCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerAddressQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.customer.facade.AddressFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.enums.DeliveryMethodEnum;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 地址操作
 *
 * @author tiansong
 */
@Service
public class AddressFacadeImpl implements AddressFacade {
    @Autowired
    private AddressAdapter addressAdapter;

    @Override
    public List<CustomerAddressDTO> queryList(CustomerAddressQuery customerAddressQuery) {
        return addressAdapter.queryList(customerAddressQuery.getCustId(), customerAddressQuery.getDefaultYn());
    }

    @Override
    public CustomerAddressDTO addOrEdit(AddressCommand addressCommand) {
        if (addressCommand.getId() != null) {
            // 1. 检查收货地址归属
            addressAdapter.queryAddressById(
                addressCommand.getCustId(),
                addressCommand.getId()
            );
        }
        String deliveryMethod = addressCommand.getDeliveryMethod();
        if (StringUtils.isNotBlank(deliveryMethod) && !"PUCK_UP".equals(deliveryMethod)) {
            if (!DeliveryMethodEnum.hasExist(deliveryMethod)) {
                throw new GmallException(CustomerFrontResponseCode.ADDRESS_SAVE_FAIL);
            }
        }
        return addressAdapter.createOrUpdate(addressCommand);
    }

    @Override
    public Boolean setDefaultById(AddressOptCommand addressOptCommand) {
        // 1. 检查收货地址归属
        CustomerAddressDTO customerAddressDTO = addressAdapter.queryAddressById(addressOptCommand.getCustId(),
            addressOptCommand.getAddressId());
        if (customerAddressDTO == null) {
            throw new GmallException(CustomerFrontResponseCode.ADDRESS_QUERY_FAIL);
        }
        // 2. 设置默认收货地址
        Boolean result = addressAdapter.setDefault(addressOptCommand.getCustId(), addressOptCommand.getAddressId());
        if (BooleanUtils.isTrue(result)) {
            return result;
        }
        throw new GmallException(CustomerFrontResponseCode.ADDRESS_DEFAULT_FAIL);
    }

    @Override
    public Boolean delById(AddressOptCommand addressOptCommand) {
        // 1. 检查收货地址归属
        CustomerAddressDTO customerAddressDTO = addressAdapter.queryAddressById(addressOptCommand.getCustId(),
            addressOptCommand.getAddressId());
        if (customerAddressDTO == null) {
            return Boolean.TRUE;
        }
        // 2. 删除地址
        return addressAdapter.deleteAddress(addressOptCommand.getCustId(), addressOptCommand.getAddressId());
    }



    @Override
    public CustomerAddressDTO detail(AddressOptCommand addressOptCommand) {
        CustomerAddressDTO customerAddressDTO = addressAdapter.queryAddressById(addressOptCommand.getCustId(),
                addressOptCommand.getAddressId());
        return customerAddressDTO;
    }
}
