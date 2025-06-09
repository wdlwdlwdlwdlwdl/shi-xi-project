package com.aliyun.gts.gmall.manager.front.media.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoCommentRpcReq;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoCommentDTO;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoCommentReadFacade;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoCommentWriteFacade;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoCommentQueryReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerAdapter;
import com.aliyun.gts.gmall.manager.front.media.converter.ShortVideoCommentVOConverter;
import com.aliyun.gts.gmall.manager.front.media.service.VideoCommentService;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoCommentVO;
import com.aliyun.gts.gmall.manager.front.media.web.output.SubShortVideoCommentVO;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import com.aliyun.gts.gmall.platform.operator.api.dto.output.OperatorDTO;
import com.aliyun.gts.gmall.platform.operator.api.facade.OperatorReadFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerExtendDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.aliyun.gts.gmall.center.user.api.dto.constants.SellerConstant.EXTEND_SHOP_TYPE;

/**
 * @description 视频评论管理服务实现类
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/13 15:52
 **/
@Service
@Slf4j
public class VideoCommentServiceImpl implements VideoCommentService {

    @Resource
    private ShortVideoCommentReadFacade readFacade;
    @Resource
    private ShortVideoCommentWriteFacade writeFacade;

    @Autowired
    private CustomerAdapter customerAdapter;
    @Resource
    private OperatorReadFacade operatorReadFacade;
    @Resource
    private SellerReadFacade sellerReadFacade;

    @Resource
    ShortVideoCommentVOConverter converter;


    @Override
    public PageInfo<ShortVideoCommentVO> listByPage(ShortVideoCommentQueryReq query, Long userId) {
        RestResponse<PageInfo<ShortVideoCommentVO>> response = ResponseUtils.convertVOPageResponse(readFacade.page(query),
                converter::dto2VO, false);
        if (Objects.nonNull(response) && Objects.nonNull(response.getData()) && CollectionUtils.isNotEmpty(response.getData().getList())) {
            for (ShortVideoCommentVO item : response.getData().getList()) {
                CustomerDTO customer = customerAdapter.queryById(item.getUserId());
                if (Objects.isNull(customer)) {
                    //如果消费者信息为空，则去商家账号那里查询数据
                    com.aliyun.gts.gmall.platform.operator.api.dto.input.CommonByIdQuery operatorQuery = new com.aliyun.gts.gmall.platform.operator.api.dto.input.CommonByIdQuery(item.getUserId());
                    OperatorDTO operatorDTO = operatorReadFacade.query(operatorQuery).getData();
                    customer = new CustomerDTO();
                    if (Objects.nonNull(operatorDTO)) {
                        BeanUtils.copyProperties(operatorDTO, customer);
                        Long sellerId = operatorDTO.getOutId();
                        SellerDTO sellerDTO = sellerReadFacade.query(com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdQuery.of(sellerId)).getData();
                        List<SellerExtendDTO> sellerExtendDTOList = sellerDTO.getSellerExtends();
                        if (CollectionUtils.isNotEmpty(sellerExtendDTOList)) {
                            Optional<SellerExtendDTO> sellerExtendDTOOptional = sellerExtendDTOList.stream()
                                    .filter(p -> EXTEND_SHOP_TYPE.equalsIgnoreCase(p.getType())).findFirst();
                            if (sellerExtendDTOOptional.isPresent()) {
                                log.info("*********VideoCommentServiceImpl#listByPage SellerExtendDTO = {}", JSONObject.toJSONString(sellerExtendDTOOptional.get()));
                                JSONObject shopJson = (JSONObject) JSON.parse(sellerExtendDTOOptional.get().getV());
                                customer.setHeadUrl(shopJson.getString("logoUrl"));
                            }
                        }
                    }
                }
                item.setCustomerInfo(customer);
                item.setSameUser(Objects.nonNull(userId) && Objects.nonNull(item.getUserId()) && userId.compareTo(item.getUserId()) == 0 ? Boolean.TRUE : Boolean.FALSE);
                //回复列表
                CommonByIdQuery commonByIdQuery = new CommonByIdQuery();
                commonByIdQuery.setId(item.getId());
                List<ShortVideoCommentDTO> subComments = readFacade.listSubComment(commonByIdQuery).getData();
                if (CollectionUtils.isNotEmpty(subComments)) {
                    List<SubShortVideoCommentVO> subShortVideoCommentVOS = new ArrayList<>();
                    for (ShortVideoCommentDTO dto : subComments) {
                        SubShortVideoCommentVO subVO = new SubShortVideoCommentVO();
                        BeanUtils.copyProperties(dto, subVO);
                        item.setSameUser(Objects.nonNull(userId) && Objects.nonNull(subVO.getUserId()) && userId.compareTo(subVO.getUserId()) == 0 ? Boolean.TRUE : Boolean.FALSE);
                        //查询商家操作账号信息
                        com.aliyun.gts.gmall.platform.operator.api.dto.input.CommonByIdQuery operatorQuery = new com.aliyun.gts.gmall.platform.operator.api.dto.input.CommonByIdQuery(subVO.getUserId());
                        OperatorDTO operatorDTO = operatorReadFacade.query(operatorQuery).getData();
                        Long sellerId = operatorDTO.getOutId();
                        SellerDTO sellerDTO = sellerReadFacade.query(com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdQuery.of(sellerId)).getData();
                        List<SellerExtendDTO> sellerExtendDTOList = sellerDTO.getSellerExtends();
                        if (CollectionUtils.isNotEmpty(sellerExtendDTOList)) {
                            Optional<SellerExtendDTO> sellerExtendDTOOptional = sellerExtendDTOList.stream()
                                    .filter(p -> EXTEND_SHOP_TYPE.equalsIgnoreCase(p.getType())).findFirst();
                            if (sellerExtendDTOOptional.isPresent()) {
                                log.info("*********VideoCommentServiceImpl#listByPage sub SellerExtendDTO = {}", JSONObject.toJSONString(sellerExtendDTOOptional.get()));
                                JSONObject shopJson = (JSONObject) JSON.parse(sellerExtendDTOOptional.get().getV());
                                operatorDTO.setHeadUrl(shopJson.getString("logoUrl"));
                            }
                        }
                        subVO.setSellerOperatorInfo(operatorDTO);
                        subShortVideoCommentVOS.add(subVO);
                    }
                    item.setSubComments(subShortVideoCommentVOS);
                }
            }
        }
        return response.getData();
    }

    /**
     * 根据ID删除
     * @param query
     * @return
     */
    @Override
    public Boolean deleteById(CommonByIdQuery query) {
        return   writeFacade.deleteShortVideoComment(query).getData();
    }

    /**
     * 新增评论
     * @param shortVideoCommentDTO
     * @return
     */
    @Override
    public Long addComment(ShortVideoCommentRpcReq shortVideoCommentDTO) {
        return writeFacade.createShortVideoComment(shortVideoCommentDTO).getData();
    }
}
