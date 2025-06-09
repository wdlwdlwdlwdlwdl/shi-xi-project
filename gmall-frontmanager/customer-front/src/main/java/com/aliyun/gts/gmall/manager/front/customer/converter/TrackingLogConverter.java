package com.aliyun.gts.gmall.manager.front.customer.converter;

import com.aliyun.gts.gmall.center.report.api.dto.input.FrontManagerTrackingLogMessage;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddressCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditCustomerInfoCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditHeadUrlCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditPasswordCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.InvoiceCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.TrackingLogCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CustomerInvoiceVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerLevelVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerPromotionVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerVO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CreateCustomerAddressCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CreateCustomerInvoiceCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.UpdateCustomerAddressCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.UpdateCustomerCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.UpdateCustomerInvoiceCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerInvoiceDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerLevelConfigDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerLevelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 *
 *
 * @author tiansong
 */
@Mapper(componentModel = "spring")
public interface TrackingLogConverter {

    /**
     * cmd转message
     *
     * @param trackingLogCommand
     * @return
     */
    FrontManagerTrackingLogMessage command2Message(TrackingLogCommand  trackingLogCommand);


}
