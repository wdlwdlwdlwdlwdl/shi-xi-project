package com.aliyun.gts.gmall.manager.front.login.controller;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.GetShortUrlByItemIdReq;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.GetShortUrlReq;
import com.aliyun.gts.gmall.manager.front.login.dto.output.ShortUrlInfo;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import com.aliyun.gts.gmall.manager.utils.ShortUrlUtils;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * 短链相关api
 *
 * @author liguotai
 * @date 2022/11/15
 */

@Controller
@Api(value = "短链相关api", tags = {"shortUrl"})
@Slf4j
public class ShortUrlController {
    @Autowired
    @Qualifier("cacheManager")
    private CacheManager cacheManager;

    private static final String SHORT_URL_PREFIX = "SHORT_URL_";

    private static final String SHORT_URL_ITEM_PREFIX = "SHORT_URL_ITEM";

    @NacosValue(value = "${front-manager.short.uri:http://47.96.102.195/dl/}", autoRefreshed = true)
    private String shortUri;

    @NacosValue(value = "${front-manager.item.short.uri:http://47.96.102.195/dl/item/}", autoRefreshed = true)
    private String itemShortUri;

    @NacosValue(value = "${front-manager.item.short.host.list:myth-dev2.hanghangohye.com}", autoRefreshed = true)
    private String hostList;

    @ApiOperation(value = "根据长链接获取短链接")
    @PostMapping(name = "getShortUrl", value = "/api/getShortUrl")
    public @ResponseBody RestResponse<String> getShortUrl(@RequestBody GetShortUrlReq getShortUrlReq) {
        ParamUtil.expectTrue(isTmallgenie(getShortUrlReq.getUrl()),
                I18NMessageUtils.getMessage("shortlink.source") + "host" + I18NMessageUtils.getMessage("exception") + "！"); // # "短链原链接host异常
        try {
            String shortUrl = ShortUrlUtils.shortUrl(getShortUrlReq.getUrl(), UserHolder.getUser().getCustPrimary());
            String cacheKey = getCacheKey(shortUrl);
            ShortUrlInfo shortUrlInfo = new ShortUrlInfo();
            shortUrlInfo.setOriginalUrl(getShortUrlReq.getUrl());
            shortUrlInfo.setShortUrl(shortUrl);
            cacheManager.set(cacheKey, shortUrlInfo);
            return RestResponse.okWithoutMsg(shortUri + shortUrl);
        } catch (Exception e) {
            log.error("生成短链，保存缓存异常！", e);
            return RestResponse.fail(LoginFrontResponseCode.SHORT_URL_ERROR);
        }
    }

    @ApiOperation(value = "根据长链接及商品ID获取短链接")
    @PostMapping(name = "getItemShortUrl", value = "/api/item/getShortUrl")
    public @ResponseBody RestResponse<String> getShortUrlByItemId(@RequestBody GetShortUrlByItemIdReq getShortUrlReq) {
        ParamUtil.expectTrue(isTmallgenie(getShortUrlReq.getUrl()),
                I18NMessageUtils.getMessage("shortlink.source") + "host" + I18NMessageUtils.getMessage("exception") + "！"); // # "短链原链接host异常
        try {
            String shortUrl = ShortUrlUtils.shortUrl(getShortUrlReq.getUrl(), getShortUrlReq.getItemId());
            String cacheKey = StringUtils.join(SHORT_URL_ITEM_PREFIX, shortUrl);
            ShortUrlInfo shortUrlInfo = new ShortUrlInfo();
            shortUrlInfo.setOriginalUrl(getShortUrlReq.getUrl());
            shortUrlInfo.setShortUrl(shortUrl);
            cacheManager.set(cacheKey, shortUrlInfo);
            return RestResponse.okWithoutMsg(itemShortUri + shortUrl);
        } catch (Exception e) {
            log.error("生成短链，保存缓存异常！", e);
            return RestResponse.fail(LoginFrontResponseCode.SHORT_URL_ERROR);
        }
    }

    private static String getCacheKey(String shortUrl) {
        return StringUtils.join(SHORT_URL_PREFIX, shortUrl);
    }

    @GetMapping("/dl/{shortUrl}")
    public void jumpLongLink(HttpServletResponse response, @PathVariable("shortUrl") String shortUrl) {
        try {
            String cacheKey = getCacheKey(shortUrl);
            ShortUrlInfo shortUrlInfo = cacheManager.get(cacheKey);
            if (shortUrlInfo != null) {
                response.sendRedirect(shortUrlInfo.getOriginalUrl());
            }
        } catch (Exception e) {
            log.error("获取长链接异常！", e);
        }
    }

    @GetMapping("/dl/item/{shortUrl}")
    public void jumpLongLinkByItemId(HttpServletResponse response, @PathVariable("shortUrl") String shortUrl) {
        try {
            String cacheKey = StringUtils.join(SHORT_URL_ITEM_PREFIX, shortUrl);
            ShortUrlInfo shortUrlInfo = cacheManager.get(cacheKey);
            if (shortUrlInfo != null) {
                response.sendRedirect(shortUrlInfo.getOriginalUrl());
            }
        } catch (Exception e) {
            log.error("获取长链接异常！", e);
        }
    }

    private Boolean isTmallgenie(String url) {
        try {
            URL url1 = new URL(url);
            String host = url1.getHost();
            if (StringUtils.isNotBlank(host) && StringUtils.isNotBlank(hostList)) {
                List<String> hosts = StrUtil.split(hostList, StrUtil.COMMA);
                return hosts.contains(host);
            }
        } catch (Exception e) {
            log.warn("生成短链原链接转化URL异常；url:{}", url, e);
        }
        return false;
    }

    public static void main(String[] args) throws MalformedURLException {
        String url = "https://fd.tmallgenie.com/pages/home.html#/home?a=1&b=2";
        URL url1 = new URL(url);
        System.out.println(url1.getHost());
    }
}
