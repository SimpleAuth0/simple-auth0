package io.github.simpleauth0.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: ReLive27
 * @date: 2025/11/12 22:59
 */
@Data
@Schema(description = "用户查询对象")
public class UserPageDTO {
    @NotNull(message = "{pagenum.notnull}")
    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer pageNum;
    @NotNull(message = "{pagesize.notnull}")
    @Schema(description = "分页大小", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer pageSize;
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "test")
    private String username;
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "13122233322")
    private String phone;
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "test@163.com")
    private String email;
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "2025-11-23 00:00:00")
    private String startTime;
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "2025-11-24 00:00:00")
    private String endTime;
}
