package com.cet.pq.pqgovernanceservice.service.impl;

import com.cet.pq.pqgovernanceservice.feign.DeviceDataService;
import com.cet.pq.pqgovernanceservice.mapper.MatterhornMapper;
import com.cet.pq.pqgovernanceservice.model.common.Constants;
import com.cet.pq.pqgovernanceservice.model.common.Result;
import com.cet.pq.pqgovernanceservice.model.datalog.DataLogData;
import com.cet.pq.pqgovernanceservice.model.datalog.TrendDataVo;
import com.cet.pq.pqgovernanceservice.model.datalog.TrendSearchListVo;
import com.cet.pq.pqgovernanceservice.model.datalog.TrendSearchVo;
import com.cet.pq.pqgovernanceservice.model.line.GovernanceData;
import com.cet.pq.pqgovernanceservice.model.line.MonitorReSubstation;
import com.cet.pq.pqgovernanceservice.model.line.QuantityLimit;
import com.cet.pq.pqgovernanceservice.service.GovernanceService;
import com.cet.pq.pqgovernanceservice.util.DateUtils;
import com.cet.pq.pqgovernanceservice.util.ParseDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gongtong
 * @date 2022/4/29 11:43
 * @description 治理决策业务类
 */
@Service
@Slf4j
public class GovernanceServiceImpl implements GovernanceService {

    @Autowired
    private MatterhornMapper matterhornMapper;
    @Autowired
    private DeviceDataService deviceDataService;

