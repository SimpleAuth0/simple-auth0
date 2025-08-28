package io.github.simpleauth0.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: ReLive27
 * @date: 2025/8/28 22:42
 */
@Data
@TableName("auth0_permission")
public class PermissionEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long parentId;
    private String permissionCode;
    private String resourceType;
    private String resource;
    private String action;
    private String description;
    private Date createTime;
    private Date updateTime;
}
