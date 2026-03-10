package io.github.simpleauth0.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/11/9 21:51
 */
@Data
@Schema(description = "用户信息对象")
public class UserInfoVO {
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
    @Schema(description = "用户头像", example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUg...")
    private String avatar;
    @Schema(description = "链接的社交账户", example = "[\n" +
            "    {\n" +
            "      \"platform\": \"Google\",\n" +
            "      \"username\": \"user123\",\n" +
            "      \"profileUrl\": \"https://plus.google.com/user123\",\n" +
            "    },\n" +
            "    {\n" +
            "      \"platform\": \"GitHub\",\n" +
            "      \"username\": \"devUser\",\n" +
            "      \"profileUrl\": \"https://github.com/devUser\",\n" +
            "    }\n" +
            "  ]")
    private List<LinkedAccountsVO> linkedAccounts;
}
