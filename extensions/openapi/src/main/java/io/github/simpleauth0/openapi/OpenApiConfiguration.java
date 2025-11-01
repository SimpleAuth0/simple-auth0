package io.github.simpleauth0.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: ReLive27
 * @date: 2025/7/27 22:42
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SimpleAuth0 身份与访问管理 API 文档")
                        .version("v1")
                        .description(
                                "**SimpleAuth0** 是一个开源的身份与访问管理（IAM）平台，" +
                                        "致力于为开发者提供安全、灵活、易集成的认证与授权解决方案。\n\n" +
                                        "主要功能包括：\n" +
                                        "- 支持多种身份认证协议（OAuth 2.0、OIDC、SAML、LDAP 等）\n" +
                                        "- 提供统一的用户认证与单点登录（SSO）\n" +
                                        "- 支持多因素认证（MFA）与细粒度访问控制\n" +
                                        "- 丰富的审计与安全日志功能\n\n" +
                                        "本文档提供 SimpleAuth0 服务端的 RESTful API 接口说明，" +
                                        "便于开发者集成、定制与二次开发。"
                        )
                        .contact(new Contact()
                                .name("SimpleAuth0 开发团队")
                                .url("https://github.com/SimpleAuth0")
                                .email("support@simpleauth0.io"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }


}
