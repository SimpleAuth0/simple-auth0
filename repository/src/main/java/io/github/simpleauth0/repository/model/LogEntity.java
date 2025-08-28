package io.github.simpleauth0.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: ReLive27
 * @date: 2025/8/28 23:08
 */
@Data
@TableName("auth0_log")
public class LogEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String eventType;
    private String eventDetail;
    private int userId;
    private int roleId;
    private String ipAddress;
    private String deviceType;
    private String deviceOs;
    private int success;
    private Date createTime;
    private Date updateTime;

}
