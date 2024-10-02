package com.morningstar.config;

import com.morningstar.properties.SiteProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI springOpenAPI(SiteProperties siteProperties) {
        Contact contact = new Contact();
        contact.setEmail(siteProperties.getAdminEmail());
        contact.setUrl("https://" + siteProperties.getDomain());
        contact.setName("MorningStar Administrator");
        return new OpenAPI()
                .info(
                        new Info()
                                .title("晨星小站 - 在线接口文档")
                                .contact(contact)
                                .description("这是一个方便前后端开发人员快速了解开发接口需求的在线接口API文档")
                );
    }
}
