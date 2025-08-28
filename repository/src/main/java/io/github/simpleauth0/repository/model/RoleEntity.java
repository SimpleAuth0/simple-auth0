package io.github.simpleauth0.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: ReLive27
 * @date: 2025/8/19 22:44
 */
@Data
@TableName("auth0_role")
public class RoleEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roleName;
    private String roleCode;
    private Integer roleAudience;
    private Integer status;
    private String description;
    private Date createTime;
    private Date updateTime;
}
