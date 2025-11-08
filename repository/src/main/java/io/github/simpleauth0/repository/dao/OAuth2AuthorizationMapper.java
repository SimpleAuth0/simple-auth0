package io.github.simpleauth0.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.simpleauth0.repository.model.OAuth2AuthorizationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ReLive27
 * @date: 2025/11/8 23:03
 */
@Mapper
public interface OAuth2AuthorizationMapper extends BaseMapper<OAuth2AuthorizationEntity> {
}
