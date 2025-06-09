/*
package com.aliyun.gts.gmall.manager.front;

import com.aliyun.gts.gmall.framework.config.I18NMvcConfig;
import com.aliyun.gts.gmall.framework.server.interceptor.HeaderResolveInterceptor;
import com.aliyun.gts.gmall.manager.front.common.config.DegradationConfig;
import com.aliyun.gts.gmall.manager.front.common.config.GmallFrontConfig;
import com.aliyun.gts.gmall.manager.front.config.GmallMappingJackson2HttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

*/
/**
 * Swagger config
 *
 * @author tiansong
 *//*

@Configuration
//@EnableSwagger2
@ComponentScan(basePackages = {"com.aliyun.gts.gmall.manager.front"})
public class SwaggerConfig extends I18NMvcConfig {
    @Autowired
    private GmallFrontConfig gmallFrontConfig;

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//            .select()
//            .apis(RequestHandlerSelectors.any())
//            .paths(PathSelectors.any())
//            .build();
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HeaderResolveInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(gmallFrontConfig.getAddAllowedOrigin())
            .allowCredentials(true)
            .allowedMethods("GET", "POST", "DELETE", "PUT")
            .maxAge(3600);
    }

    private CorsConfiguration corsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        */
/* 请求常用的三种配置，*代表允许所有，当时你也可以自定义属性（比如header只能带什么，只能是post方式等等）
         *//*

        if (gmallFrontConfig.getAddAllowedOrigin() != null && gmallFrontConfig.getAddAllowedOrigin().length > 0) {
            for (String origin : gmallFrontConfig.getAddAllowedOrigin()) {
                corsConfiguration.addAllowedOrigin(origin);
            }
        }
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig());
        return new CorsFilter(source);
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        converters.removeIf(x -> x instanceof MappingJackson2HttpMessageConverter);
        converters.add(new GmallMappingJackson2HttpMessageConverter());
    }
}
*/