    @Override
    public Result dealHistoryData(Long startTime, Long endTime) {
        log.info("start deal history data. starttime {}, endtime {}.", startTime, endTime);
        //查询所有监测点和变电站信息
        List<MonitorReSubstation> monitorReSubstations = matterhornMapper.queryMonitorAndSubstation();
        if (CollectionUtils.isEmpty(monitorReSubstations)) {
            log.info("monitor is empty.");
            return Result.success();
        }
        //查询监测点的定时记录
        Result<List<TrendDataVo>> trendCurveDataResult = queryDataLog(startTime, endTime, monitorReSubstations);
        if (trendCurveDataResult.getCode() != 0) {
            return Result.error("request datalog error. " + trendCurveDataResult.getMsg());
        }
        List<TrendDataVo> trendDataVos = trendCurveDataResult.getData();
        //按照设备分组
        Map<Long, List<TrendDataVo>> deviceTrendDataMap = trendDataVos.stream().collect(Collectors.groupingBy(TrendDataVo::getDeviceId));
        List<GovernanceData> governanceDataList = new ArrayList<>();
        deviceTrendDataMap.forEach((deviceId, trendDatas) -> {
            //过滤计算电压偏差数据
            List<TrendDataVo> voltageDeviationResult = trendDatas.stream().filter(t -> Constants.VOLTAGE_DEVIATION.contains(t.getDataId())).collect(Collectors.toList());
            List<Double> powerQualityValues = new ArrayList<>();
            List<Double> voltageData = new ArrayList<>();
            List<Double> voltageDeviationValue = new ArrayList<>();
            if (!CollectionUtils.isEmpty(voltageDeviationResult)) {
                // 电压偏差取AB、BC、CA线电压偏差的均值
                List<List<Double>> voltageDeviationDataList = new ArrayList<>();
                voltageDeviationResult.forEach(result -> {
                    List<Double> dataLogs = result.getDataList().stream().map(DataLogData::getValue).collect(Collectors.toList());
                    voltageDeviationDataList.add(dataLogs);
                });
                Map<Integer, Double> dataMap = new TreeMap<>();
                voltageDeviationDataList.forEach(datas -> {
                    for (int i = 0; i< datas.size(); i++) {
                        Double value = ParseDataUtil.parseDouble(datas.get(i));
                        if (!dataMap.containsKey(i)) {
                            dataMap.put(i, value);
                        } else {
                            dataMap.put(i, dataMap.get(i) + value);
                        }
                    }
                });
                dataMap.forEach((k, v) -> voltageDeviationValue.add(v / 3));
                voltageData.addAll(calcuVoltageDeviation(voltageDeviationValue));
                powerQualityValues.add(calcuPowerQualityGrade(voltageData));
            }
            //过滤计算频率偏差数据
            List<TrendDataVo> frequencyDeviationResult = trendDatas.stream().filter(t -> Constants.FREQUENCY_DEVIATION.contains(t.getDataId())).collect(Collectors.toList());
            List<Double> frequencyDeviationData = new ArrayList<>();
            List<Double> frequencyDeviationValue = new ArrayList<>();
            if (!CollectionUtils.isEmpty(frequencyDeviationResult)) {
                List<DataLogData> dataList = frequencyDeviationResult.get(0).getDataList();
                frequencyDeviationValue.addAll(dataList.stream().map(DataLogData::getValue).collect(Collectors.toList()));
                frequencyDeviationData.addAll(calcuFrequencyDeviation(frequencyDeviationValue));
                powerQualityValues.add(calcuPowerQualityGrade(frequencyDeviationData));
            }
            //过滤计算三相不平衡数据
            List<TrendDataVo> threePhaseImbalanceResult = trendDatas.stream().filter(t -> Constants.THREE_PHASE_IMBALANCE.contains(t.getDataId())).collect(Collectors.toList());
            List<Double> threePhaseImbalanceData = new ArrayList<>();
            List<Double> threePhaseImbalanceValue = new ArrayList<>();
            if (!CollectionUtils.isEmpty(threePhaseImbalanceResult)) {
                List<DataLogData> dataList = threePhaseImbalanceResult.get(0).getDataList();
                threePhaseImbalanceValue.addAll(dataList.stream().map(DataLogData::getValue).collect(Collectors.toList()));
                threePhaseImbalanceData.addAll(calcuThreePhaseImbalance(threePhaseImbalanceValue));
                powerQualityValues.add(calcuPowerQualityGrade(threePhaseImbalanceData));
            }
            //过滤计算谐波电流数据
            List<TrendDataVo> harmonicCurrentResult = trendDatas.stream().filter(t -> Constants.HARMONIC_CURRENT.contains(t.getDataId())).collect(Collectors.toList());
            List<TrendDataVo> iaCurrentResult = trendDatas.stream().filter(t -> Constants.IA_CURRENT.contains(t.getDataId())).collect(Collectors.toList());
            List<Double> harmonicCurrentData = new ArrayList<>();
            List<Double> harmonicCurrentValue = new ArrayList<>();
            if (!CollectionUtils.isEmpty(harmonicCurrentResult) && !CollectionUtils.isEmpty(iaCurrentResult)) {
                //谐波电压需计算总谐波畸变率，取50次总谐波；
                List<List<Double>> harmonicCurrentDataList = new ArrayList<>();
                harmonicCurrentResult.forEach(result -> {
                    List<Double> dataLogs = result.getDataList().stream().map(DataLogData::getValue).collect(Collectors.toList());
                    harmonicCurrentDataList.add(dataLogs);
                });
                Map<Integer, Double> dataMap = new TreeMap<>();
                harmonicCurrentDataList.forEach(datas -> {
                    for (int i = 0; i< datas.size(); i++) {
                        Double value = ParseDataUtil.parseDouble(datas.get(i));
                        if (!dataMap.containsKey(i)) {
                            dataMap.put(i, value);
                        } else {
                            dataMap.put(i, dataMap.get(i) + value);
                        }
                    }
                });
                //除以基波电流
                List<DataLogData> iaCurrentDataList = iaCurrentResult.get(0).getDataList();
                dataMap.forEach((k, v) -> harmonicCurrentValue.add(v / iaCurrentDataList.get(k).getValue()));
                harmonicCurrentData.addAll(calcuHarmonicCurrentVoltage(harmonicCurrentValue));
                powerQualityValues.add(calcuPowerQualityGrade(harmonicCurrentData));
            }
            //过滤计算谐波电压数据
            List<TrendDataVo> harmonicVoltageResult = trendDatas.stream().filter(t -> Constants.HARMONIC_VOLTAGE.contains(t.getDataId())).collect(Collectors.toList());
            List<Double> harmonicVoltageData = new ArrayList<>();
            List<Double> harmonicVoltageValue = new ArrayList<>();
            if (!CollectionUtils.isEmpty(harmonicVoltageResult)) {
                //谐波电压需计算总谐波畸变率，取50次总谐波；
                List<List<Double>> harmonicVoltageDataList = new ArrayList<>();
                harmonicVoltageResult.forEach(result -> {
                    List<Double> dataLogs = result.getDataList().stream().map(DataLogData::getValue).collect(Collectors.toList());
                    harmonicVoltageDataList.add(dataLogs);
                });
                Map<Integer, Double> dataMap = new TreeMap<>();
                harmonicVoltageDataList.forEach(datas -> {
                    for (int i = 0; i< datas.size(); i++) {
                        Double value = ParseDataUtil.parseDouble(datas.get(i));
                        if (!dataMap.containsKey(i)) {
                            dataMap.put(i, value);
                        } else {
                            dataMap.put(i, dataMap.get(i) + value);
                        }
                    }
                });
                dataMap.forEach((k, v) -> harmonicVoltageValue.add(v));
                harmonicVoltageData.addAll(calcuHarmonicCurrentVoltage(harmonicVoltageValue));
                powerQualityValues.add(calcuPowerQualityGrade(harmonicVoltageData));
            }
            //判断电能质量等级
            Integer level = calcuPowerQualityLevel(powerQualityValues);
            //获取国标限值，判断是否超标
            List<MonitorReSubstation> reSubstations = monitorReSubstations.stream().filter(t -> deviceId.equals(t.getDeviceId())).collect(Collectors.toList());
            List<Long> lineIds = reSubstations.stream().map(MonitorReSubstation::getMonitorId).collect(Collectors.toList());
            //取固定的参数值作为国标限值
            List<Long> dataIds = Arrays.asList(100L, 69L, 1040002L, 10002L, 97L);
            List<QuantityLimit> quantityLimits = matterhornMapper.queryQuantityLimit(lineIds, dataIds);
            //组装治理决策数据
            combineGovernanceData(endTime, reSubstations, governanceDataList, voltageData, frequencyDeviationData,
                    threePhaseImbalanceData, harmonicCurrentData, harmonicVoltageData, voltageDeviationValue, frequencyDeviationValue,
                    threePhaseImbalanceValue, harmonicCurrentValue, harmonicVoltageValue, level, dataIds, quantityLimits);
        });
        //插入治理决策数据
        if (!CollectionUtils.isEmpty(governanceDataList)) {
            matterhornMapper.insertGovernanceData(governanceDataList);
        }
        return Result.success(governanceDataList.size());
    }

