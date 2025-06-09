package com.aliyun.gts.gmall.manager.front.media.config;

import com.aliyun.gts.gmall.center.media.api.facade.*;
import com.aliyun.gts.gmall.center.user.api.facade.CustShopInterestRelReadFacade;
import com.aliyun.gts.gmall.center.user.api.facade.CustShopInterestRelWriteFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MediaApiConfiguration {

    @Value("${dubbo.directUrl.media:}")
    private String directUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoCategoryReadFacade getShortVideoCategoryReadFacade() {
        return serviceSubscriber.consumer(ShortVideoCategoryReadFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoCategoryWriteFacade getShortVideoCategoryWriteFacade(){
        return serviceSubscriber.consumer(ShortVideoCategoryWriteFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoInfoReadFacade getShortVideoInfoReadFacade(){
        return serviceSubscriber.consumer(ShortVideoInfoReadFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoInfoWriteFacade getShortVideoInfoWriteFacade(){
        return serviceSubscriber.consumer(ShortVideoInfoWriteFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoLabelReadFacade getShortVideoLabelReadFacade() {
        return serviceSubscriber.consumer(ShortVideoLabelReadFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoLabelWriteFacade getShortVideoLabelWriteFacade(){
        return serviceSubscriber.consumer(ShortVideoLabelWriteFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoLabelRelationsReadFacade getShortVideoLabelRelationsReadFacade() {
        return serviceSubscriber.consumer(ShortVideoLabelRelationsReadFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoLikesReadFacade getShortVideoLikesReadFacade() {
        return serviceSubscriber.consumer(ShortVideoLikesReadFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoCommentReadFacade getShortVideoCommentReadFacade() {
        return serviceSubscriber.consumer(ShortVideoCommentReadFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoCommentWriteFacade getShortVideoCommentWriteFacade() {
        return serviceSubscriber.consumer(ShortVideoCommentWriteFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public VodFacade getVodFacade() {
        return serviceSubscriber.consumer(VodFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortVideoLikesWriteFacade getShortVideoLikesWriteFacade() {
        return serviceSubscriber.consumer(ShortVideoLikesWriteFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public CustShopInterestRelWriteFacade getCustShopInterestRelWriteFacade() {
        return serviceSubscriber.consumer(CustShopInterestRelWriteFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public CustShopInterestRelReadFacade getCustShopInterestRelReadFacade() {
        return serviceSubscriber.consumer(CustShopInterestRelReadFacade.class).directUrl(directUrl).subscribe();
    }
}
