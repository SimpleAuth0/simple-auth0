package io.github.simpleauth0.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.simpleauth0.repository.model.UserSessionsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ReLive27
 * @date: 2025/11/8 23:06
 */
@Mapper
public interface UserSessionsMapper extends BaseMapper<UserSessionsEntity> {
}