    /**
     * @Description: 查询监测点的定时记录
     **/
    private Result<List<TrendDataVo>> queryDataLog(Long startTime, Long endTime, List<MonitorReSubstation> monitorReSubstations) {
        List<TrendSearchVo> meterConfigs = new ArrayList<>();
        monitorReSubstations.forEach(monitorReSubstation -> {
            //dataTypeId取95值
            Constants.VOLTAGE_DEVIATION.forEach(dataId -> {
                TrendSearchVo trendSearchVo = new TrendSearchVo(monitorReSubstation.getDeviceId(), dataId, 5, 1);
                meterConfigs.add(trendSearchVo);
            });
            Constants.THREE_PHASE_IMBALANCE.forEach(dataId -> {
                TrendSearchVo trendSearchVo = new TrendSearchVo(monitorReSubstation.getDeviceId(), dataId, 5, 1);
                meterConfigs.add(trendSearchVo);
            });
            Constants.HARMONIC_CURRENT.forEach(dataId -> {
                TrendSearchVo trendSearchVo = new TrendSearchVo(monitorReSubstation.getDeviceId(), dataId, 5, 1);
                meterConfigs.add(trendSearchVo);
            });
            Constants.HARMONIC_VOLTAGE.forEach(dataId -> {
                TrendSearchVo trendSearchVo = new TrendSearchVo(monitorReSubstation.getDeviceId(), dataId, 5, 1);
                meterConfigs.add(trendSearchVo);
            });
            Constants.FREQUENCY_DEVIATION.forEach(dataId -> {
                TrendSearchVo trendSearchVo = new TrendSearchVo(monitorReSubstation.getDeviceId(), dataId,  5, 1);
                meterConfigs.add(trendSearchVo);
            });
            Constants.IA_CURRENT.forEach(dataId -> {
                TrendSearchVo trendSearchVo = new TrendSearchVo(monitorReSubstation.getDeviceId(), dataId,  5, 1);
                meterConfigs.add(trendSearchVo);
            });
        });
        //取两小时，15min间隔的数据点
        TrendSearchListVo trendSearchListVo = new TrendSearchListVo(DateUtils.dateToStr(new Date(endTime), DateUtils.DATE_TO_STRING_DETAIAL_PATTERN),
                DateUtils.dateToStr(new Date(startTime), DateUtils.DATE_TO_STRING_DETAIAL_PATTERN), 15, meterConfigs);
        //查询定时记录数据
        return deviceDataService.queryTrendCurveData2(trendSearchListVo, true);
    }

