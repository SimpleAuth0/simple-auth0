package io.github.simpleauth0.audit.repository;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/7/2 22:53
 */
@Data
public class AuditLogEvent {

    private String eventType;

    private String eventDetail;

    private String username;

    private String ipAddress;

    private String deviceType;

    private String deviceOs;

    private Boolean success;

    private List<Field> fields = new ArrayList<Field>();

}
