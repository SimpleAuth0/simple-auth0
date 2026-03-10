package io.github.simpleauth0.repository.dao;

import io.github.simpleauth0.repository.dao.internal.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * @author: ReLive27
 * @date: 2025/11/8 23:08
 */
@Repository
@RequiredArgsConstructor
public class ApplicationRepository {
    private final ApplicationMapper applicationMapper;
}
