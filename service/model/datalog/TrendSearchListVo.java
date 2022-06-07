package com.cet.pq.pqgovernanceservice.model.datalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName TrendSearchListVo
 * @Description 定时记录查询参数
 * @Author zhangzhuang
 * @Date 2020/2/24 14:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendSearchListVo {
    private String endTime;
    private String startTime;
    /**
     * 周期
     */
    private Integer interval;
    private List<TrendSearchVo> meterConfigs;
}
