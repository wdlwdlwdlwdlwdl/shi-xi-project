package com.aliyun.gts.gmall.manager.front.b2bcomm.consumer.service.impl;

import com.aliyun.gts.gmall.center.misc.api.dto.output.flow.message.Tag;
import com.aliyun.gts.gmall.center.misc.api.dto.output.flow.message.WorkflowInvovedMessage;
import com.aliyun.gts.gmall.manager.front.b2bcomm.consumer.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class AuditConfirmServiceImpl implements ConsumerService {

    @Override
    public void consume(WorkflowInvovedMessage message) throws Exception {
    }

    //
//    @Autowired
//    private SupplierReadFacade supplierReadFacade;
//    @Autowired
//    private SupplierWriteFacade supplierWriteFacade;
//
//    @Autowired
//    @Qualifier("internalGmallOssClient")
//    private GmallOssClient ossClient;
//
//    @Value("${oss.bucket.supplierDraft}")
//    private String supplierDraftBucket;
//
//    @Override
//    public void consume(WorkflowInvovedMessage message) {
//        Long supplierId = Long.parseLong(message.getBusinessId().split(WorkFlowUtils.DELIMITER)[1]);
//        CommonQueryRequest<Long> req = new CommonQueryRequest<>();
//        req.setData(supplierId);
//        RpcResponse<SupplierInfoDTO> res = supplierReadFacade.querySupplierById(req);
//        if (!res.isSuccess()) {
//            log.error("supplierId:{}审核通过确认失败", supplierId);
//            throw new GmallException(AdminResponseCode.QUERY_SUPPLIER_ERROR, supplierId);
//        }
//        int auditStatus = res.getData().getAuditStatus();
//        if (AuditStatusEnum.PASS_AUDIT.isThatStatus(auditStatus) || AuditStatusEnum.NOT_PASS_AUDIT.isThatStatus(auditStatus)) {
//            log.warn("supplierId:{}已审核完成", supplierId);
//            return;
//        }
//
//        String draftStr = ossClient.getContent(supplierDraftBucket, "supplierDraft/" + message.getBusinessId());
//        SupplierSettleInConfirmDTO data = new Gson().fromJson(draftStr, SupplierSettleInConfirmDTO.class);
//        //设置状态和审核状态
//        int toSetAudit = message.getApproved() ? AuditStatusEnum.PASS_AUDIT.getCode() : AuditStatusEnum.NOT_PASS_AUDIT.getCode();
//        SupplierInfoDTO supplierInfoDTO = data.getSupplierInfo();
//        supplierInfoDTO.setStatus(1);
//        supplierInfoDTO.setAuditStatus(toSetAudit);
//        CompanyDTO companyDTO = data.getCompanyInfo();
//        companyDTO.setStatus(1);
//        companyDTO.setAuditStatus(toSetAudit);
//        List<SupplierCertificateDTO> certificateDTOS = data.getCertificates();
//        if (!CollectionUtils.isEmpty(certificateDTOS)) {
//            certificateDTOS.forEach(certificate -> {
//                certificate.setAuditStatus(toSetAudit);
//            });
//        }
//        data.setPass(message.getApproved());
//        if(message.getApproved()) {
//            JSONObject rawInfoJ = JSONObject.parseObject(message.getRawInfo());
//            if(rawInfoJ.containsKey("fieldData")){
//                JSONObject fieldDataJ = rawInfoJ.getJSONObject("fieldData");
//                if(fieldDataJ.containsKey("level")) {
//                    data.setGrade(fieldDataJ.getString("level"));
//                }
//            }
//        } else {
//            data.setInfo(message.getReason());
//        }
//        RpcResponse<Boolean> r = supplierWriteFacade.confirmAudit(data);
//        if(!r.isSuccess()) {
//            throw new GmallException(AdminResponseCode.CONFIRM_AUDIT_FAILED);
//        }
//    }
//
    @Override
    public String getTag() {
        return Tag.SUPPLIER_ENTER_APPLICATION;
    }
}
