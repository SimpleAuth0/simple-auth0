package io.github.simpleauth0.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: ReLive27
 * @date: 2025/7/27 22:15
 */
public class APIResourcesController {
    @Operation(summary = "获取用户信息", description = "根据 ID 获取用户详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功",
                    content = @Content(schema = @Schema(implementation = SecurityProperties.User.class))),
            @ApiResponse(responseCode = "404", description = "用户未找到")
    })
    @GetMapping("api")
    public void  tt(){

    }
}
