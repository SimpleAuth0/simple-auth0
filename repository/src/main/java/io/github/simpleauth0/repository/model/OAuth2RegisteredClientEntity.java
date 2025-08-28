package io.github.simpleauth0.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * @author: ReLive27
 * @date: 2025/8/19 22:40
 */
@Data
@TableName("auth0_oauth2_registered_client")
public class OAuth2RegisteredClientEntity {
    @TableId(type = IdType.AUTO)
    private String id;
    private String clientId;
    private Date clientIdIssuedAt;
    private String clientSecret;
    private Date clientSecretExpiresAt;
    private String clientName;
    private String clientAuthMethods;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String scopes;
    private String clientSettings;
    private String tokenSettings;
    private Date createTime;
    private Date updateTime;
}
