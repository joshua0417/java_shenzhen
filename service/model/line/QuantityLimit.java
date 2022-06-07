package com.cet.pq.pqgovernanceservice.model.line;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gongtong
 * @date 2022/5/6 8:47
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuantityLimit {
    private Long lineId;
    private Double uplimit;
    private Double lowlimit;
    private Long dataId;
}
