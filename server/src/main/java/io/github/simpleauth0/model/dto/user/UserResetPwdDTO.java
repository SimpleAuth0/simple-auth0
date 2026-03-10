package io.github.simpleauth0.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: ReLive27
 * @date: 2025/11/12 22:59
 */
@Data
@Schema(description = "重置密码对象")
public class UserResetPwdDTO {
    @NotNull(message = "{user.id.notnull}")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    @NotBlank(message = "{user.password.notblank}")
    @Schema(description = "原口令", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String oriPassword;
    @NotBlank(message = "{user.password.notblank}")
    @Schema(description = "新口令", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String targetPassword;
}
