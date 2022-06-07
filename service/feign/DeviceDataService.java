package com.cet.pq.pqgovernanceservice.feign;

import com.cet.pq.pqgovernanceservice.model.common.Result;
import com.cet.pq.pqgovernanceservice.model.datalog.TrendDataVo;
import com.cet.pq.pqgovernanceservice.model.datalog.TrendSearchListVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author srr
 * @Description 设备数据服务
 * @Data Created in ${Date}
 */
@FeignClient(value = "device-data-service")
public interface DeviceDataService {


	/**
	 * 查询定时记录
	 *
	 * @param searchVo
	 * @return
	 */
	@PostMapping("/api/v1/batch/datalog/span/group")
	public Result<List<TrendDataVo>> queryTrendCurveData2(@RequestBody TrendSearchListVo searchVo, @RequestParam("fill") Boolean fill);


}
