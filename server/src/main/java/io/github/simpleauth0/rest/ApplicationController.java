package io.github.simpleauth0.rest;

import org.springframework.web.bind.annotation.RestController;

/**
 * 应用管理控制器
 *
 * <p>
 * 提供与应用(Application)相关的接口，例如应用的创建、查询、修改、删除等。
 * 该控制器作为 REST API 的入口，返回的数据默认为 JSON 格式。
 * </p>
 *
 * <p>
 * 使用场景：前端调用应用管理相关接口时，通过该控制器提供服务。
 * </p>
 *
 * <p>
 * 注意事项：
 * 1. 后续需根据业务需求增加具体的 API 方法；
 * 2. 可以结合 Spring Security 对接口进行权限控制；
 * 3. 返回数据建议统一使用标准响应体封装。
 * </p>
 *
 * @author: ReLive27
 * @date: 2025/7/25 23:16
 */
@RestController
public class ApplicationController {

    /**
     *
     *
     * 应用分类别：发行人（应用提供商），客户端（）
     *
     * 列表展示字段：应用名称，应用类型（单页应用还是其他），
     *
     */
}
