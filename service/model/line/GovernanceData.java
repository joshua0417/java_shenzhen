package com.cet.pq.pqgovernanceservice.model.line;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gongtong
 * @date 2022/5/6 8:59
 * @description 治理决策
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovernanceData {
    private Long substation_id;
    private Integer level;
    private Long quantityparaset_id;
    private boolean isOverlimit;
    private Long logtime;
    private Double value;
    private Double evaluate;
}
