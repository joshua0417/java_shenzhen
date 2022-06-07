package com.cet.pq.pqgovernanceservice.model.line;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gongtong
 * @date 2022/5/5 15:10
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorReSubstation {
    private Long substationId;
    private Long monitorId;
    private Long deviceId;
}
