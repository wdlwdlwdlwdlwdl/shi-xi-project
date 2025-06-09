package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.constant.MiscResponseCode;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode;
import com.aliyun.gts.gmall.manager.front.customer.converter.CustomerConverter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddressCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdAndCustByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CommonDeleteByIdWithCustIdCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerAddressListQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.enums.DeliveryMethodEnum;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerAddressReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerAddressWriteFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 会员地址操作
 *
 * @author tiansong
 */
@Service
@Slf4j
public class AddressAdapter {
    @Autowired
    private CustomerAddressReadFacade  customerAddressReadFacade;
    @Autowired
    private CustomerAddressWriteFacade customerAddressWriteFacade;
    @Autowired
    private CustomerConverter          customerConverter;
    @Autowired
    private DatasourceConfig           datasourceConfig;

    DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(FrontCommonResponseCode.CUSTOMER_CENTER_ERROR).build();

    /**
     * 获取收货地址详情
     *
     * @param loginUserId
     * @param addressId
     * @return
     */
    public CustomerAddressDTO queryAddressById(Long loginUserId, Long addressId) {
        CommonByIdAndCustByIdQuery commonByIdAndCustByIdQuery = new CommonByIdAndCustByIdQuery();
        commonByIdAndCustByIdQuery.setCustId(loginUserId);
        commonByIdAndCustByIdQuery.setId(addressId);
        CustomerAddressDTO dto = builder
            .create(datasourceConfig)
            .id(DsIdConst.customer_address_queryById)
            .queryFunc((Function<CommonByIdAndCustByIdQuery, RpcResponse<CustomerAddressDTO>>) request ->
                customerAddressReadFacade.query(request))
            .bizCode(CustomerFrontResponseCode.CUSTOMER_QUERY_FAIL)
            .query(commonByIdAndCustByIdQuery);
        if (dto != null && !Objects.equals(dto.getCustId(), loginUserId)) {
            throw new GmallException(MiscResponseCode.DATA_OWNER_ERROR);
        }
        return dto;

    }

    /**
     * 会员收货地址列表（最多展示50条）
     *
     * @param loginUserId
     * @return
     */
    public List<CustomerAddressDTO> queryList(long loginUserId, Boolean defaultYn) {
        CustomerAddressListQuery customerAddressQuery = new CustomerAddressListQuery();
        customerAddressQuery.setCustId(loginUserId);
        customerAddressQuery.setDefaultYn(defaultYn);
        customerAddressQuery.setDeliveryMethod(DeliveryMethodEnum.HOME_DELIVERY.getCode());
        List<CustomerAddressDTO> customerAddressDTOList = builder.create(datasourceConfig)
            .id(DsIdConst.customer_address_queryList)
            .queryFunc((Function<CustomerAddressListQuery, RpcResponse<List<CustomerAddressDTO>>>) request ->
                customerAddressReadFacade.queryList(request))
            .strong(Boolean.FALSE).query(customerAddressQuery);
        return customerAddressDTOList;
    }

    /**
     * 创建或更新收货地址
     *
     * @param addressCommand 收货地址
     * @return 更新后的收货地址
     */
    public CustomerAddressDTO createOrUpdate(AddressCommand addressCommand) {
        return builder.create(datasourceConfig)
            .id(DsIdConst.customer_address_createOrUpdate)
            .queryFunc((Function<AddressCommand, RpcResponse<CustomerAddressDTO>>) request -> {
                RpcResponse<CustomerAddressDTO> rpcResponse = null;
                if (addressCommand.getId() == null) {
                    rpcResponse = customerAddressWriteFacade.create(customerConverter.convertCreate(request));
                } else {
                    rpcResponse = customerAddressWriteFacade.update(customerConverter.convertUpdate(request));
                }
                return rpcResponse.isSuccess() && rpcResponse.getData() != null ?
                    rpcResponse : RpcResponse.fail(rpcResponse.getFail());
            })
            .bizCode(CustomerFrontResponseCode.ADDRESS_SAVE_FAIL)
            .query(addressCommand);
    }

    /**
     * 设置默认收货地址
     *
     * @param custId
     * @param addressId
     * @return
     */
    public Boolean setDefault(Long custId, Long addressId) {
        CommonDeleteByIdWithCustIdCommand setDefaultCommand = new CommonDeleteByIdWithCustIdCommand();
        setDefaultCommand.setCustId(custId);
        setDefaultCommand.setId(addressId);
        return builder.create(datasourceConfig).id(DsIdConst.customer_address_setDefault).queryFunc(
                (Function<CommonDeleteByIdWithCustIdCommand, RpcResponse<Boolean>>) request ->
                        customerAddressWriteFacade.markDefaultAddress(request)
        ).bizCode(CustomerFrontResponseCode.ADDRESS_DEFAULT_FAIL).query(setDefaultCommand);
    }

    /**
     * 删除收货地址
     *
     * @param userId
     * @param addressId
     * @return
     */
    public Boolean deleteAddress(Long userId, Long addressId) {
        CommonDeleteByIdWithCustIdCommand deleteCommand = new CommonDeleteByIdWithCustIdCommand();
        deleteCommand.setCustId(userId);
        deleteCommand.setId(addressId);
        Boolean result = builder.create(datasourceConfig).id(DsIdConst.customer_address_delById).queryFunc(
                (Function<CommonDeleteByIdWithCustIdCommand, RpcResponse<Boolean>>) request ->
                        customerAddressWriteFacade.delete(request)
        ).bizCode(CustomerFrontResponseCode.ADDRESS_DEL_FAIL).query(deleteCommand);
        return result == null ? Boolean.FALSE : result;
    }
}