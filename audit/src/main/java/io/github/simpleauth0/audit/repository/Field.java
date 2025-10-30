package io.github.simpleauth0.audit.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ReLive27
 * @date: 2025/7/2 22:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Field {
    private String name;

    private String value;

    private String type;
}
