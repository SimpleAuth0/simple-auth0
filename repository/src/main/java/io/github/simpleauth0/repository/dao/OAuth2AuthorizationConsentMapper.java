package io.github.simpleauth0.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.simpleauth0.repository.model.OAuth2AuthorizationConsentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ReLive27
 * @date: 2025/11/8 23:04
 */
@Mapper
public interface OAuth2AuthorizationConsentMapper extends BaseMapper<OAuth2AuthorizationConsentEntity> {
}
