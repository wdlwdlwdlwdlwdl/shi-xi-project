package com.aliyun.gts.gmall.manager.front.customer.converter;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CustomerInvoiceVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.*;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CreateCustomerAddressCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CreateCustomerInvoiceCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.UpdateCustomerAddressCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.UpdateCustomerCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.UpdateCustomerInvoiceCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.output.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 买家信息的转换
 *
 * @author tiansong
 */
@Mapper(componentModel = "spring")
public interface CustomerConverter {

    /**
     * 密码更新
     *
     * @param editPasswordCommand
     * @return
     */
    @Mapping(source = "custId", target = "id")
    @Mapping(source = "newPwd", target = "pwd")
    UpdateCustomerCommand convertPwd(EditPasswordCommand editPasswordCommand);

    /**
     * 买家头像更新
     *
     * @param editHeadUrlCommand
     * @return
     */
    @Mapping(source = "custId", target = "id")
    UpdateCustomerCommand convertHeadUrl(EditHeadUrlCommand editHeadUrlCommand);

    /**
     * 买家基础信息更新
     *
     * @param editCustomerInfoCommand
     * @return
     */
    @Mapping(source = "custId", target = "id")
    UpdateCustomerCommand convertBaseInfo(EditCustomerInfoCommand editCustomerInfoCommand);

    /**
     * 买家基础信息更新
     *
     * @param editCustomerLanguageCommand
     * @return
     */
    @Mapping(source = "custId", target = "id")
    UpdateCustomerCommand convertLanguageInfo(EditCustomerLanguageCommand editCustomerLanguageCommand);

    /**
     * 买家信息转换
     *
     * @param customerDTO      基础信息
     * @param customerLevelDTO 等级
     * @return
     */
    @Mapping(source = "customerLevelDTO", target = "customerLevelVO")
    @Mapping(source = "customerDTO.id", target = "custId")
    CustomerVO convertCustomer(CustomerDTO customerDTO, CustomerLevelDTO customerLevelDTO);

    /**
     * 用户等级列表转换
     *
     * @param customerLevelConfigDTOList
     * @return
     */
    List<CustomerLevelVO> convertLevelList(List<CustomerLevelConfigDTO> customerLevelConfigDTOList);

    @Mapping(source = "awards.coupons", target = "coupons")
    CustomerLevelVO convertLevel(CustomerLevelConfigDTO customerLevelConfigDTO);

    // ------------------------------ address -------------------------------

    /**
     * 地址信息转换
     *
     * @param addressCommand 地址信息
     * @return RPC请求
     */
    CreateCustomerAddressCommand convertCreate(AddressCommand addressCommand);

    /**
     * 地址信息转换
     *
     * @param addressCommand 地址信息
     * @return RPC请求
     */
    UpdateCustomerAddressCommand convertUpdate(AddressCommand addressCommand);

    // ------------------------------ invoice -------------------------------

    /**
     * 发票列表展示
     *
     * @param customerInvoiceDTOList
     * @return
     */
    List<CustomerInvoiceVO> convertInvoiceList(List<CustomerInvoiceDTO> customerInvoiceDTOList);

    /**
     * 发票信息展示转换
     *
     * @param customerInvoiceDTO
     * @return
     */
    @Mapping(source = "dutyParagraph", target = "taxNo")
    @Mapping(source = "depositBank", target = "bankName")
    CustomerInvoiceVO convertInvoice(CustomerInvoiceDTO customerInvoiceDTO);

    /**
     * 创建发票转换
     *
     * @param invoiceCommand
     * @return
     */
    @Mapping(source = "taxNo", target = "dutyParagraph")
    @Mapping(source = "bankName", target = "depositBank")
    CreateCustomerInvoiceCommand convertCreateInvoice(InvoiceCommand invoiceCommand);

    /**
     * 更新发票转换
     *
     * @param invoiceCommand
     * @return
     */
    @Mapping(source = "taxNo", target = "dutyParagraph")
    @Mapping(source = "bankName", target = "depositBank")
    UpdateCustomerInvoiceCommand convertUpdateInvoice(InvoiceCommand invoiceCommand);

    /**
     * 等级权益（优惠券）转换
     *
     * @param couponInstanceDTOList
     * @return
     */
    List<CustomerPromotionVO> convertLevelPromotion(List<CouponInstanceDTO> couponInstanceDTOList);


    CustomerGrowthRecordVO  convertCustomerGrowth(CustomerGrowthDTO customerGrowthDTO);



    List<CustomerGrowthVO> convertCustomerGrowths(List<CustomerGrowthSumDTO> customerGrowthSumDTOList);


}
