package com.cet.pq.pqgovernanceservice.mapper;

import com.cet.pq.pqgovernanceservice.model.line.GovernanceData;
import com.cet.pq.pqgovernanceservice.model.line.MonitorReSubstation;
import com.cet.pq.pqgovernanceservice.model.line.QuantityLimit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ：gongtong
 * @ClassName ：MatterhornMapper
 * @date ：Created in 2021/2/23 14:09
 * @description： matterhorn数据查询
 */
public interface MatterhornMapper {
    /**
     * 查询监测点和变电站
     * @return
     */
    List<MonitorReSubstation> queryMonitorAndSubstation();

    /**
     * 查询监测点国标限值
     * @return
     */
    List<QuantityLimit> queryQuantityLimit(@Param("lineIds") List<Long> lineIds, @Param("dataIds") List<Long> dataIds);

    /**
     * 查询监测点国标限值
     * @return
     */
    int insertGovernanceData(List<GovernanceData> governanceDataList);
}
