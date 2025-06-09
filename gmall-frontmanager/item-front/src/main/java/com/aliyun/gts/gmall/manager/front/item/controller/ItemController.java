package com.aliyun.gts.gmall.manager.front.item.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.ItemPageQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.ItemSkuIndexPageQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemPageVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemSkuIndexPageVO;
import com.aliyun.gts.gmall.manager.front.item.facade.ItemFacade;

/**
 * 
 * @Title: ItemController.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2024年11月12日 09:08:46
 * @version V1.0
 */
@RestController
public class ItemController {
    @Autowired
    private ItemFacade itemFacade;

    @PostMapping("/api/item/page")
    public RestResponse<List<ItemPageVO>> page(@RequestBody ItemPageQuery query) {
        return RestResponse.okWithoutMsg(itemFacade.queryPage(query));
    }

    @PostMapping("/api/item/sku/index/page")
    public RestResponse<List<ItemSkuIndexPageVO>> skuIndex(@RequestBody ItemSkuIndexPageQuery query) {
        List<ItemSkuIndexPageVO> list = new ArrayList<>();
        list.add(mock());
        return RestResponse.okWithoutMsg(list);
    }

    private ItemSkuIndexPageVO mock() {
        ItemSkuIndexPageVO vo = new ItemSkuIndexPageVO();
        vo.setItemId(9999L);
        vo.setSkuId(9999L);
        vo.setItemTitle("mock的商品");
        vo.setItemType(1);
        vo.setItemTypeName("实物商品");
        vo.setCategoryId(9999L);
        vo.setCategoryName("mock的商品类目");
        vo.setBasePrice(9999L);
        List<String> pics = new ArrayList<>();
        pics.add("http://img30.360buyimg.com/popWareDetail/jfs/t1/206730/1/8381/115752/6184e408E8b07b0ee/eb0413d5587a8d3e.png");
        pics.add("http://img30.360buyimg.com/popWareDetail/jfs/t1/204073/28/14204/96114/6184e408Eb13cf5b9/d6a6b056dd4ed057.png");
        pics.add("http://img30.360buyimg.com/popWareDetail/jfs/t1/144609/7/22148/154173/6184e408E367f32c6/5c5747814a4f91df.png");
        vo.setPictureList(pics);
        vo.setSkuQuoteId(9999L);
        vo.setSellerId(9999L);
        vo.setStoreName("mock的店铺名称");
        vo.setItemTotalCount(1L);
        return vo;
    }

}
