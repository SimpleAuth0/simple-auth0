package io.github.simpleauth0.repository.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: ReLive27
 * @date: 2025/8/28 23:06
 */
@Data
@TableName("auth0_oauth2_authorization_consent")
public class OAuth2AuthorizationConsent {
    private String registeredClientId;
    private String principalName;
    private String authorities;
    private Date createTime;
    private Date updateTime;
}
