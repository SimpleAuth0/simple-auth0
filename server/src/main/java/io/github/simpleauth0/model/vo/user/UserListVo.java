package io.github.simpleauth0.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/11/22 21:03
 */
@Data
@Schema(description = "用户列表对象")
public class UserListVo {
    @Schema(description = "总数", example = "1")
    private Integer total;
    @Schema(description = "用户列表", example = "1")
    private List<UserList> list;


    @Data
    static class UserList {
        @Schema(description = "用户Id", example = "1")
        private Long id;
        @Schema(description = "用户名", example = "admin")
        private String username;
        @Schema(description = "昵称", example = "admin")
        private String nickname;
        @Schema(description = "手机号", example = "13112345678")
        private String phone;
        @Schema(description = "邮箱", example = "test@163.com")
        private String email;
        @Schema(description = "创建时间", example = "2025-01-22 09:22:12")
        private String createTime;
    }
}
