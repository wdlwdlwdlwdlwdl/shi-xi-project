package com.aliyun.gts.gmall.manager.front.sourcing.facade;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.BidingPriceDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.BidingPriceReadFacade;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.BidingPriceWriteFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.input.StatusUpdateRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.BidingPriceQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.IdListQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.type.BidingPriceStatus;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ErrorCodeUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ResponseUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/6/3 10:14
 */
@Component
public class BidingPriceFacade {

    @Resource
    private BidingPriceWriteFacade priceWriteFacade;

    @Resource
    private BidingPriceReadFacade priceReadFacade;

    /**
     * 排名
     *
     * @param sourcingId
     * @param includeFirst
     * @return
     */
    public List<BidingPriceDTO> priceRank(Long sourcingId, Boolean includeFirst) {
        BidingPriceQuery query = new BidingPriceQuery();
        query.setSourcingId(sourcingId);
        query.setIncludeFirstBid(includeFirst);
        RpcResponse<List<BidingPriceDTO>> response = priceReadFacade.priceRank(query);
        if (!response.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapCode(response.getFail()));
        }
        return response.getData();
    }

    public List<BidingPriceDTO> queryByIds(List<Long> ids) {
        IdListQuery q = new IdListQuery();
        q.setIdList(ids);
        RpcResponse<List<BidingPriceDTO>> resp = priceReadFacade.queryByIds(q);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapCode(resp.getFail()));
        }
        return resp.getData();
    }

    /**
     * 报价列表
     *
     * @param query
     * @return
     */
    public PageInfo<BidingPriceDTO> page(BidingPriceQuery query) {
        RpcResponse<PageInfo<BidingPriceDTO>> response = priceReadFacade.page(query);
        if (!response.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapCode(response.getFail()));
        }
        return response.getData();
    }

    /**
     * 授标
     * @param bidingPrice
     * @param status
     * @return
     */
    public RestResponse<Boolean> preAwardBiding(List<Long> bidingPrice, Integer status) {
        for (Long id : bidingPrice) {
            StatusUpdateRequest updateRequest = new StatusUpdateRequest();
            updateRequest.setId(id);
            updateRequest.setStatus(BidingPriceStatus.pre_award.getValue());
            RpcResponse<Boolean> response = priceWriteFacade.awardBiding(updateRequest);
            if (!response.isSuccess()) {
                throw new GmallException(ErrorCodeUtils.mapCode(response.getFail()));
            }
            if (!Boolean.TRUE.equals(response.getData())) {
                return ResponseUtils.operateResult(response);
            }
        }
        return RestResponse.ok(Boolean.TRUE);
    }
}
