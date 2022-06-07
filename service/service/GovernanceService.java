package com.cet.pq.pqgovernanceservice.service;

import com.cet.pq.pqgovernanceservice.model.common.Result;

/**
 * @author gongtong
 * @date 2022/4/29 11:36
 * @description 治理决策业务类
 */
public interface GovernanceService {

    /**
     * @Description: 处理历史治理决策数据
     **/
    Result dealHistoryData(Long startTime, Long endTime);
}
