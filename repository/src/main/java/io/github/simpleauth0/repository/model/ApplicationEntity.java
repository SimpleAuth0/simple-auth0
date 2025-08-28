package io.github.simpleauth0.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: ReLive27
 * @date: 2025/8/17 22:21
 */
@Data
@TableName("auth0_app")
public class ApplicationEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String logo;
    private String appType;
    private String appCategory;
    private String protocolType;
    private String description;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