    /**
     * @Description: 组装参数
     **/
    private void combineGovernanceData(Long endTime, List<MonitorReSubstation> monitorReSubstations, List<GovernanceData> governanceDataList,
                                       List<Double> voltageDeviationData, List<Double> frequencyDeviationData, List<Double> threePhaseImbalanceData,
                                       List<Double> harmonicCurrentData, List<Double> harmonicVoltageData, List<Double> voltageDeviationValue,
                                       List<Double> frequencyDeviationValue, List<Double> threePhaseImbalanceValue,
                                       List<Double> harmonicCurrentValue, List<Double> harmonicVoltageValue,Integer level, List<Long> dataIds, List<QuantityLimit> quantityLimits) {
        monitorReSubstations.forEach(monitorReSubstation -> {
            Long lineId = monitorReSubstation.getMonitorId();
            Long substationId = monitorReSubstation.getSubstationId();
            dataIds.forEach(dataId -> {
                GovernanceData governanceData = new GovernanceData();
                governanceData.setSubstation_id(substationId);
                governanceData.setLogtime(endTime);
                governanceData.setLevel(level);
                governanceDataList.add(governanceData);
                if (dataId == 100L) {
                    List<QuantityLimit> limits = quantityLimits.stream().filter(t -> lineId.equals(t.getLineId()) && 100L == t.getDataId()).collect(Collectors.toList());
                    Boolean isOverlimit = false;
                    double avgData = voltageDeviationValue.stream().mapToDouble(ParseDataUtil::parseDouble).average().orElse(0D);
                    if (!CollectionUtils.isEmpty(limits)) {
                        QuantityLimit quantityLimit = limits.get(0);
                        if (avgData < ParseDataUtil.parseDouble(quantityLimit.getLowlimit()) || avgData > ParseDataUtil.parseDouble(quantityLimit.getUplimit())) {
                            isOverlimit = true;
                        }
                    }
                    governanceData.setOverlimit(isOverlimit);
                    double dataValue = voltageDeviationData.stream().mapToDouble(ParseDataUtil::parseDouble).average().orElse(0D);
                    governanceData.setEvaluate(dataValue);
                    governanceData.setValue(avgData);
                    // 1001电压偏差 1002三相不平衡 1003谐波电流 1004谐波电压 1005频率偏差
                    governanceData.setQuantityparaset_id(1001L);
                } else if (dataId == 69L) {
                    List<QuantityLimit> limits = quantityLimits.stream().filter(t -> lineId.equals(t.getLineId()) && 69L == t.getDataId()).collect(Collectors.toList());
                    Boolean isOverlimit = false;
                    double avgData = threePhaseImbalanceValue.stream().mapToDouble(ParseDataUtil::parseDouble).average().orElse(0D);
                    if (!CollectionUtils.isEmpty(limits)) {
                        QuantityLimit quantityLimit = limits.get(0);
                        if (avgData < ParseDataUtil.parseDouble(quantityLimit.getLowlimit()) || avgData > ParseDataUtil.parseDouble(quantityLimit.getUplimit())) {
                            isOverlimit = true;
                        }
                    }
                    governanceData.setOverlimit(isOverlimit);
                    double dataValue = threePhaseImbalanceData.stream().mapToDouble(ParseDataUtil::parseDouble).average().orElse(0D);
                    governanceData.setEvaluate(dataValue);
                    governanceData.setValue(avgData);
                    // 1001电压偏差 1002三相不平衡 1003谐波电流 1004谐波电压 1005频率偏差
                    governanceData.setQuantityparaset_id(1002L);
                } else if (dataId == 1040002L) {
                    List<QuantityLimit> limits = quantityLimits.stream().filter(t -> lineId.equals(t.getLineId()) && 1040002L == t.getDataId()).collect(Collectors.toList());
                    Boolean isOverlimit = false;
                    double avgData = harmonicCurrentValue.stream().mapToDouble(ParseDataUtil::parseDouble).average().orElse(0D);
                    if (!CollectionUtils.isEmpty(limits)) {
                        QuantityLimit quantityLimit = limits.get(0);
                        if (avgData < ParseDataUtil.parseDouble(quantityLimit.getLowlimit()) || avgData > ParseDataUtil.parseDouble(quantityLimit.getUplimit())) {
                            isOverlimit = true;
                        }
                    }
                    double dataValue = harmonicCurrentData.stream().mapToDouble(ParseDataUtil::parseDouble).average().orElse(0D);
                    governanceData.setEvaluate(dataValue);
                    governanceData.setValue(avgData);
                    governanceData.setOverlimit(isOverlimit);
                    // 1001电压偏差 1002三相不平衡 1003谐波电流 1004谐波电压 1005频率偏差
                    governanceData.setQuantityparaset_id(1003L);
                } else if (dataId == 10002L) {
                    List<QuantityLimit> limits = quantityLimits.stream().filter(t -> lineId.equals(t.getLineId()) && 10002L == t.getDataId()).collect(Collectors.toList());
                    Boolean isOverlimit = false;
                    double avgData = harmonicVoltageValue.stream().mapToDouble(ParseDataUtil::parseDouble).average().orElse(0D);
                    if (!CollectionUtils.isEmpty(limits)) {
                        QuantityLimit quantityLimit = limits.get(0);
                        if (avgData < ParseDataUtil.parseDouble(quantityLimit.getLowlimit()) || avgData > ParseDataUtil.parseDouble(quantityLimit.getUplimit())) {
                            isOverlimit = true;
                        }
                    }
                    governanceData.setOverlimit(isOverlimit);
                    double dataValue = harmonicVoltageData.stream().mapToDouble(ParseDataUtil::parseDouble).average().orElse(0D);
                    governanceData.setEvaluate(dataValue);
                    governanceData.setValue(avgData);
                    // 1001电压偏差 1002三相不平衡 1003谐波电流 1004谐波电压 1005频率偏差
                    governanceData.setQuantityparaset_id(1004L);
                } else if (dataId == 97L) {
                    List<QuantityLimit> limits = quantityLimits.stream().filter(t -> lineId.equals(t.getLineId()) && 97L == t.getDataId()).collect(Collectors.toList());
                    Boolean isOverlimit = false;
                    double avgData = frequencyDeviationValue.stream().mapToDouble(ParseDataUtil::parseDouble).average().orElse(0D);
                    if (!CollectionUtils.isEmpty(limits)) {
                        QuantityLimit quantityLimit = limits.get(0);
                        if (avgData < ParseDataUtil.parseDouble(quantityLimit.getLowlimit()) || avgData > ParseDataUtil.parseDouble(quantityLimit.getUplimit())) {
                            isOverlimit = true;
                        }
                    }
                    governanceData.setOverlimit(isOverlimit);
                    double dataValue = frequencyDeviationData.stream().mapToDouble(ParseDataUtil::parseDouble).average().orElse(0D);
                    governanceData.setEvaluate(dataValue);
                    governanceData.setValue(avgData);
                    // 1001电压偏差 1002三相不平衡 1003谐波电流 1004谐波电压 1005频率偏差
                    governanceData.setQuantityparaset_id(1005L);
                }
            });
        });
    }

