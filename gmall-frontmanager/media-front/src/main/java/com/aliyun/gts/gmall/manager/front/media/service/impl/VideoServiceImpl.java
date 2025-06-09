package com.aliyun.gts.gmall.manager.front.media.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoInfoRpcReq;
import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoLikesRpcReq;
import com.aliyun.gts.gmall.center.media.api.dto.output.Result;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoCategoryDTO;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoInfoDTO;
import com.aliyun.gts.gmall.center.media.api.dto.output.VideoInfoDTO;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoCategoryReadFacade;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoCommentReadFacade;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoInfoReadFacade;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoInfoWriteFacade;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoLikesReadFacade;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoLikesWriteFacade;
import com.aliyun.gts.gmall.center.media.api.facade.VodFacade;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoInfoQueryReq;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoLikesQueryReq;
import com.aliyun.gts.gmall.center.media.common.enums.VideoCommentStatusEnum;
import com.aliyun.gts.gmall.center.media.common.enums.VideoStatusEnum;
import com.aliyun.gts.gmall.center.user.api.dto.input.CustShopInterestRelQueryReq;
import com.aliyun.gts.gmall.center.user.api.dto.input.CustShopInterestRelRpcReq;
import com.aliyun.gts.gmall.center.user.api.facade.CustShopInterestRelReadFacade;
import com.aliyun.gts.gmall.center.user.api.facade.CustShopInterestRelWriteFacade;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.ShopInfoVO;
import com.aliyun.gts.gmall.manager.front.customer.enums.SellerConstants;
import com.aliyun.gts.gmall.manager.front.media.converter.ShortVideoConverter;
import com.aliyun.gts.gmall.manager.front.media.dto.VideoInitializeQuery;
import com.aliyun.gts.gmall.manager.front.media.enums.LikesOperateTypeEnum;
import com.aliyun.gts.gmall.manager.front.media.service.VideoService;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoVO;
import com.aliyun.gts.gmall.manager.front.media.web.output.VideoInitializeInfoVO;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemReadFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByBatchIdsQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerExtendDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description 视频管理服务实现类
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/13 15:52
 **/
