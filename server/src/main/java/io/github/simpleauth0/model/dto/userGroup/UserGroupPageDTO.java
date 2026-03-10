package io.github.simpleauth0.model.dto.userGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: ReLive27
 * @date: 2025/12/1 22:52
 */
@Data
@Schema(description = "用户组查询对象")
public class UserGroupPageDTO {
    @NotNull(message = "{pagenum.notnull}")
    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer pageNum;
    @NotNull(message = "{pagesize.notnull}")
    @Schema(description = "分页大小", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer pageSize;
    @Schema(description = "用户组名", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "test")
    private String groupName;
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "2025-11-23 00:00:00")
    private String startTime;
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "2025-11-24 00:00:00")
    private String endTime;
}
