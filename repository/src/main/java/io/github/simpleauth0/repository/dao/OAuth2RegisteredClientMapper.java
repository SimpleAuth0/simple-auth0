package io.github.simpleauth0.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.simpleauth0.repository.model.OAuth2RegisteredClientEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ReLive27
 * @date: 2025/11/8 23:04
 */
@Mapper
public interface OAuth2RegisteredClientMapper extends BaseMapper<OAuth2RegisteredClientEntity> {
}
