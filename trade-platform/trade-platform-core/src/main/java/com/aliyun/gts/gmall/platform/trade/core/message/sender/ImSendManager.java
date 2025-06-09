package com.aliyun.gts.gmall.platform.trade.core.message.sender;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.gim.api.dto.input.ImCommonMessageRequest;
import com.aliyun.gts.gmall.platform.gim.api.dto.output.TemplateDTO;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImManualFacade;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImTemplateFacade;
import com.aliyun.gts.gmall.platform.gim.common.query.TemplateQuery;
import com.aliyun.gts.gmall.platform.gim.common.utils.LocalCache;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author xinchen
 * 消息发送
 */
@Slf4j
@Component
public class ImSendManager {

    @Autowired
    private ImManualFacade imManualFacade;
    @Resource
    private ImTemplateFacade imTemplateFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private I18NConfig i18NConfig;

    private LocalCache<TemplateDTO> cache = new LocalCache("tmp");

    /**
     * 发送IM消息
     * @param imMsg    数据
     * @return
     */
    public RpcResponse<Boolean> sendMessage(ImCommonMessageRequest imMsg) {
        // 设置语言
        if (StringUtils.isEmpty(imMsg.getLang())) {
            imMsg.setLang(i18NConfig.getDefaultLang());
        }
        LocaleContextHolder.setLocale(new Locale(imMsg.getLang()));
        this.check(imMsg);
        log.info("IM发送消息：{}", imMsg.toString());
        RpcResponse<Boolean> rpc = imManualFacade.sendMessage(imMsg);
        log.info("IM发送返回消息：{}",rpc.toString());
        return rpc;
    }

    /**
     * 初始化用户和卖家的参数
     * @param id
     * @param msg
     * @param isCust
     * 2025-3-17 11:19:49
     */
    public void initParam(Long id, ImCommonMessageRequest msg, Boolean isCust) {
        if(isCust){
            Customer customer = userRepository.getCustomerRequired(id);
            Map<String, String> receiver = new HashMap<>();
            receiver.put("iin", customer.getIin());
            receiver.put("phone", customer.getPhone());
            msg.setLang(customer.getLanguage());
            msg.setReceiver(JSON.toJSONString(receiver));
        } else {
            Seller seller = userRepository.getSeller(id);
            msg.setReceiver(seller.getEmail());
            msg.setSellerId(seller.getSellerId());
            //再查一下语言
            String lang = userRepository.queryMainOperatorLang(id);
            // 没设置使用默认语言
            msg.setLang(StringUtils.isEmpty(lang) ? i18NConfig.getDefaultLang() : lang);
        }
    }

    private void check(ImCommonMessageRequest imMsg) {
        ParamUtil.nonNull(imMsg.getCode(), "templateCode " + I18NMessageUtils.getMessage("cannot.be.empty", new Object[0]));
        ParamUtil.nonNull(imMsg.getReceiver(), "receiver " + I18NMessageUtils.getMessage("not.empty", new Object[0]));
        ParamUtil.nonNull(imMsg.getReplacements(), "replacements " + I18NMessageUtils.getMessage("not.empty", new Object[0]));
        ParamUtil.nonNull(imMsg.getTemplateType(), "templateType " + I18NMessageUtils.getMessage("not.empty", new Object[0]));
    }

    public TemplateDTO queryTemplateByCode(String code, Boolean withCache) {
        if (withCache) {
            TemplateDTO templateDTO = (TemplateDTO)this.cache.get(code);
            if (templateDTO != null) {
                return templateDTO;
            }
        }
        TemplateQuery query = new TemplateQuery();
        query.setCode(code);
        RpcResponse<TemplateDTO> response = this.imTemplateFacade.queryByCode(query);
        if (response.getData() != null) {
            this.cache.put(code, (TemplateDTO)response.getData());
        }
        return (TemplateDTO)response.getData();
    }
}
