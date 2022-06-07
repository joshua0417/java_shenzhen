package com.cet.pq.pqgovernanceservice.task;

import com.cet.pq.pqgovernanceservice.model.common.Result;
import com.cet.pq.pqgovernanceservice.service.GovernanceService;
import com.cet.pq.pqgovernanceservice.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gongtong
 * @date 2022/5/6 10:02
 * @description
 */
@Component
@Slf4j
public class GovernanceTask  {

    @Autowired
    private GovernanceService governanceService;

    @Scheduled(cron = "${governance.task.cron}")
    public void dealGovernanceTask() {
        log.info("start deal governance task.");
        Long startTime = DateUtils.getPreTwoHour();
        Long endTime = DateUtils.getCurrentHour();
        Result result = governanceService.dealHistoryData(startTime, endTime);
        log.info("success add governance data count {}.", result.getData());
    }
}
