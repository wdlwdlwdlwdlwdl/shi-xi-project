package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerAccountInfo;
import com.aliyun.gts.gmall.platform.user.api.constants.FeatureKeyConstants;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.SellerStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.UserExtendConstants;
import com.aliyun.gts.gmall.platform.user.api.dto.input.*;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ESignInfoConfigDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerExtendDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ShopConfigDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import org.testng.collections.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

@Component
public class MockSellerReadFacade implements SellerReadFacade {
    @Override
    public RpcResponse<SellerDTO> query(CommonByIdQuery commonByIdQuery) {
        SellerDTO seller = new SellerDTO();
        seller.setId(commonByIdQuery.getId());
        seller.setSellerStatus(SellerStatusEnum.NORMAL.getCode().toString());
        seller.setNickname("SELLER_NAME");
        Map<String, String> feature = new HashMap<>();
        seller.setFeatures(feature);

        List<SellerExtendDTO> list = Lists.newArrayList();
        SellerExtendDTO extendDTO = new SellerExtendDTO();

        extendDTO.setId(1L);
        extendDTO.setSellerId(commonByIdQuery.getId());
        extendDTO.setType(UserExtendConstants.APPLY_INFO);
        extendDTO.setK(UserExtendConstants.APPLY_INFO);

        SellerApplyInfo sellerApplyInfo = new SellerApplyInfo();
        SellerFundAccount sellerFundAccount = new SellerFundAccount();
        sellerFundAccount.setAlipayAccountNo("XX");
        sellerFundAccount.setWechatAccountNo("XX");
        sellerFundAccount.setBankAccountName("XX");
        sellerFundAccount.setBankAccountNum("XX");
        sellerApplyInfo.setSellerFundAccount(sellerFundAccount);

        extendDTO.setV(JSON.toJSONString(sellerApplyInfo));

        list.add(extendDTO);
        seller.setSellerExtends(list);

        return RpcResponse.ok(seller);
    }

    @Override
    public RpcResponse<PageInfo<SellerDTO>> pageQuery(SellerQuery sellerQuery) {
        return null;
    }

    @Override
    public RpcResponse<List<SellerDTO>> queryByIds(CommonByBatchIdsQuery commonByBatchIdsQuery) {
        return null;
    }

    @Override
    public RpcResponse<SellerDTO> checkByPwd(SellerPwdCheckCommand sellerPwdCheckCommand) {
        return null;
    }

    @Override
    public RpcResponse<ShopConfigDTO> queryShop(ShopConfigQuery shopConfigQuery) {
        return null;
    }

    @Override
    public RpcResponse<ESignInfoConfigDTO> queryESign(CommonByIdQuery commonByIdQuery) {
        return null;
    }

    @Override
    public RpcResponse<SellerExtendDTO> queryExtend(CommonByIdQuery commonByIdQuery) {
        return null;
    }

    @Override
    public RpcResponse<PageInfo<SellerExtendDTO>> pageQueryExtend(SellerExtendQuery sellerExtendQuery) {
        return null;
    }
}
