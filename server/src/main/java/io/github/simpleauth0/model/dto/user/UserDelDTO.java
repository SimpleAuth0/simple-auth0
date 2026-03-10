package io.github.simpleauth0.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/11/12 22:58
 */
@Data
@Schema(description = "用户删除对象")
public class UserDelDTO {
    @NotEmpty(message = "{user.ids.notempty}")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1]")
    private List<Long> ids;

}
