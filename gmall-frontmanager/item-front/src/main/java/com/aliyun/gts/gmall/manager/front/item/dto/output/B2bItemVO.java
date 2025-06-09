package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.Collections;
import java.util.List;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.testng.collections.Lists;

/**
 * b2b商品详情信息
 *
 * @author linyi
 */
@Data
@ApiModel("b2b商品信息")
public class B2bItemVO extends AbstractOutputInfo {

    @ApiModelProperty("类型,1:阶梯价,2:规格")
    private Integer type;

    @ApiModelProperty("计量单位")
    private String unit;

    @ApiModelProperty("最小购买数量")
    private Integer minBuyNum;

    @ApiModelProperty("阶梯价列表")
    List<StepPrice> priceList;

    @ApiModelProperty("获取阶梯价")
    public Long getPrice(Long buyNum) {

        if (CollectionUtils.isEmpty(priceList)) {
            return null;
        }

        //倒序遍历
        for (int i = priceList.size() - 1; i >= 0; i--) {
            StepPrice x = priceList.get(i);
            if (buyNum >= x.getNum()) {
                return Long.valueOf(x.getPrice());
            }
        }

        return null;
    }

    @ApiModelProperty("阶梯价展现")
    public List<ImmutablePair<String, String>> getDisplayPrice() {

        if (CollectionUtils.isEmpty(priceList)) {
            return null;
        }

        int last = 0;

        List<ImmutablePair<String, String>> result = Lists.newArrayList();

        //倒序遍历
        for (int i = priceList.size() - 1; i >= 0; i--) {

            StepPrice x = priceList.get(i);

            //最后一个
            if (i == priceList.size() - 1 && priceList.size() != 1) {
                result.add(new ImmutablePair<>(String.format(">=%s%s", x.getNum(), unit), x.getPrice()));
                last = x.getNum();
            } else if (i != 0) {
                result.add(new ImmutablePair<>(String.format("%s-%s%s", x.getNum(), last, unit), x.getPrice()));
                last = x.getNum();
            } else {
                //第一个
                result.add(new ImmutablePair<>(String.format(">=%s%s", x.getNum(), unit), x.getPrice()));
            }
        }

        Collections.reverse(result);

        return result;
    }

}