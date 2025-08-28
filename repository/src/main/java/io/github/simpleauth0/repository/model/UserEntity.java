package io.github.simpleauth0.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: ReLive27
 * @date: 2025/8/4 23:19
 */
@Data
@TableName("auth0_user")
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private String setPasswordMethod;
    private String storeType;
    private Integer status;
    private Integer accountNonLocked;
    private Integer enabled;
    private String description;
    private Date createTime;
    private Date updateTime;
}
