package io.github.simpleauth0.model.dto.userGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/12/1 22:26
 */
@Data
@Schema(description = "用户组更新对象")
public class UserGroupUpdateDTO {
    @NotNull(message = "{usergroup.groupid.notnull}")
    @Schema(description = "用户组Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long groupId;
    @NotBlank(message = "{usergroup.groupname.notblank}")
    @Schema(description = "用户组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
    private String groupName;
    @Schema(description = "关联用户ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "[1]")
    private List<Long> userIds;
    @Schema(description = "关联角色ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "[1]")
    private List<Long> roleIds;
}
