package com.cet.pq.pqgovernanceservice.model.datalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author leipeng
 * @date 2019年12月5日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendSearchVo {

    private Long dataId;
    private Integer dataTypeId;
    private Long deviceId;
    private Integer logicalId;

    public TrendSearchVo(Long deviceId, Long dataId, int dataTypeId, int logicalId) {
        this.dataId = dataId;
        this.dataTypeId = dataTypeId;
        this.deviceId = deviceId;
        this.logicalId = logicalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        TrendSearchVo that = (TrendSearchVo) o;
        return Objects.equals(dataId, that.dataId) &&
                Objects.equals(dataTypeId, that.dataTypeId) &&
                Objects.equals(deviceId, that.deviceId) &&
                Objects.equals(logicalId, that.logicalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataId, dataTypeId, deviceId, logicalId);
    }
}
