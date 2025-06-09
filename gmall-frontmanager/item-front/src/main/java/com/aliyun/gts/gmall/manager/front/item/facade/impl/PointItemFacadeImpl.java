package com.aliyun.gts.gmall.manager.front.item.facade.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSONArray;
import com.aliyun.gts.gmall.center.item.api.dto.input.PointItemQueryReq;
import com.aliyun.gts.gmall.center.item.api.dto.output.PointItemQueryDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.PointItemQueryDetailDTO;
import com.aliyun.gts.gmall.center.item.api.facade.PointItemQueryFacade;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.exception.GmallInvalidArgumentException;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.ByIdCouponQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.PromCampaignVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.PromDetailVO;
import com.aliyun.gts.gmall.manager.front.customer.facade.PromotionFacade;
import com.aliyun.gts.gmall.manager.front.item.convertor.PointItemConvertor;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.ItemDetailRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.PointItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.PointItemQueryVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuVO;
import com.aliyun.gts.gmall.manager.front.item.facade.ItemDetailFacade;
import com.aliyun.gts.gmall.manager.front.item.facade.PointItemFacade;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PointItemFacadeImpl implements PointItemFacade {

    @Resource
    private PointItemQueryFacade pointItemQueryFacade;

    @Autowired
    private ItemDetailFacade itemDetailFacade;

    @Autowired
    private PointItemConvertor pointItemConvertor;

    @Resource
    private PromotionFacade promotionFacade;

    @Override
    public RestResponse<PageInfo<PointItemQueryVO>> queryPointItemPage(PointItemQueryReq itemQueryReq) {
        try {
            PageInfo pageInfo = new PageInfo<>();
            RpcResponse<PageInfo<PointItemQueryDTO>> pageInfoRpcResponse = pointItemQueryFacade.queryPointItemPage(itemQueryReq);
            if (pageInfoRpcResponse.isSuccess() && CollectionUtils.isNotEmpty(pageInfoRpcResponse.getData().getList())) {
                List<PointItemQueryVO> pointItemQueryVOS = convertPointItemQueryVO(pageInfoRpcResponse.getData().getList(), 1);
                //升序
                //pointItemQueryVOS.stream().sorted(Comparator.comparingLong(PointItemQueryVO::getRealPrice));
                pageInfo.setTotal(pageInfoRpcResponse.getData().getTotal());
                pageInfo.setList(pointItemQueryVOS);
                return RestResponse.ok(pageInfo);

            } else if (!pageInfoRpcResponse.isSuccess()) {
                return RestResponse.fail(pageInfoRpcResponse.getFail().getCode(),pageInfoRpcResponse.getFail().getMessage());
            } else {
                return RestResponse.ok(pageInfo);
            }
        } catch (Throwable e) {
            log.error("查询积分商品列表失败,queryPointItemPage:{}",itemQueryReq,e);
            if(e instanceof GmallException){
                return RestResponse.fail(((GmallException) e).getFrontendCare().getCode());
            }else{
                return RestResponse.fail("", I18NMessageUtils.getMessage("points.product.list.query.fail"));  //# "查询积分商品列表失败"
            }
        }
    }

    @Override
    public RestResponse<ItemDetailVO> queryPointItemDetail(PointItemQueryReq itemQueryReq) {
        try {
            RpcResponse<PointItemQueryDTO> pointItemQueryDTORpcResponse = pointItemQueryFacade.queryPointItemDetail(itemQueryReq);
            if (pointItemQueryDTORpcResponse.isSuccess() && pointItemQueryDTORpcResponse.getData() != null) {
                ItemDetailVO itemDetailVO = queryPointItemDetail(pointItemQueryDTORpcResponse.getData());

                return RestResponse.ok(itemDetailVO);
            } else if (!pointItemQueryDTORpcResponse.isSuccess()) {
                return RestResponse.fail(pointItemQueryDTORpcResponse.getFail().getCode(),pointItemQueryDTORpcResponse.getFail().getMessage());
            } else {
                return RestResponse.ok(null);
            }
        } catch (Throwable e) {
            log.error("查询积分商品详情失败,queryPointItemDetail:{}",itemQueryReq,e);
            if(e instanceof  GmallException){
                return RestResponse.fail(((GmallException) e).getFrontendCare().getCode());
            }
            else{
                return RestResponse.fail("", I18NMessageUtils.getMessage("points.product.details.query.fail"));  //# "查询积分商品详情失败"
            }
        }
    }

    @Override
    public RestResponse<PageInfo<PointItemQueryVO>> queryPointCouponPage(PointItemQueryReq itemQueryReq) {
        //排序
        PageInfo pageInfo = new PageInfo<>();
        try {
            RpcResponse<PageInfo<PointItemQueryDTO>> pageInfoRpcResponse = pointItemQueryFacade.queryPointCouponPage(itemQueryReq);
            if (pageInfoRpcResponse.isSuccess() && CollectionUtils.isNotEmpty(pageInfoRpcResponse.getData().getList())) {
                List<PointItemQueryVO> pointItemQueryVOS = convertPointItemQueryVO(pageInfoRpcResponse.getData().getList(), 3);
                //升序
                //pointItemQueryVOS.stream().sorted(Comparator.comparingLong(PointItemQueryVO::getPointCount));
                pageInfo.setTotal(pageInfoRpcResponse.getData().getTotal());
                pageInfo.setList(pointItemQueryVOS);
                return RestResponse.ok(pageInfo);

            } else if (!pageInfoRpcResponse.isSuccess()) {
                return RestResponse.fail(pageInfoRpcResponse.getFail().getCode(),pageInfoRpcResponse.getFail().getMessage());
            } else  {
                return RestResponse.ok(pageInfo);
            }
        } catch (Throwable e) {
            log.error("查询积分优惠券列表失败,queryPointCouponPage:{}",itemQueryReq,e);
            if(e instanceof  GmallException){
                return RestResponse.fail(((GmallException) e).getFrontendCare().getCode());
            }
            return RestResponse.fail("", I18NMessageUtils.getMessage("coupon.list.query.fail"));  //# "查询优惠券列表失败"
        }
    }

    private PointItemDetailVO queryPointItemDetail(PointItemQueryDTO pointItemQueryDTO){
        List<ItemSkuVO> itemSkuVOS = new ArrayList<>();
        List<PointItemQueryDetailDTO> details = pointItemQueryDTO.getDetails();
        Map<Long, List<PointItemQueryDetailDTO>> collect = details.stream().collect(Collectors.groupingBy(PointItemQueryDetailDTO::getSubBizId));
        Set<Long> skuIds = collect.keySet();
        ItemDetailRestQuery itemDetailRestQuery = new ItemDetailRestQuery();
        itemDetailRestQuery.setItemId(pointItemQueryDTO.getBizId());
        ItemDetailVO itemDetailVO = queryCommonItem(itemDetailRestQuery);
        if(itemDetailVO!=null){
            itemDetailVO.setCampaigns(null);
            itemDetailVO.setCoupons(null);
        }
        List<ItemSkuVO> itemSkuVOList = itemDetailVO.getItemSkuVOList();
        for (ItemSkuVO itemSkuVO:itemSkuVOList) {
            for (Long skuId:skuIds) {
                if(itemSkuVO.getId().equals(skuId)){
                    List<PointItemQueryDetailDTO> pointItemQueryDetailDTOS = collect.get(skuId);
                    if(CollectionUtils.isNotEmpty(pointItemQueryDetailDTOS)){
                        PointItemQueryDetailDTO pointItemQueryDetailDTO = pointItemQueryDetailDTOS.get(0);
                        itemSkuVO.setPointPrice(String.valueOf(pointItemQueryDetailDTO.getRealPrice()));
                        itemSkuVO.setPointNum(getPointNum(pointItemQueryDetailDTO.getPointCount()));
                        itemSkuVOS.add(itemSkuVO);
                    }
                }

            }
        }
        itemDetailVO.setItemSkuVOList(itemSkuVOS);
        PointItemDetailVO pointItemDetailVO = pointItemConvertor.convertPointItemDetailVO(itemDetailVO);
        PointItemQueryDetailDTO pointItemQueryDetail = details.stream().min(Comparator.comparingLong(PointItemQueryDetailDTO::getPointCount)).get();
        pointItemDetailVO.setPointNum(getPointNum(pointItemQueryDetail.getPointCount()));
        pointItemDetailVO.setPointPrice(String.valueOf(pointItemQueryDetail.getRealPrice()));
        pointItemDetailVO.setTitle(pointItemQueryDTO.getTitle());
        fillPointItemDetailOriginalPrice(pointItemQueryDTO.getItemDTO(),pointItemDetailVO);
        return pointItemDetailVO;
    }

    private void fillPointItemDetailOriginalPrice(ItemDTO itemDTO, PointItemDetailVO pointItemDetailVO){
        if (itemDTO == null) {
            return;
        }
        List<SkuDTO> skuList = itemDTO.getSkuList();
        if (CollectionUtils.isEmpty(skuList)){
            return;
        }
//        Optional<SkuDTO> min = skuList.stream().min(Comparator.comparing(SkuDTO::getPrice));
//        if (min.isPresent() && min.get().getPrice() !=null ){
//            pointItemDetailVO.setOriginalPrice(ItemUtils.fen2Yuan(min.get().getPrice()));
//        }
    }

    private ItemDetailVO queryCommonItem(ItemDetailRestQuery itemDetailRestQuery) {
        return itemDetailFacade.queryItemDetail(itemDetailRestQuery);
    }

    private List<PointItemQueryVO> convertPointItemQueryVO(List<PointItemQueryDTO> pointItemQueryDTOList, Integer bizType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        List<PointItemQueryVO> pointItemQueryVOS = new ArrayList<>();
        //这边先写死，积分商品1 优惠券3
        if (CollectionUtils.isNotEmpty(pointItemQueryDTOList)) {
            for (PointItemQueryDTO pointItemQueryDTO : pointItemQueryDTOList) {
                PointItemQueryVO pointItemQueryVO = pointItemConvertor.convertVO(pointItemQueryDTO);
                List<PointItemQueryDetailDTO> details = pointItemQueryVO.getDetails();
                //积分商品去sku中拿价格最低的sku来填充到vo的字段中去
                if (CollectionUtils.isNotEmpty(details)) {
                    if (bizType.equals(Integer.valueOf(1))) {
                        PointItemQueryDetailDTO pointItemQueryDetailDTO = details.stream().min(Comparator.comparingLong(PointItemQueryDetailDTO::getPointCount)).get();
                        pointItemQueryVO.setPointCount(pointItemQueryDetailDTO.getPointCount());
                        pointItemQueryVO.setPointCountStr(getPointNum(pointItemQueryDetailDTO.getPointCount()));
                        pointItemQueryVO.setRealPrice(pointItemQueryDetailDTO.getRealPrice());
                        pointItemQueryVO.setRealPriceStr(String.valueOf(pointItemQueryDetailDTO.getRealPrice()));
                        //取到对应的sku的原价和库存数量放入VO中
                        if(CollectionUtils.isNotEmpty(pointItemQueryVO.getItemDTO().getSkuList())){
                            List<SkuDTO> collect = pointItemQueryVO.getItemDTO().getSkuList().stream().filter(SkuDTO -> SkuDTO.getId().equals(pointItemQueryDetailDTO.getSubBizId())).collect(Collectors.toList());
                            if(CollectionUtils.isNotEmpty(collect)){
//                                pointItemQueryVO.setOriginalPrice(ItemUtils.fen2Yuan(collect.get(0).getPrice()));
//                                pointItemQueryVO.setItemQuantity(collect.get(0).getQuantity());
                            }
                        }
                    } else if (bizType.equals(Integer.valueOf(3))) {
                        pointItemQueryVO.setPointCount(details.get(0).getPointCount());
                        pointItemQueryVO.setPointCountStr(getPointNum(details.get(0).getPointCount()));
                        //把picture字段处理下返回的是这种格式的  "[\"https://testproject-myth-image-dev.oss-cn-hangzhou.aliyuncs.com/itembb5f2a1a-57a1-405f-b34c-c92b9adb0591.jpg\"]"
                        if(StringUtils.isNotBlank(pointItemQueryVO.getPicture())){
                            pointItemQueryVO.setCouPonPictureList(JSONArray.parseArray(pointItemQueryVO.getPicture(), String.class));
                        }
                        pointItemQueryVO.setStartDate(simpleDateFormat.format(pointItemQueryVO.getPromCampaignDTO().getStartTime()));
                        pointItemQueryVO.setEndDate(simpleDateFormat.format(pointItemQueryVO.getPromCampaignDTO().getEndTime()));
                        pointItemQueryVO.setManYuan(String.valueOf(pointItemQueryVO.getPromCampaignDTO().getDiscountRule().getMan()));
                        pointItemQueryVO.setJianYuan(String.valueOf(pointItemQueryVO.getPromCampaignDTO().getDiscountRule().getJian()));
                    }
                }

                pointItemQueryVOS.add(pointItemQueryVO);
            }
        }
        return pointItemQueryVOS;
    }


    @Override
    public RestResponse<PointItemQueryVO> queryPointCouponDetail(PointItemQueryReq itemQueryReq) {
        try {
            RpcResponse<PointItemQueryDTO> pointItemQueryDTORpcResponse = pointItemQueryFacade.queryPointCouponDetail(itemQueryReq);
            if (pointItemQueryDTORpcResponse.isSuccess() && pointItemQueryDTORpcResponse.getData() != null) {
                List<PointItemQueryVO> pointItemQueryVOS = convertPointItemQueryVO(Arrays.asList(pointItemQueryDTORpcResponse.getData()), Integer.valueOf(3));
                if(CollectionUtils.isNotEmpty(pointItemQueryVOS)){
                    PointItemQueryVO pointItemQueryVO = pointItemQueryVOS.get(0);
                    pointItemQueryVO.setItemDetails(queryCouponItem(itemQueryReq));
                    Integer promotionActionType = pointItemQueryVO.getPromCampaignDTO().getPromTargetType();
                    if(Integer.valueOf(1).equals(promotionActionType)){
                        pointItemQueryVO.setLimit(I18NMessageUtils.getMessage("all.products.available"));  //# "全部商品可用"
                    }
                    else if(Integer.valueOf(2).equals(promotionActionType)){
                        pointItemQueryVO.setLimit(I18NMessageUtils.getMessage("specified.products.available"));  //# "指定商品可用"
                    }
                    else if(Integer.valueOf(4).equals(promotionActionType)){
                        pointItemQueryVO.setLimit(I18NMessageUtils.getMessage("specified.group.available"));  //# "指定分组可用"
                    }
                    return RestResponse.ok(pointItemQueryVO);
                }
                else{
                    return RestResponse.ok(null);
                }
            } else if (!pointItemQueryDTORpcResponse.isSuccess()) {
                return RestResponse.fail(pointItemQueryDTORpcResponse.getFail().getCode(), pointItemQueryDTORpcResponse.getFail().getMessage());
            } else  {
                return RestResponse.ok(null);
            }
        } catch (Throwable e) {
            log.error("查询积分优惠券详情失败,queryPointCouponDetail:{}",itemQueryReq,e);
            if(e instanceof  GmallException){
                return RestResponse.fail(((GmallException) e).getFrontendCare().getCode());
            }else{
                return RestResponse.fail("", I18NMessageUtils.getMessage("coupon.details.query.fail"));  //# "查询优惠券详情失败"
            }
        }
    }

    private List<PromDetailVO> queryCouponItem(PointItemQueryReq itemQueryReq){
        ByIdCouponQuery byIdCouponQuery = new ByIdCouponQuery();
        byIdCouponQuery.setCouponId(itemQueryReq.getBizId());
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(itemQueryReq.getPageIndex());
        pageParam.setPageSize(itemQueryReq.getPageSize());
        byIdCouponQuery.setPage(pageParam);
        RestResponse<PromCampaignVO> promCampaignVORestResponse = promotionFacade.queryCouponDetail(byIdCouponQuery);
        if(promCampaignVORestResponse.isSuccess()){
            return promCampaignVORestResponse.getData().getDetails();
        }
        else{
            throw new GmallInvalidArgumentException(promCampaignVORestResponse.getMessage());
        }
    }

    private String getPointNum(Long point){
        if(point==null){
            return null;
        }
        return new BigDecimal(point).divide(new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
    }
}
