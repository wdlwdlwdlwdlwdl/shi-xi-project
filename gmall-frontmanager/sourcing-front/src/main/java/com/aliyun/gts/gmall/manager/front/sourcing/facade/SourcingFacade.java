package com.aliyun.gts.gmall.manager.front.sourcing.facade;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteCountDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingMaterialDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.SourcingApplyWriteFacade;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.SourcingReadFacade;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.SourcingWriteFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.input.CommandRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.CommonIdQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.IdListQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.SourcingQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.type.ApplyType;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.service.CategoryService;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ErrorCodeUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ResponseUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.constants.SourcingConstants;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.SourcingConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingMaterialVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/13 16:17
 */
@Component
public class SourcingFacade {

    @Resource
    private SourcingReadFacade readFacade;
    @Resource
    private SourcingWriteFacade writeFacade;
    @Resource
    private SourcingConvert convert;
    @Resource
    private SourcingApplyWriteFacade applyWriteFacade;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private QuotePriceFacade quotePriceFacade;

    public RestResponse<Boolean> create(SourcingVo vo) {
        SourcingDTO dto = convert.vo2DTO(vo);
        RpcResponse<Long> response = writeFacade.create(CommandRequest.of(dto));
        if (response.isSuccess() == false) {
            return ResponseUtils.toBoolean(response);
        }
        vo.setId(response.getData());
        if (CollectionUtils.isEmpty(vo.getApplySupplier())) {
            return ResponseUtils.toBoolean(response);
        }
        vo.getApplySupplier().stream().forEach(applyDTO -> {
            applyDTO.setSourcingId(response.getData());
            applyDTO.setApplyType(ApplyType.BOOK.getValue());
            applyDTO.setSourcingType(vo.getSourcingType());
        });
        RpcResponse<Boolean> result = applyWriteFacade.applys(CommandRequest.of(vo.getApplySupplier()));
        return ResponseUtils.operateResult(result);
    }

    /**
     * 更新
     *
     * @param vo
     * @return
     */
    public RestResponse<Boolean> update(SourcingVo vo) {
        //校验
        SourcingVo sourcingVo = queryById(vo.getId(), false);
//        if(!SourcingStatus.draft.eq(sourcingVo.getStatus())){
//            return RestResponse.fail("1001","当前状态不可编辑");
//        }
        SourcingDTO dto = convert.vo2DTO(vo);
        RpcResponse<Boolean> response = writeFacade.update(CommandRequest.of(dto));
        return ResponseUtils.operateResult(response);
    }

    /**
     * 更新
     *
     * @param vo
     * @return
     */
    public RestResponse<Boolean> updateStatus(SourcingVo vo) {
        SourcingDTO dto = convert.vo2DTO(vo);
        RpcResponse<Boolean> response = writeFacade.updateStatus(CommandRequest.of(dto));
        return ResponseUtils.operateResult(response);
    }

    public SourcingVo queryById(Long sourcingId, Boolean includeDetail) {
        CommonIdQuery query = CommonIdQuery.of(sourcingId, includeDetail);
        RpcResponse<SourcingDTO> response = readFacade.queryById(query);
        return convert.dto2Vo(response.getData());
    }

    /**
     * 返回结果
     *
     * @param query
     * @return
     */
    public RestResponse<PageInfo<SourcingVo>> page(SourcingQuery query) {
        RpcResponse<PageInfo<SourcingDTO>> response = readFacade.page(query);
        if (!response.isSuccess()) {
            return RestResponse.fail("1001", I18NMessageUtils.getMessage("interface.error"));  //# "接口错误"
        }
        return ResponseUtils.convertVOPageResponse(response, convert::dto2Vo, false);
    }

    public void fillDetail(List<SourcingVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Long> idList = list.stream().map(SourcingVo::getId).collect(Collectors.toList());

        // 物料 & 类目
        {
            IdListQuery req = new IdListQuery();
            req.setIdList(idList);
            RpcResponse<List<SourcingMaterialDTO>> resp = readFacade.queryMaterialsBySourcingId(req);
            if (!resp.isSuccess()) {
                throw new GmallException(ErrorCodeUtils.mapCode(resp.getFail()));
            }
            List<SourcingMaterialVo> materials = convert.convertMaterials(resp.getData());
            Multimap<Long, SourcingMaterialVo> map = ArrayListMultimap.create();
            for (SourcingMaterialVo m : materials) {
                map.put(m.getBizId(), m);
                List<CategoryVO> catePath = Optional.ofNullable(m.getExtra())
                        .map(x -> x.get(SourcingConstants.MATERIAL_EXT_KEY_CATEGORY))
                        .filter(StringUtils::isNotBlank)
                        .map(x -> JSON.parseArray(x, CategoryVO.class))
                        .orElse(null);
                //List<CategoryVO> catePath = categoryService.queryCategoryPathById(m.getCategoryId());
                m.setCategoryList(catePath);
            }
            for (SourcingVo s : list) {
                Collection<SourcingMaterialVo> mList = map.get(s.getId());
                if (!CollectionUtils.isEmpty(mList)) {
                    s.setMaterials(new ArrayList(mList));
                }
            }
        }

        // 报价数量
        {
            List<QuoteCountDTO> counts = quotePriceFacade.queryCountBySourcing(idList);
            Map<Long, QuoteCountDTO> countMap = counts.stream().collect(Collectors.toMap(
                    QuoteCountDTO::getSourcingId, Function.identity()));
            for (SourcingVo s : list) {
                QuoteCountDTO count = countMap.get(s.getId());
                if (count != null) {
                    s.setQuoteCount(count.getQuoteCount());
                }
            }
        }
    }

    public RestResponse<Boolean> stop(CommonByIdQuery query) {
        RpcResponse<Boolean> response = writeFacade.stop(query);
        return ResponseUtils.operateResult(response);
    }
}
