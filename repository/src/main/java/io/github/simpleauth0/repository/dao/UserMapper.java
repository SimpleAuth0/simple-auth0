package io.github.simpleauth0.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.simpleauth0.repository.model.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ReLive27
 * @date: 2025/8/19 22:46
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
