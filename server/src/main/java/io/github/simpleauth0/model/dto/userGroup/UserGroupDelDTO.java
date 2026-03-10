package io.github.simpleauth0.model.dto.userGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/12/1 22:42
 */
@Data
@Schema(description = "用户组删除对象")
public class UserGroupDelDTO {
    @NotEmpty(message = "{usergroup.ids.notempty}")
    @Schema(description = "用户组ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1]")
    private List<Long> ids;
}
