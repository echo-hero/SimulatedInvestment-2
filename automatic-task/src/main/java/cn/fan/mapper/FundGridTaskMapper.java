package cn.fan.mapper;

import cn.fan.pojo.FundGridTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FundGridTaskMapper {
    @Select(" select * from fund_grid_task where fall_taskid = #{fall_taskid} limit 0,1; ")
    FundGridTask firstByFalltaskid(int fall_taskid);

    @Select(" select * from fund_grid_task where rise_taskid = #{rise_taskid} limit 0,1; ")
    FundGridTask firstByRisetaskid(int rise_taskid);
}
