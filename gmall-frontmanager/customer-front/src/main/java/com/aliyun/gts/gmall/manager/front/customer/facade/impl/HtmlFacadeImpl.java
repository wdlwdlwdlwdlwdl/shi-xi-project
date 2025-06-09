package com.aliyun.gts.gmall.manager.front.customer.facade.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.exception.GmallInvalidArgumentException;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.customer.facade.HtmlFacade;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.operator.api.dto.input.SceneCodeQuery;
import com.aliyun.gts.gmall.platform.operator.api.dto.output.SceneDTO;
import com.aliyun.gts.gmall.platform.operator.api.facade.SceneReadFacade;
import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

import static com.aliyun.gts.gmall.manager.front.customer.enums.SellerConstants.HALF_HOUR;
import static com.aliyun.gts.gmall.manager.front.customer.enums.SellerConstants.SCENE_CODE_PREFIX;

@Service
public class HtmlFacadeImpl  implements HtmlFacade {

    /**
     * PC 场景
     */
    @Value("${operator.sceneCode.pc:shop_pc}")
    private String pcSceneCode;

    /**
     * 手机场景
     */
    @Value("${operator.sceneCode.mobile:shop_mobile}")
    private String mobileSceneCode;

    @Autowired
    private SceneReadFacade sceneReadFacade;

    @Resource
    @Qualifier("cacheManager")
    private CacheManager cacheManager;

    /**
     * PC 访问地址参数
     * @param
     * @return
     */
    public Map<String, Object> toPcMap() {
        return toMap(pcSceneCode);
    }

    /**
     * 手机 访问地址参数
     * @param
     * @return
     */
    public Map<String, Object> toMobileMap() {
        return toMap(mobileSceneCode);
    }

    /**
     * 获取配置信息
     * @param sceneCode
     * @return
     */
    private Map<String, Object> toMap(String sceneCode) {
        JSONObject result=null;
        try {
            RpcResponse<SceneDTO>  rpcResponse = sceneReadFacade.queryByCode(new SceneCodeQuery(sceneCode));
            if(!rpcResponse.isSuccess()){
                throw new GmallInvalidArgumentException(I18NMessageUtils.getMessage("call.scene.interface.fail"));  //# "调用获取场景接口失败"
            }
            result = rpcResponse.getData().getConfig();
        } catch (GmallInvalidArgumentException e) {
            throw new GmallInvalidArgumentException(I18NMessageUtils.getMessage("call.scene.interface.fail"));  //# "调用获取场景接口失败"
        }
        Map<String, String> pathMap = Maps.newHashMap();
        JSONArray jsonArray = result.getJSONArray("modules");
        if (jsonArray != null) {
            jsonArray.forEach(x -> {
                JSONObject object = (JSONObject)x;
                if (object != null
                    && StringUtils.isNotBlank(object.getString("name"))
                    && StringUtils.isNotBlank(object.getString("path"))
                    && StringUtils.isNotBlank(object.getString("branch"))) {
                    pathMap.put(object.getString("name") + "_" + object.getString("path").replace("-", "_"), object.getString("branch"));
                }
            });
        }
        result.putAll(pathMap);
        result.put("cdnUi",result.getString("cdnUI"));
        result.put("origin",result.getString("origin"));
        result.put("cdnDomain",result.getString("cdnDomain"));
        result.put("loaderVersion",result.getString("version"));
        result.put("cmsDomain",result.getString("cmsDomain"));
        result.put("restDomain",result.getString("restDomain"));
        result.put("apiDomain",result.getString("apiDomain"));
        result.put("ossDomain",result.getString("ossDomain"));
        return result;
    }
}
