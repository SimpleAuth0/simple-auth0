package io.github.simpleauth0.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: ReLive27
 * @date: 2025/8/28 23:02
 */
@Data
@TableName("auth0_app_metadata")
public class ApplicationMetadataEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String templateName;
    private String metadataConfig;
    private String description;
    private Date createTime;
    private Date updateTime;
}
