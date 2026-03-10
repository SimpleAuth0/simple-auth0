package io.github.simpleauth0.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: ReLive27
 * @date: 2025/11/11 22:56
 */
@Data
@Schema(description = "链接的社会帐户对象")
public class LinkedAccountsVO {
    @Schema(description = "社交平台", example = "Github")
    private String platform;
    @Schema(description = "用户名", example = "admin")
    private String username;
    @Schema(description = "社交平台URL", example = "https://example.com")
    private String profileUrl;
}
