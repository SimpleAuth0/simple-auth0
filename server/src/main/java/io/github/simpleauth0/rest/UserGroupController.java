package io.github.simpleauth0.rest;

import io.github.simpleauth0.model.dto.userGroup.UserGroupAddDTO;
import io.github.simpleauth0.model.dto.userGroup.UserGroupPageDTO;
import io.github.simpleauth0.model.dto.userGroup.UserGroupUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
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
 * @date: 2025/7/25 22:57
 */
@RestController
@RequestMapping("/userGroup")
@Tag(name = "用户组管理", description = "用户组信息相关操作")
public class UserGroupController {

    @Operation(summary = "新增用户组", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "新增用户组成功", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping()
    public void create(@Valid @RequestBody UserGroupAddDTO dto) {
    }

    @Operation(summary = "更新用户组", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新用户组成功", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping()
    public void update(@Valid @RequestBody UserGroupUpdateDTO dto) {
    }

    @Operation(summary = "用户组列表", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询用户组列表成功", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/list")
    public void delete(@Valid @RequestBody UserGroupPageDTO dto) {
    }
}
