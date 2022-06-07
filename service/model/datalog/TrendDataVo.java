package com.cet.pq.pqgovernanceservice.model.datalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName TrendDataVo
 * @Description 定时记录数据
 * @Author zhangzhuang
 * @Date 2020/2/24 14:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendDataVo {
    private Long deviceId;
    private Integer logicalId;
    private Integer dataTypeId;
    private Long dataId;
    private String unit;
    private List<DataLogData> dataList;
}
