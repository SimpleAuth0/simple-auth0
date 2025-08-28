package io.github.simpleauth0.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: ReLive27
 * @date: 2025/8/26 22:49
 */
@Data
@TableName("auth0_user_sessions")
public class UserSessionsEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long user_id;
    private String session_token;
    private String ipAddress;
    private String deviceOs;
    private Date lastAccessTime;
    private Date loginTime;
    private Date createTime;
    private Date updateTime;
}
