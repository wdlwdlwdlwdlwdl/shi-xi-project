package com.aliyun.gts.gmall.manager.front.media.web.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoAuditRpcReq;
import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoCommentRpcReq;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoInfoReadFacade;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoCommentQueryReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;
import com.aliyun.gts.gmall.manager.front.media.service.VideoCommentService;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoCommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @description 短视频评论管理
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/17 16:46
 **/
@Slf4j
@RestController
@RequestMapping(value = "/media/comment")
@Api(value = "短视频评论操作", tags = {"短视频评论操作"})
public class VideoCommentController extends BaseRest {

    @Resource
    private VideoCommentService commentService;
    @Resource
    private ShortVideoInfoReadFacade shortVideoInfoReadFacade;

    @ApiOperation("分页查询短视频评论")
    @PostMapping(value = "/page")
    public RestResponse<PageInfo<ShortVideoCommentVO>> listByPage(@RequestBody ShortVideoCommentQueryReq query) {
        OperatorDO operator = getLogin();
        if (Objects.isNull(operator)) {
            return RestResponse.ok(null);
        }
        PageInfo<ShortVideoCommentVO> result = commentService.listByPage(query, operator.getOperatorId());
        return RestResponse.okWithoutMsg(result);
    }

    @ApiOperation("根据ID删除短视频评论")
    @PostMapping(value = "/deleteById")
    public RestResponse<Boolean> deleteById(@RequestBody CommonByIdQuery query) {
        Boolean result = commentService.deleteById(query);
        return RestResponse.okWithoutMsg(result);
    }

    @ApiOperation("新增短视频评论")
    @PostMapping(value = "/add")
    public RestResponse<Long> addComment(@RequestBody ShortVideoCommentRpcReq shortVideoCommentDTO) {
        OperatorDO operator = getLogin();
        if (Objects.isNull(operator)) {
            return RestResponse.ok(null);
        }
        //评论内容检测
        ShortVideoAuditRpcReq scanRequest = new ShortVideoAuditRpcReq();
        scanRequest.setContent(shortVideoCommentDTO.getCommentContent());
        RpcResponse<Boolean> scanResponse = shortVideoInfoReadFacade.textScan(scanRequest);
        log.info("*******VideoCommentController#addComment 内容检测结果： scanResponse = {}", JSONObject.toJSONString(scanResponse));
        if (Objects.isNull(scanResponse) || !scanResponse.isSuccess()) {
            return RestResponse.fail("100001", I18NMessageUtils.getMessage("content.safety.exception"));  //# "内容安全检测服务调用异常"
        }
        if (!scanResponse.getData()) {
            return RestResponse.fail("100002", I18NMessageUtils.getMessage("comment.content.fail")+"，"+I18NMessageUtils.getMessage("illegal.char"));  //# "评论内容检测不通过，含有非法字符"
        }

        shortVideoCommentDTO.setUserId(operator.getOperatorId());
        shortVideoCommentDTO.setUserName(operator.getNickname());
        shortVideoCommentDTO.setCreateId(operator.getOperatorId().toString());
        shortVideoCommentDTO.setUpdateId(operator.getOperatorId().toString());
        return RestResponse.okWithoutMsg(commentService.addComment(shortVideoCommentDTO));
    }
}
