package com.cet.pq.pqgovernanceservice.model.datalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DataLogData
 * @Description 定时记录数据
 * @Author zhangzhuang
 * @Date 2020/2/24 14:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataLogData {
    private Long time;
    private Double value;
    private Integer status;
}
