package cn.fan.mapper;

import cn.fan.pojo.UserInvest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserInvestMapper {
    @Select(" select * from user_invest where dwjz=0 ")
    List<UserInvest> findIncomplete();
}
