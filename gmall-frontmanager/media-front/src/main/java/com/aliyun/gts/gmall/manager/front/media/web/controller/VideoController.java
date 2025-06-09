package com.aliyun.gts.gmall.manager.front.media.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.media.api.dto.input.ItemInfo;
import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoLikesRpcReq;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoInfoQueryReq;
import com.aliyun.gts.gmall.center.user.api.dto.input.CustShopInterestRelRpcReq;
import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.media.converter.ShortVideoConverter;
import com.aliyun.gts.gmall.manager.front.media.dto.ShortVideoInfoQuery;
import com.aliyun.gts.gmall.manager.front.media.dto.VideoInitializeQuery;
import com.aliyun.gts.gmall.manager.front.media.service.VideoService;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoVO;
import com.aliyun.gts.gmall.manager.front.media.web.output.VideoInitializeInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author wang.yulin01
 * @version V1.0
 * @description 短视频管理
 * @date 2023/4/14 14:31
 **/
@Api(value = "短视频操作", tags = {"短视频操作"})
@RestController
@RequestMapping(value = "/media/video")
@Slf4j
public class VideoController extends BaseRest {

    @Resource
    private VideoService videoService;
    @Resource
    ShortVideoConverter converter;

    @ApiOperation("根据ID查询短视频详情")
    @PostMapping(value = "/getById/token")
    public RestResponse<ShortVideoVO> queryDetail(@RequestBody CommonByIdQuery query) {
        CommonByIdQuery condition = new CommonByIdQuery();
        condition.setId(query.getId());
        ShortVideoVO vo = videoService.queryDetail(condition);
        log.info("************ VideoController#queryDetail vo = {}", JSONObject.toJSONString(vo));
        if (Objects.nonNull(vo) && StringUtils.isNotBlank(vo.getProduct())) {
            log.info("************ VideoController#queryDetail 商品信息转换开始");
            ItemInfo itemInfo = JSON.toJavaObject(JSONObject.parseObject(vo.getProduct()), ItemInfo.class);
            if (vo.getItemDetail() != null) {
                String minPromPrice = vo.getItemDetail().getString("minPromPrice");
                String minItemPrice = vo.getItemDetail().getString("minItemPrice");
                if(StringUtils.isNotBlank(minItemPrice)){
                    itemInfo.setMaxPrice(minItemPrice);
                }
                if(StringUtils.isNotBlank(minPromPrice)){
                    itemInfo.setPrice(minPromPrice);
                }
            }else{
                itemInfo.setMaxPrice(itemInfo.getPrice());
            }
            log.info("************ VideoController#queryDetail 商品信息转换结束");
            vo.setProductInfo(itemInfo);
        }
        return RestResponse.okWithoutMsg(vo);
    }


    @ApiOperation("分页查询短视频")
    @PostMapping(value = "/page/token")
    public RestResponse<PageInfo<ShortVideoVO>> listByPage(@RequestBody ShortVideoInfoQuery query) {
        ShortVideoInfoQueryReq condition = converter.query2Req(query);
        PageInfo<ShortVideoVO> result = videoService.listByPage(condition);
        return RestResponse.okWithoutMsg(result);
    }

    @ApiOperation("点赞/取消赞")
    @PostMapping(value = "/addOrCancelLikes")
    public RestResponse<Boolean> addOrCancelLikes(@RequestBody ShortVideoLikesRpcReq query) {
        OperatorDO operator = getLogin();
        if (Objects.isNull(operator)) {
            return RestResponse.okWithoutMsg(null);
        }
        query.setUserId(operator.getOperatorId());
        query.setUserName(operator.getUsername());
        query.setCreateId(operator.getOperatorId().toString());
        query.setUpdateId(operator.getOperatorId().toString());
        Boolean result = videoService.addOrCancelLikes(query);
        return RestResponse.okWithoutMsg(result);
    }

    @ApiOperation("下一条视频")
    @PostMapping(value = "/queryNextVideo")
    public RestResponse<ShortVideoVO> queryNextVideo(@RequestBody ShortVideoInfoQuery query) {
        ShortVideoInfoQueryReq condition = new ShortVideoInfoQueryReq();
        BeanUtils.copyProperties(query, condition);
        ShortVideoVO vo = videoService.queryNextVideo(condition);
        if (Objects.nonNull(vo) && StringUtils.isNotBlank(vo.getProduct())) {
            log.info("************ VideoController#queryDetail 商品信息转换开始");
            ItemInfo itemInfo = JSON.toJavaObject(JSONObject.parseObject(vo.getProduct()), ItemInfo.class);
            log.info("************ VideoController#queryDetail 商品信息转换结束");
            vo.setProductInfo(itemInfo);
        }
        return RestResponse.okWithoutMsg(vo);
    }
    @ApiOperation("取消关注店铺")
    @PostMapping(value = "/cancelInterest")
    public RestResponse<Boolean> cancelInterest(@RequestBody CommonByIdQuery query) {
        Boolean result = videoService.cancelInterest(query);
        return RestResponse.okWithoutMsg(result);
    }

    @ApiOperation("关注店铺")
    @PostMapping(value = "/addInterest")
    public RestResponse<Long> addInterest(@RequestBody CustShopInterestRelRpcReq custShopInterestRelDTO) {
        CustDTO user = UserHolder.getUser();
        if (user == null || user.getCustId() <= 0L) {
            return RestResponse.fail(CommonResponseCode.NotLogin);
        }
        OperatorDO operatorDO = getLogin();
        custShopInterestRelDTO.setCreateId(operatorDO.getOperatorId().toString());
        custShopInterestRelDTO.setUpdateId(operatorDO.getOperatorId().toString());
        custShopInterestRelDTO.setUserId(operatorDO.getOperatorId());
        Long aLong = videoService.addInterest(custShopInterestRelDTO);
        return RestResponse.okWithoutMsg(aLong);
    }

    @ApiOperation("查询视频初始化信息")
    @PostMapping(value = "/getVideoInitializeInfo")
    public RestResponse<VideoInitializeInfoVO> getVideoInitializeInfo(@RequestBody VideoInitializeQuery query) {
        VideoInitializeQuery initializeQuery = new VideoInitializeQuery();
        Long custId = query.getCustId();
        if (Objects.isNull(custId)) {
            return RestResponse.ok(null);
        }
        initializeQuery.setUserId(custId);
        return RestResponse.okWithoutMsg(videoService.getVideoInitializeInfo(query));
    }

    @ApiOperation("根据分享短视频")
    @PostMapping(value = "/share")
    public RestResponse<Integer> share(@RequestBody CommonByIdQuery query) {
        Integer shareNum = videoService.share(query);

        return RestResponse.okWithoutMsg(shareNum);
    }
}
