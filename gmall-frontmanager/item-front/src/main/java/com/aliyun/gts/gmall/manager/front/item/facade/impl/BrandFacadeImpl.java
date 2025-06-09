package com.aliyun.gts.gmall.manager.front.item.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.ByIdsQueryRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.item.convertor.BrandConvertor;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.BrandIdsQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.BrandVO;
import com.aliyun.gts.gmall.manager.front.item.facade.BrandFacade;
import com.aliyun.gts.gmall.platform.item.api.dto.output.brand.BrandDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.brand.BrandReadFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class BrandFacadeImpl implements BrandFacade {


    @Autowired
    private BrandReadFacade brandReadFacade;

    @Autowired
    private BrandConvertor brandConvertor;

    @Override
    public List<BrandVO> queryAllByParam(BrandIdsQuery req) {

        ByIdsQueryRequest byIdsQueryRequest = new ByIdsQueryRequest();
        byIdsQueryRequest.setIds(req.getIds());
        RpcResponse<List<BrandDTO>> listRpcResponse =  brandReadFacade.queryByIds(byIdsQueryRequest);
        List<BrandDTO> data = listRpcResponse.getData();

        return brandConvertor.toBrandVOList(data);
    }
}
