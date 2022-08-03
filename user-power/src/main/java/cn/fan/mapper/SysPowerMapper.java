package cn.fan.mapper;

import cn.fan.pojo.SysPower;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysPowerMapper {

    @Select(" select * from sys_power where powername like #{powername} and powerdesc like #{powerdesc} and powerurl like #{powerurl} ")
    List<SysPower> queryLikePower(String powername,String powerdesc,String powerurl);

    @Select(" select * from sys_power where powername = #{powername}  or powerurl = #{powerurl} ")
    List<SysPower> queryPower(String powername,String powerurl);
}
