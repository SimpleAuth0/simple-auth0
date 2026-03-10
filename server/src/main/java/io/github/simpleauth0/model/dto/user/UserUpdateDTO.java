package io.github.simpleauth0.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: ReLive27
 * @date: 2025/11/12 22:58
 */
@Data
@Schema(description = "用户更新对象")
public class UserUpdateDTO {
    @NotBlank(message = "{user.username.notblank}")
    @Schema(description = "用户名", example = "admin")
    private String username;
    @Schema(description = "昵称", example = "admin")
    private String nickname;
    @Schema(description = "手机号", example = "13122200099")
    private String phone;
    @Schema(description = "邮箱", example = "test@163.com")
    private String email;
}
