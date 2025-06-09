package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;

import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author FaberWong
 * @Date 2024/8/29 11:22
 */
@Data
public class TrackingLogCommand extends AbstractCommandRestRequest {

     /**
      * scm埋点
      */
     private String scm;

     /**
      * 推广id
      */
     private String traceId;

     /**
      * 订单id
      */
     private String orderId;

     /**
      * 用户id
      */
     private String userId;

     /**
      * 客户端ip
      */
     private String clientIp;

     /**
      * spm埋点
      */
     private String spm;

     /**
      * 站点id
      */
     private String site;

     /**
      * 实例id
      */
     private String instance;

     /**
      * 事件
      */
     private String event;

     /**
      * 用户是否登录
      */
     private String login;

     /**
      * 商品or内容id
      */
     private String itemId;

     /**
      * page路径
      */
     private String page;

     /**
      * 这个小程序用的，没有用
      */
     private String tid;

     /**
      * 前端没搜到，应该是没有
      */
     private String tradeNo;

     /**
      * 前端渠道
      */
     private String env;

     private Date gmtCreate;

     
}
