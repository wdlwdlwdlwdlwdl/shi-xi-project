package com.aliyun.gts.gmall.center.trade.core.converter;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.api.dto.enums.InvoiceStatusEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.enums.InvoiceTypeEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.input.OrderInvoiceListRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.OrderInvoiceDTO;
import com.aliyun.gts.gmall.center.trade.core.enums.SaleTypeEnum;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcOrderInvoiceDO;
import com.aliyun.gts.gmall.center.trade.domain.entity.invoice.InvoiceQueryParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface OrderInvoiceConverter {

    InvoiceQueryParam toEntity(OrderInvoiceListRpcReq rpcReq);


    List<OrderInvoiceDTO> toDtoList(List<TcOrderInvoiceDO> list);


    @Mappings({
            @Mapping(target = "saleTypeName", expression = "java(toSaleType(tcOrderInvoiceDO.getSaleType()))"),
            @Mapping(target = "invoiceTypeName", expression = "java(toInvoiceType(tcOrderInvoiceDO.getInvoiceType()))"),
            @Mapping(target = "statusName", expression = "java(toStatus(tcOrderInvoiceDO.getStatus()))"),
            @Mapping(target = "invoiceTitle", expression = "java(toInvoiceInfo(tcOrderInvoiceDO.getInvoiceTitle()))"),
            @Mapping(target = "features", ignore = true),
    })
    OrderInvoiceDTO toDto(TcOrderInvoiceDO tcOrderInvoiceDO);

    default String toStatus(Integer code) {
        InvoiceStatusEnum invoiceStatusEnum = InvoiceStatusEnum.codeOf(code);
        return Objects.nonNull(invoiceStatusEnum) ? invoiceStatusEnum.getName() : "";
    }

    default String toSaleType(Integer code) {
        SaleTypeEnum saleTypeEnum = SaleTypeEnum.buildSaleType(code);
        return Objects.nonNull(saleTypeEnum) ? saleTypeEnum.getName() : "";
    }

    default String toInvoiceType(Integer code) {
        InvoiceTypeEnum invoiceTypeEnum = InvoiceTypeEnum.codeOf(code);
        return Objects.nonNull(invoiceTypeEnum) ? invoiceTypeEnum.getName() : "";
    }

    default OrderInvoiceDTO.CustomerInvoiceDTO toInvoiceInfo(String invoiceInfo) {
        return JSONObject.parseObject(invoiceInfo, OrderInvoiceDTO.CustomerInvoiceDTO.class);
    }
}
