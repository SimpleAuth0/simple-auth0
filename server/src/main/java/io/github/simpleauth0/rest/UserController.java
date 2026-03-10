package io.github.simpleauth0.rest;

import io.github.simpleauth0.model.dto.user.*;
import io.github.simpleauth0.model.vo.user.UserInfoVO;
import io.github.simpleauth0.model.vo.user.UserListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author: ReLive27
 * @date: 2025/7/21 23:04
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户信息相关操作")
public class UserController {

    @Operation(summary = "获取当前用户信息", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功返回当前用户信息", content = @Content(schema = @Schema(implementation = UserInfoVO.class))),
            @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/getInfo")
    public UserInfoVO getUserInfo() {
        return new UserInfoVO();
    }

    @Operation(summary = "新增用户", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "新增用户成功", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping()
    public void create(@Valid @RequestBody UserAddDTO dto) {
    }

    @Operation(summary = "更新用户", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新用户成功", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping()
    public void update(@Valid @RequestBody UserUpdateDTO dto) {

    }

    @Operation(summary = "删除用户", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除用户成功", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping()
    public void delete(@Valid @RequestBody UserDelDTO dto) {

    }

    @Operation(summary = "用户列表", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "用户列表查询成功", content = @Content(schema = @Schema(implementation = UserListVo.class))),
            @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/page")
    public UserListVo page(@Valid @RequestBody UserPageDTO dto) {

        return new UserListVo();
    }

    @Operation(summary = "检查用户名是否可用", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "检查完成", content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping("/checkUsername")
    public Boolean checkUsername(
            @Parameter(description = "待检查的用户名", required = true)
            @RequestParam String username) {
        return true;
    }


    @Operation(summary = "用户锁定", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "用户锁定", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping("/lock")
    public void lock(@Parameter(description = "用户Id", required = true)
                     @RequestParam Long userId) {

    }

    @Operation(summary = "重置密码", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "重置密码", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @PostMapping("/reset/password")
    public void resetPassword(@Valid @RequestBody UserResetPwdDTO dto) {

    }
}

