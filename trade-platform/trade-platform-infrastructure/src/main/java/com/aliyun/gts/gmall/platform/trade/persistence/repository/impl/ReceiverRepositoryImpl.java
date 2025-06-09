package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ReceiverRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter.ReceiverRpcConverter;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdAndCustByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerAddressQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.SellerAddressQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerAddressReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerAddressReadFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "receiverRepository", havingValue = "default", matchIfMissing = true)
public class ReceiverRepositoryImpl implements ReceiverRepository {

    @Autowired
    private CustomerAddressReadFacade customerAddressReadFacade;
    @Autowired
    private ReceiverRpcConverter receiverRpcConverter;
    @Autowired
    private SellerAddressReadFacade sellerAddressReadFacade;

    @Override
    public ReceiveAddr getReceiverById(Long custId, Long id) {
        CommonByIdAndCustByIdQuery commonByIdAndCustByIdQuery = new CommonByIdAndCustByIdQuery();
        commonByIdAndCustByIdQuery.setId(id);
        commonByIdAndCustByIdQuery.setCustId(custId);
        RpcResponse<CustomerAddressDTO> resp = RpcUtils.invokeRpc(
            () -> customerAddressReadFacade.query(commonByIdAndCustByIdQuery),
            "customerAddressReadFacade.query",
            I18NMessageUtils.getMessage("address.query"),
            commonByIdAndCustByIdQuery
        );  //# "收货地址查询"
        if (resp.getData() == null || !Objects.equals(custId, resp.getData().getCustId())) {
            // 地址不存在
            return null;
        }
        return receiverRpcConverter.toReceiveAddr(resp.getData());
    }

    @Override
    public ReceiveAddr getDefaultReceiver(Long custId) {
        CustomerAddressQuery q = new CustomerAddressQuery();
        q.setCustId(custId);
        q.setDefaultYn(true);
        q.setPage(new PageParam(PageParam.DEFAULT_PAGE_NO, 1));
        RpcResponse<PageInfo<CustomerAddressDTO>> resp = RpcUtils.invokeRpc(
                () -> customerAddressReadFacade.pageQuery(q),
                "customerAddressReadFacade.query", I18NMessageUtils.getMessage("default.address.query"), q);  //# "默认收货地址查询"
        List<CustomerAddressDTO> list = resp.getData().getList();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return receiverRpcConverter.toReceiveAddr(list.get(0));
    }

    @Override
    public ReceiveAddr getSellerDefaultReceiver(Long sellerId) {
        SellerAddressQuery q = new SellerAddressQuery();
        q.setSellerId(sellerId);
        q.setDefaultYn(true);
        q.setPage(new PageParam(PageParam.DEFAULT_PAGE_NO, 1));
        RpcResponse<PageInfo<SellerAddressDTO>> resp = RpcUtils.invokeRpc(
                () -> sellerAddressReadFacade.pageQuery(q),
                "customerAddressReadFacade.query", I18NMessageUtils.getMessage("default.address.query"), q);  //# "默认收货地址查询"
        List<SellerAddressDTO> list = resp.getData().getList();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return receiverRpcConverter.toReceiveAddr(list.get(0));
    }
}