    /**
     * @Description: 计算隶属度与电能质量等级关系
     **/
    private Integer calcuPowerQualityLevel(List<Double> powerQualityValues) {
        /**
         * [0.29 0.22 0.18 0.16 0.15]  1“优质”、2“良好”、3“中等”、4“合格”、5“不合格”
         * 取对应的最大的元素为电能质量等级
         */
        int level = powerQualityValues.indexOf(Collections.max(powerQualityValues));
        return level + 1;
    }
    /**
     * @Description: 计算隶属度与电能质量等级关系
     **/
    private Double calcuPowerQualityGrade(List<Double> datas) {
        double data1 = (datas.stream().filter(t -> t >= 0.9 && t <= 1).count() / (double) datas.size()) * 0.25;
        double data2 = (datas.stream().filter(t -> t >= 0.8 && t < 0.9).count() / (double) datas.size()) * 0.15;
        double data3 = (datas.stream().filter(t -> t >= 0.7 && t < 0.8).count() / (double) datas.size()) * 0.2;
        double data4 = (datas.stream().filter(t -> t >= 0.5 && t < 0.7).count() / (double) datas.size()) * 0.25;
        double data5 = (datas.stream().filter(t -> t < 0.5).count() / (double) datas.size()) * 0.15;
        return data1 + data2 + data3 + data4 + data5;
    }