@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Resource
    private ShortVideoInfoReadFacade readFacade;
    @Resource
    private ShortVideoInfoWriteFacade writeFacade;
    @Resource
    private ShortVideoLikesReadFacade likesReadFacade;
    @Resource
    private ShortVideoLikesWriteFacade likesWriteFacade;
    @Resource
    private ShortVideoCommentReadFacade videoCommentReadFacade;
    @Resource
    private ShortVideoCategoryReadFacade shortVideoCategoryReadFacade;
    @Resource
    private SellerReadFacade sellerReadFacade;
    @Resource
    private ItemReadFacade itemReadFacade;
    @Resource
    private CustShopInterestRelWriteFacade interestRelWriteFacade;
    @Resource
    private CustShopInterestRelReadFacade interestRelReadFacade;
    @Resource
    private VodFacade vodFacade;

    @Resource
    ShortVideoConverter converter;

    @Resource
    @Qualifier("promPublishConsumeCacheManager")
    private CacheManager promPublishConsumeCacheManager;

    @Resource
    @Qualifier("cacheManager")
    private CacheManager videoImageCacheManager;

    private String preFixVideo = "video_image_url_";

    private String preFixItem = "promotion_publish_item_";
    /**
     * 查询视频详情
     * @param  query 视频ID
     * @return
     */
    @Override
    public ShortVideoVO queryDetail(CommonByIdQuery query) {
        log.info("************ VideoService#queryDetail start");
        RpcResponse<ShortVideoInfoDTO> response =  readFacade.queryById(query);
        log.info("************ VideoService#queryDetail response = {}", JSONObject.toJSONString(response));
        ShortVideoVO shortVideoVO = converter.dto2VO(response.getData());
        log.info("************ VideoService#queryDetail shortVideoVO = {}", JSONObject.toJSONString(shortVideoVO));
        fillShortVideoInfoForDetail(shortVideoVO);
        return shortVideoVO;
    }

    /**
     * 更新视频状态
     * @param shortVideoDTO
     * @return
     */
    @Override
    public Boolean updateStatus(@RequestBody ShortVideoInfoRpcReq shortVideoDTO) {
        if (Objects.isNull(shortVideoDTO) || Objects.isNull(shortVideoDTO.getId())) {
            return Boolean.FALSE;
        }
        //根据id更新数据
        return writeFacade.updateShortVideoInfo(shortVideoDTO).getData();
    }

    @Override
    public PageInfo<ShortVideoVO> listByPage(ShortVideoInfoQueryReq query) {
        RestResponse<PageInfo<ShortVideoVO>> response = ResponseUtils.convertVOPageResponse(readFacade.page(query),
                converter::dto2VO, false);
        if (Objects.nonNull(response) && Objects.nonNull(response.getData())
                && CollectionUtils.isNotEmpty(response.getData().getList())) {
            // 批量数据准备
            // 商家信息
            Set<Long> sellerIdSet = Sets.newHashSet();
            List<Long> sellerIds = response.getData().getList().stream().map(ShortVideoVO::getStoreId).filter(Objects::nonNull).collect(
                Collectors.toList());
            sellerIdSet.addAll(sellerIds);
            CommonByBatchIdsQuery sellerQuery = new CommonByBatchIdsQuery();
            sellerQuery.setIds(new ArrayList<>(sellerIdSet));
            RpcResponse<List<SellerDTO>> sellerListResponse = sellerReadFacade.queryByIds(sellerQuery);
            Map<Long, SellerDTO> sellerDTOMap = Maps.newHashMap();
            if (Objects.nonNull(sellerListResponse) && CollectionUtils.isNotEmpty(sellerListResponse.getData())) {
                // 转换为sellerId为Key，sellerDTO为Value的Map
                sellerDTOMap = sellerListResponse.getData().stream()
                    .collect(Collectors.toMap(SellerDTO::getId, Function.identity(), (key1, key2) -> key1));
            }

            for (ShortVideoVO item : response.getData().getList()) {
                if (Objects.isNull(item)) {
                    continue;
                }
                // 性能优化，去掉无用信息，减小数据包大小
                item.setProductInfo(null);
                // 补充seller信息
                SellerDTO sellerDTO = sellerDTOMap.get(item.getStoreId());
                if (Objects.nonNull(sellerDTO)) {
                    List<SellerExtendDTO> sellerExtendDTOS = sellerDTO.getSellerExtends();
                    Optional<SellerExtendDTO> sellerExtendDTOOptional = sellerExtendDTOS.stream()
                            .filter(p -> SellerConstants.EXTEND_SHOP_TYPE.equalsIgnoreCase(p.getType())).findFirst();
                    if (sellerExtendDTOOptional.isPresent()) {
                        JSONObject jsonObject = JSONObject.parseObject(sellerExtendDTOOptional.get().getV());
                        item.setLogoUrl(jsonObject.getString("logoUrl"));
                    }
                }
                fillShortVideoInfoForList(item);
            }
        }
        return response.getData();
    }

    private void fillShortVideoInfoForList(ShortVideoVO item) {
        log.info("************ VideoService#fillShortVideoInfo start");
//        // 添加评论数
//        ShortVideoCommentDTO commentQuery = new ShortVideoCommentDTO();
//        commentQuery.setVideoId(item.getId());
//        RpcResponse<Long> commentNum = videoCommentReadFacade.countRelatedNum(commentQuery);
//        item.setCommentsNum(commentNum.getData().intValue());
//        // 添加点赞数
//        ShortVideoLikesDTO likesQuery = new ShortVideoLikesDTO();
//        likesQuery.setVideoId(item.getId());
//        RpcResponse<Long> likesNum = likesReadFacade.countRelatedNum(likesQuery);
//        item.setLikesNum(likesNum.getData().intValue());
        //添加分类名称
        // 性能优化，没有使用，去掉
//        CommonByIdQuery categoryQuery = new CommonByIdQuery();
//        categoryQuery.setId(item.getCategoryId());
//        RpcResponse<ShortVideoCategoryDTO> category = shortVideoCategoryReadFacade.queryById(categoryQuery);
//        if (Objects.nonNull(category) && Objects.nonNull(category.getData())) {
//            item.setCategoryName(category.getData().getName());
//        }
        //枚举转换
        item.setCommentStatusName(VideoCommentStatusEnum.getName(item.getCommentStatus()));
        item.setVideoStatusName(VideoStatusEnum.getName(item.getVideoStatus()));
        // 店铺名称
        // 性能优化移到循环外
//        com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdQuery sellerQuery = new com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdQuery();
//        sellerQuery.setId(item.getStoreId());
//        log.info("************ VideoService#fillShortVideoInfo 查询店铺 start");
//        RpcResponse<SellerDTO> seller = sellerReadFacade.query(sellerQuery);
//        log.info("************ VideoService#fillShortVideoInfo 查询店铺结果：{}", JSONObject.toJSONString(seller));
//        if (Objects.nonNull(seller) && Objects.nonNull(seller.getData())) {
//            item.setSellerDTO(seller.getData());
//            List<SellerExtendDTO> sellerExtendDTOS = seller.getData().getSellerExtends();
//            for (SellerExtendDTO extendDTO : sellerExtendDTOS) {
//                if ("shop_0".equals(extendDTO.getK()) && StringUtils.isNoneBlank(extendDTO.getV())) {
//                    JSONObject jsonObject = JSONObject.parseObject(extendDTO.getV());
//                    seller.getData().setHeadUrl(jsonObject.getString("logoUrl"));
//                    break;
//                }
//            }
//        }
        // 商品信息
        // 性能优化列表不需要查商品信息
//        String jsonStrDetail = promPublishConsumeCacheManager.get(preFixItem + item.getProductId());
//        JSONObject  deatilJson = JSONObject.parseObject(jsonStrDetail);
//        item.setItemDetail(deatilJson);

       /* ItemQueryReq itemQuery = new ItemQueryReq();
        itemQuery.setItemId(item.getProductId());
        RpcResponse<ItemDTO> product = itemReadFacade.queryItem(itemQuery);
        log.info("************ VideoService#fillShortVideoInfo 查询商品结果：{}", JSONObject.toJSONString(product));
        if (Objects.nonNull(product) && Objects.nonNull(product.getData())) {
            item.setItemDTO(product.getData());
        }*/

        // 视频封面
        if (StringUtils.isBlank(item.getVideoImage())) {
            // 从缓存获取
            String videoImageUrlCache = videoImageCacheManager.get(preFixVideo + item.getCode());
            if (StringUtils.isBlank(videoImageUrlCache)) {
                // 从vod获取
                // 链接有效时间，单位秒
                long authInfoTimeOut = 3000L;
                Result<VideoInfoDTO> playAuth = vodFacade.getVideoPlayAuth(item.getCode(), authInfoTimeOut);
                if (Objects.nonNull(playAuth) && Objects.nonNull(playAuth.getData()) && StringUtils.isNotBlank(playAuth.getData().getCoverUrl())) {
                    item.setVideoImage(playAuth.getData().getCoverUrl());
                    // 缓存时间=链接有效时间（秒）-300秒，考虑拿到的缓存链接有效时间最少也有将近5分钟
                    videoImageCacheManager.set(preFixVideo + item.getCode(), playAuth.getData().getCoverUrl(), authInfoTimeOut - 300, TimeUnit.SECONDS);
                }
            }
            else {
                item.setVideoImage(videoImageUrlCache);
            }
        }
        log.info("************ VideoService#fillShortVideoInfo end");
    }

    private void fillShortVideoInfoForDetail(ShortVideoVO item) {
        log.info("************ VideoService#fillShortVideoInfo start");
        if (Objects.isNull(item)) {
            return;
        }
        //        // 添加评论数
        //        ShortVideoCommentDTO commentQuery = new ShortVideoCommentDTO();
        //        commentQuery.setVideoId(item.getId());
        //        RpcResponse<Long> commentNum = videoCommentReadFacade.countRelatedNum(commentQuery);
        //        item.setCommentsNum(commentNum.getData().intValue());
        //        // 添加点赞数
        //        ShortVideoLikesDTO likesQuery = new ShortVideoLikesDTO();
        //        likesQuery.setVideoId(item.getId());
        //        RpcResponse<Long> likesNum = likesReadFacade.countRelatedNum(likesQuery);
        //        item.setLikesNum(likesNum.getData().intValue());
        //添加分类名称
        CommonByIdQuery categoryQuery = new CommonByIdQuery();
        categoryQuery.setId(item.getCategoryId());
        RpcResponse<ShortVideoCategoryDTO> category = shortVideoCategoryReadFacade.queryById(categoryQuery);
        if (Objects.nonNull(category) && Objects.nonNull(category.getData())) {
            item.setCategoryName(category.getData().getName());
        }
        //枚举转换
        item.setCommentStatusName(VideoCommentStatusEnum.getName(item.getCommentStatus()));
        item.setVideoStatusName(VideoStatusEnum.getName(item.getVideoStatus()));
        // 店铺名称
        com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdQuery sellerQuery = new com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdQuery();
        sellerQuery.setId(item.getStoreId());
        log.info("************ VideoService#fillShortVideoInfo 查询店铺 start");
        RpcResponse<SellerDTO> seller = sellerReadFacade.query(sellerQuery);
        log.info("************ VideoService#fillShortVideoInfo 查询店铺结果：{}", JSONObject.toJSONString(seller));
        if (Objects.nonNull(seller) && Objects.nonNull(seller.getData())) {
            item.setSellerDTO(seller.getData());
            List<SellerExtendDTO> sellerExtendDTOS = seller.getData().getSellerExtends();
            for (SellerExtendDTO extendDTO : sellerExtendDTOS) {
                if ("shop_0".equals(extendDTO.getK()) && StringUtils.isNoneBlank(extendDTO.getV())) {
                    JSONObject jsonObject = JSONObject.parseObject(extendDTO.getV());
                    seller.getData().setHeadUrl(jsonObject.getString("logoUrl"));
                    break;
                }
            }
        }
        // 商品信息
        log.info("************ VideoService#fillShortVideoInfo 查询商品 start");
        String jsonStrDetail = promPublishConsumeCacheManager.get(preFixItem + item.getProductId());
        JSONObject  deatilJson = JSONObject.parseObject(jsonStrDetail);
        item.setItemDetail(deatilJson);

       /* ItemQueryReq itemQuery = new ItemQueryReq();
        itemQuery.setItemId(item.getProductId());
        RpcResponse<ItemDTO> product = itemReadFacade.queryItem(itemQuery);
        log.info("************ VideoService#fillShortVideoInfo 查询商品结果：{}", JSONObject.toJSONString(product));
        if (Objects.nonNull(product) && Objects.nonNull(product.getData())) {
            item.setItemDTO(product.getData());
        }*/

        // 视频封面
        if (StringUtils.isBlank(item.getVideoImage())) {
            // 从缓存获取
            String videoImageUrlCache = videoImageCacheManager.get(preFixVideo + item.getCode());
            if (StringUtils.isBlank(videoImageUrlCache)) {
                // 从vod获取
                // 链接有效时间，单位秒
                long authInfoTimeOut = 3000L;
                Result<VideoInfoDTO> playAuth = vodFacade.getVideoPlayAuth(item.getCode(), authInfoTimeOut);
                if (Objects.nonNull(playAuth) && Objects.nonNull(playAuth.getData()) && StringUtils.isNotBlank(playAuth.getData().getCoverUrl())) {
                    item.setVideoImage(playAuth.getData().getCoverUrl());
                    // 缓存时间=链接有效时间（秒）-300秒，考虑拿到的缓存链接有效时间最少也有将近5分钟
                    videoImageCacheManager.set(preFixVideo + item.getCode(), playAuth.getData().getCoverUrl(), authInfoTimeOut - 300, TimeUnit.SECONDS);
                }
            }
            else {
                item.setVideoImage(videoImageUrlCache);
            }
        }
        log.info("************ VideoService#fillShortVideoInfo end");
    }
    /**
     * 更新评论状态
     * @param shortVideoDTO
     * @return
     */
    @Override
    public Boolean updateCommentStatus(@RequestBody ShortVideoInfoRpcReq shortVideoDTO) {
        return this.updateStatus(shortVideoDTO);
    }

    /**
     * 新增或编辑短视频
     * @param shortVideoDTO
     * @return
     */
    @Override
    public Boolean saveOrUpdate(ShortVideoInfoRpcReq shortVideoDTO) {
        if (Objects.isNull(shortVideoDTO)) {
            return Boolean.FALSE;
        }
        // 如果传主键ID则更新
        if (Objects.nonNull(shortVideoDTO.getId())) {
            //根据id更新数据
            return writeFacade.updateShortVideoInfo(shortVideoDTO).getData();
        }
        if (writeFacade.createShortVideoInfo(shortVideoDTO).getData() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 根据ID删除
     * @param query
     * @return
     */
    @Override
    public Boolean deleteById(CommonByIdQuery query) {
        return   writeFacade.deleteShortVideoInfo(query).getData();
    }

    /**
     * 点赞/取消赞
     * @param query
     * @return
     */
    @Override
    public Boolean addOrCancelLikes(ShortVideoLikesRpcReq query) {
        if (Objects.isNull(query) || Objects.isNull(query.getVideoId())) {
            return Boolean.FALSE;
        }
        if (LikesOperateTypeEnum.ADD.getcode().equals(query.getOperateType())) {
            query.setId(null);
            likesWriteFacade.createShortVideoLikes(query);
            return Boolean.TRUE;
        } else if (LikesOperateTypeEnum.DELETE.getcode().equals(query.getOperateType())) {
            CommonByIdQuery commonByIdQuery = new CommonByIdQuery();
            commonByIdQuery.setId(query.getId());
            likesWriteFacade.deleteShortVideoLikes(commonByIdQuery);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 查询下一条视频
     * @param query
     * @return
     */
    @Override
    public ShortVideoVO queryNextVideo(ShortVideoInfoQueryReq query) {
        if (Objects.isNull(query) || Objects.isNull(query.getId())) {
            return null;
        }
        Long nextId = readFacade.queryNextVideo(query).getData();
        if (Objects.nonNull(nextId)) {
            CommonByIdQuery nextCommandQuery = new CommonByIdQuery();
            nextCommandQuery.setId(nextId);
            return queryDetail(nextCommandQuery);
        }
        return null;
    }
    @Override
    public Boolean cancelInterest(CommonByIdQuery query) {
        return interestRelWriteFacade.deleteCustShopInterestRel(query).getData();
    }


    @Override
    public Long addInterest(CustShopInterestRelRpcReq custShopInterestRelDTO) {
        return interestRelWriteFacade.createCustShopInterestRel(custShopInterestRelDTO).getData();
    }

    @Override
    public VideoInitializeInfoVO getVideoInitializeInfo(VideoInitializeQuery query) {
        log.info("******VideoServiceImpl#getVideoInitializeInfo query= " + JSONObject.toJSONString(query));
        VideoInitializeInfoVO videoInitializeInfoVO = new VideoInitializeInfoVO();

        CustShopInterestRelQueryReq interestRelQueryReq = new CustShopInterestRelQueryReq();
        interestRelQueryReq.setShopId(query.getShopId());
        interestRelQueryReq.setUserId(query.getCustId());
        log.info("******VideoServiceImpl#getVideoInitializeInfo interestRelQueryReq = " + JSONObject.toJSONString(interestRelQueryReq));
        Long interestId = interestRelReadFacade.checkInterest(interestRelQueryReq).getData();
        ShortVideoLikesQueryReq likesQueryReq = new ShortVideoLikesQueryReq();
        likesQueryReq.setVideoId(query.getVideoId());
        likesQueryReq.setUserId(query.getCustId());
        log.info("******VideoServiceImpl#getVideoInitializeInfo likesQueryReq = " + JSONObject.toJSONString(likesQueryReq));
        Long likes = likesReadFacade.checkLikes(likesQueryReq).getData();
        //查询短视频详情
        CommonByIdQuery videoQuery = new CommonByIdQuery();
        videoQuery.setId(query.getVideoId());
        ShortVideoInfoDTO shortVideoInfoDTO = readFacade.queryById(videoQuery).getData();
        if (Objects.nonNull(shortVideoInfoDTO)) {
            //点赞数量
            videoInitializeInfoVO.setLikesNum(shortVideoInfoDTO.getLikesNum());
            //评论数量
            videoInitializeInfoVO.setCommentsNum(shortVideoInfoDTO.getCommentsNum());
            videoInitializeInfoVO.setShareNum(shortVideoInfoDTO.getShareNum());
        }
        videoInitializeInfoVO.setInterst(Objects.nonNull(interestId) ? Boolean.TRUE : Boolean.FALSE);
        videoInitializeInfoVO.setInterstId(interestId);
        videoInitializeInfoVO.setLikes(Objects.nonNull(likes) ? Boolean.TRUE : Boolean.FALSE);
        videoInitializeInfoVO.setLikesId(likes);
        return videoInitializeInfoVO;

    }

    @Override
    public Integer share(CommonByIdQuery query) {
        if (Objects.isNull(query) || Objects.isNull(query.getId())) {
            return 0;
        }
        ShortVideoInfoDTO shortVideoInfoDTO = readFacade.queryById(query).getData();
        if (Objects.isNull(shortVideoInfoDTO)) {
            return 0;
        }
        ShortVideoInfoRpcReq shortVideoDTO = converter.dto2Req(shortVideoInfoDTO);
        Integer shareNum = Objects.isNull(shortVideoDTO.getShareNum())? 1 : shortVideoDTO.getShareNum() + 1;
        shortVideoDTO.setShareNum(shareNum);
        saveOrUpdate(shortVideoDTO);

        return shareNum;
    }
}
