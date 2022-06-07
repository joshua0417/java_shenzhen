package com.cet.pq.pqgovernanceservice.controller;

import com.cet.pq.pqgovernanceservice.model.common.Result;
import com.cet.pq.pqgovernanceservice.service.GovernanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gongtong
 * @date 2022/4/29 11:36
 * @description
 */
@Api(value="GovernanceController",tags={"治理决策"})
@RestController
@RequestMapping("/governance")
@Slf4j
public class GovernanceController {

    @Autowired
    private GovernanceService governanceService;

    @ApiOperation(value = "追补历史治理数据")
    @GetMapping(value = "/dealHistoryData")
    public Result<Object> dealHistoryData(Long startTime, Long endTime) {
        return governanceService.dealHistoryData(startTime, endTime);
    }
}
