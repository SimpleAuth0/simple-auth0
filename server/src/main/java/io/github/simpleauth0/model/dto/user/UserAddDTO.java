package io.github.simpleauth0.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: ReLive27
 * @date: 2025/11/12 22:58
 */
@Data
@Schema(description = "用户新增对象")
public class UserAddDTO {
    @NotBlank(message = "{user.username.notblank}")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String username;
    @Schema(description = "昵称", example = "admin")
    private String nickname;
    @Schema(description = "用户组织Id", example = "1")
    private Long userGroupId;
    @NotBlank(message = "{user.passwordmethod.notblank}")
    @Schema(description = "口令设置方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "ADMIN_SET")
    private String setPasswordMethod;
    @Schema(description = "口令", requiredMode = Schema.RequiredMode.AUTO, example = "123456")
    private String password;
    @NotBlank(message = "{user.storetype.notblank}")
    @Schema(description = "存储类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "DEFAULT")
    private String storeType;
    @Schema(description = "手机号", example = "13122200099")
    private String phone;
    @Schema(description = "邮箱", example = "test@163.com")
    private String email;
}
