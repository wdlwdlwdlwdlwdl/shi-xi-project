package com.aliyun.gts.gmall.manager.front.customer.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.misc.api.utils.ConvertUtils;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.server.permission.Permission;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.ImUserAdapter;
import com.aliyun.gts.gmall.manager.front.customer.converter.ImNoticeConvert;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CusNoticeQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.ImNoticeVo;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.ImBuildUtils;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.ResponseUtils;
import com.aliyun.gts.gmall.platform.gim.api.dto.output.NoticeDTO;
import com.aliyun.gts.gmall.platform.gim.api.dto.output.UserDTO;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImNoticeFacade;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImUserReadFacade;
import com.aliyun.gts.gmall.platform.gim.common.query.ImUserQuery;
import com.aliyun.gts.gmall.platform.gim.common.query.NoticeQuery;
import com.aliyun.gts.gmall.platform.gim.common.type.ImNoticeStatus;
import com.aliyun.gts.gmall.platform.gim.common.type.ImUserOutType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 */
@RestController
@RequestMapping(value = "/imNotice/")
@Slf4j
public class ImNoticeController {
    @Resource
    private ImUserAdapter imUserAdapter;

    @Resource
    private ImNoticeFacade imNoticeFacade;
    @Resource
    private ImNoticeConvert convert;
    @Resource
    private ImUserReadFacade imUserReadFacade;

    @RequestMapping(value = "countUnReadByType")
    @Permission(required = false)
    public RestResponse<Long> countUnReadByType(@RequestBody @Validated CusNoticeQuery cusNoticeQuery) {
        try {
            NoticeQuery query = convert.convert(cusNoticeQuery);
            ParamUtil.nonNull(query.getType(), "type"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
            Long uid = imUserAdapter.getCustomerUid(UserHolder.getUser().getCustId());
            query.setReceiveId(uid);
            query.setStatus(ImNoticeStatus.unRead.getValue());
            RpcResponse<Map<Integer, Long>> response = imNoticeFacade.countGroupByType(query);
            if (response.getData() == null) {
                return RestResponse.okWithoutMsg(0L);
            }
            return RestResponse.okWithoutMsg(response.getData().get(query.getType()));
        } catch (Exception e) {
            return RestResponse.fail("101", I18NMessageUtils.getMessage("exception")+":" + e.getMessage());  //# "异常
        }
    }

    @RequestMapping(value = "countUnReadBySend")
    @Permission(required = false)
    public RestResponse<Map<Long, Long>> countUnReadBySend(@RequestBody @Validated CusNoticeQuery cusNoticeQuery) {
        try {
            NoticeQuery query = convert.convert(cusNoticeQuery);
            ParamUtil.nonNull(query.getSendIds(), "sendIds"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
            Long uid = imUserAdapter.getCustomerUid(UserHolder.getUser().getCustId());
            query.setReceiveId(uid);
            query.setStatus(ImNoticeStatus.unRead.getValue());
            RpcResponse<Map<Long, Long>> response = imNoticeFacade.countGroupBySend(query);
            if (response.getData() == null) {
                return RestResponse.okWithoutMsg(response.getData());
            }
            Long sum = 0L;
            for (Long value : response.getData().values()) {
                sum = sum + value;
            }
            response.getData().put(0L, sum);
            return RestResponse.okWithoutMsg(response.getData());
        } catch (Exception e) {
            return RestResponse.fail("101", I18NMessageUtils.getMessage("exception")+":" + e.getMessage());  //# "异常
        }
    }

    @RequestMapping(value = "tab")
    @Permission(required = false)
    public RestResponse<List<JSONObject>> tab(@RequestBody LoginRestQuery query) {
        try {
            ImUserQuery userQuery = new ImUserQuery();
            userQuery.setPage(new PageParam());
            userQuery.setOutType(ImUserOutType.system.getType());
            RpcResponse<PageInfo<UserDTO>> response = imUserReadFacade.page(userQuery);
            Map<Long, Long> count = new HashMap<>();
            List<Long> ids = null;
            if (response.getData() != null && response.getData().getList() != null) {
                ids = response.getData().getList().stream().map(v -> v.getId()).collect(Collectors.toList());
                NoticeQuery noticeQuery = new NoticeQuery();
                noticeQuery.setSendIds(ids);
                count = countUnReadGroupBySend(noticeQuery);
            }
            List<JSONObject> result = ImBuildUtils.buildNoticeTab(response.getData().getList(), count);
            return RestResponse.okWithoutMsg(result);
        } catch (Exception e) {
            return RestResponse.fail("101", I18NMessageUtils.getMessage("exception")+":" + e.getMessage());  //# "异常
        }
    }

    @RequestMapping(value = "page")
    @Permission(required = false)
    public RestResponse<PageInfo<ImNoticeVo>> page(@RequestBody @Validated CusNoticeQuery cusNoticeQuery) {
        try {
            NoticeQuery query = convert.convert(cusNoticeQuery);
            Long uid = imUserAdapter.getCustomerUid(UserHolder.getUser().getCustId());
            query.setReceiveId(uid);
            RpcResponse<PageInfo<NoticeDTO>> response = imNoticeFacade.page(query);
            PageInfo<ImNoticeVo> result = ConvertUtils.convertPage(response.getData(), convert::dto2vo);
            fillUserInfo(result.getList());
            return RestResponse.okWithoutMsg(result);
        } catch (Exception e) {
            return RestResponse.fail("101", I18NMessageUtils.getMessage("exception")+":" + e.getMessage());  //# "异常
        }
    }

    @RequestMapping(value = "markRead")
    @Permission(required = false)
    public RestResponse<Boolean> markRead(@RequestBody CusNoticeQuery cusNoticeQuery) {
        try {
            NoticeQuery query = convert.convert(cusNoticeQuery);
            ParamUtil.expectTrue(query.getId() != null || query.getIds() != null,"id "+I18NMessageUtils.getMessage("or")+"ids "+I18NMessageUtils.getMessage("not.empty") );  //# 或者ids 不为空"
            if(query.getId() != null){
                List<Long> ids = new ArrayList<>();
                ids.add(query.getId());
                query.setIds(ids);
            }
            Long uid = imUserAdapter.getCustomerUid(UserHolder.getUser().getCustId());
            query.setReceiveId(uid);
            query.setStatus(ImNoticeStatus.read.getValue());
            RpcResponse<Boolean> response = imNoticeFacade.batchUpdateStatus(query);
            return ResponseUtils.operateResult(response);
        } catch (Exception e) {
            return RestResponse.fail("101", I18NMessageUtils.getMessage("exception")+":" + e.getMessage());  //# "异常
        }
    }

    protected void fillUserInfo(List<ImNoticeVo> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        Set<Long> ids = vos.stream().map(notcie -> notcie.getSendId()).collect(Collectors.toSet());
        Map<Long, UserDTO> map = imUserAdapter.queryUserByIds(ids);
        for (ImNoticeVo noticeVo : vos) {
            UserDTO userDTO = map.get(noticeVo.getSendId());
            if (userDTO != null) {
                noticeVo.setSenderName(userDTO.getNickname());
            }
        }
    }

    private Map<Long, Long> countUnReadGroupBySend(NoticeQuery query) {
        //用户ID
        Long uid = imUserAdapter.getCustomerUid(UserHolder.getUser().getCustId());
        query.setReceiveId(uid);
        query.setStatus(ImNoticeStatus.unRead.getValue());
        RpcResponse<Map<Long, Long>> response = imNoticeFacade.countGroupBySend(query);
        if (response.getData() == null) {
            return new HashMap<>();
        }
        return response.getData();
    }
}
