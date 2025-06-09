package com.aliyun.gts.gmall.manager.front.sourcing.facade;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteCountDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.QuotePriceReadFacade;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.QuotePriceWriteFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.input.CommandRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.IdListQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.QuoteQuery;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ErrorCodeUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.QuoteConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.SourcingMaterialConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.QuoteDetailVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.QuoteVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingMaterialVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/18 14:07
 */
@Component
public class QuotePriceFacade {

    @Resource
    private QuotePriceReadFacade readFacade;
    @Resource
    private QuotePriceWriteFacade priceWriteFacade;
    @Resource
    private SourcingFacade sourcingFacade;
    @Resource
    private QuoteConvert quoteConvert;
    @Resource
    private SourcingMaterialConvert materialConvert;

    public QuoteVo queryForDetail(Long sourcingId, Long supplierId) {
        //查询询价活动
        SourcingVo sourcingVo = sourcingFacade.queryById(sourcingId, true);
        //询价活动信息
        QuoteVo quoteVo = queryBySupplier(sourcingId, supplierId);
        quoteVo.setSourcingInfo(sourcingVo);
        //物料列表,不可能为空
        List<QuoteDetailVo> list = buildQuoteDetail(sourcingVo.getMaterials(), quoteVo.getDetails());
        quoteVo.setDetails(list);
        if (quoteVo.getQuoteFeature() == null) {
            quoteVo.setQuoteFeature(new JSONObject());
        }
        return quoteVo;
    }

    private QuoteVo queryBySupplier(Long sourcingId, Long supplierId) {
        QuoteQuery quoteQuery = new QuoteQuery();
        quoteQuery.setSourcingId(sourcingId);
        quoteQuery.setSupplierId(supplierId);
        quoteQuery.setIncludeDetail(true);
        RpcResponse<QuoteDTO> response = readFacade.queryBySupplier(quoteQuery);
        if (response.getData() != null) {
            return quoteConvert.dto2Vo(response.getData());
        }
        return null;
    }

    /**
     * 返回数据
     *
     * @param quoteId
     * @return
     */
    public QuoteDTO queryQuote(Long quoteId) {
        RpcResponse<QuoteDTO> response = readFacade.queryById(CommonByIdQuery.of(quoteId));
        if (!response.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapCode(response.getFail()));
        }
        return response.getData();
    }

    /**
     * @param query
     * @return
     */
    public PageInfo<QuoteDTO> page(QuoteQuery query) {
        ParamUtil.nonNull(query.getPage(), I18NMessageUtils.getMessage("pagination.required"));  //# "分页信息不能为空"
        RpcResponse<PageInfo<QuoteDTO>> response = readFacade.page(query);
        return response.getData();
    }

    /**
     * 查询所有的李斯特
     *
     * @return
     */
    public List<QuoteDTO> list(List<Long> quoteIds) {
        List<QuoteDTO> dtos = new ArrayList<QuoteDTO>();
        if (CollectionUtils.isEmpty(quoteIds)) {
            return dtos;
        }
        for (Long id : quoteIds) {
            QuoteDTO dto = queryQuote(id);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<QuoteCountDTO> queryCountBySourcing(List<Long> sourcingIds) {
        IdListQuery req = new IdListQuery();
        req.setIdList(sourcingIds);
        RpcResponse<List<QuoteCountDTO>> resp = readFacade.queryCountBySourcing(req);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapCode(resp.getFail()));
        }
        return resp.getData();
    }

    public List<QuoteDTO> list(Long sourcingId){
        List<QuoteDTO> list = Lists.newArrayList();
        RpcResponse<List<QuoteDTO>>  response = readFacade.queryBySourcing(CommonByIdQuery.of(sourcingId));
        if(response.isSuccess()){
            list.addAll(response.getData());
        }
        return list;
    }

    public void updateQuoteStatus(QuoteDTO quoteDTO){
        priceWriteFacade.updateStatus(CommandRequest.of(quoteDTO));
    }

    private List<QuoteDetailVo> buildQuoteDetail(List<SourcingMaterialVo> materials, List<QuoteDetailVo> submit) {
        Map<Long, QuoteDetailVo> maps = new HashMap<>();
        if (!CollectionUtils.isEmpty(submit)) {
            maps = submit.stream().collect(Collectors.toMap(QuoteDetailVo::getScMaterialId, QuoteDetailVo -> QuoteDetailVo));
        }
        List<QuoteDetailVo> result = new ArrayList<>();
        for (SourcingMaterialVo material : materials) {
            QuoteDetailVo quoteDetailVo = materialConvert.material2Detail(material);
            QuoteDetailVo supplierSubmit = maps.get(quoteDetailVo.getScMaterialId());
            if (supplierSubmit != null) {
                quoteConvert.merge(quoteDetailVo, supplierSubmit);
            }
            quoteDetailVo.setMaterial(material);
            result.add(quoteDetailVo);
        }
        return result;
    }
}