    /**
     * @Description: 计算电压偏差隶属度值
     **/
    private List<Double> calcuVoltageDeviation(List<Double> datas) {
        List<Double> degreeDatas = new ArrayList<>();
        datas.forEach(data -> {
            data = ParseDataUtil.parseDouble(data);
            if (data <= -7 && data >= -10) {
                degreeDatas.add((10 + data) / 3);
            } else if (data >= -7 && data <= 7) {
                degreeDatas.add(1D);
            } else if (data >= 7 && data <= 10) {
                degreeDatas.add((10 - data) / 3);
            } else if (data <= -10 || data >= 10) {
                degreeDatas.add(0D);
            }
        });
        return degreeDatas;
    }

    /**
     * @Description: 计算频率偏差隶属度值
     **/
    private List<Double> calcuFrequencyDeviation(List<Double> datas) {
        List<Double> degreeDatas = new ArrayList<>();
        datas.forEach(data -> {
            data = ParseDataUtil.parseDouble(data);
            if (data >= -0.5 && data <= -0.2) {
                degreeDatas.add((0.5 + data) / 0.3);
            } else if (data >= -0.2 && data <= 0.2) {
                degreeDatas.add(1D);
            }  else if (data >= 0.2 && data <= 0.5) {
                degreeDatas.add((0.5 - data) / 0.3);
            } else if (data <= -0.5 || data >= 0.5) {
                degreeDatas.add(0D);
            }
        });
        return degreeDatas;
    }

    /**
     * @Description: 计算三相不平衡隶属度值
     **/
    private List<Double> calcuThreePhaseImbalance(List<Double> datas) {
        List<Double> degreeDatas = new ArrayList<>();
        datas.forEach(data -> {
            data = ParseDataUtil.parseDouble(data);
            if (data <= 2) {
                degreeDatas.add(1D);
            } else if (data>= 2 && data <= 5) {
                degreeDatas.add((5 - data) / 3);
            } else if (data >= 5) {
                degreeDatas.add(0D);
            }
        });
        return degreeDatas;
    }

    /**
     * @Description: 计算谐波电流电压隶属度值
     **/
    private List<Double> calcuHarmonicCurrentVoltage(List<Double> datas) {
        List<Double> degreeDatas = new ArrayList<>();
        datas.forEach(data -> {
            data = ParseDataUtil.parseDouble(data);
            if (data <= 2) {
                degreeDatas.add(1D);
            } else if (data>= 2 && data <= 5) {
                degreeDatas.add((5 - data) / 3);
            } else if (data >= 5) {
                degreeDatas.add(0D);
            }
        });
        return degreeDatas;
    }

}
