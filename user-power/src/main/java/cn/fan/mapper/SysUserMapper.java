package cn.fan.mapper;

import cn.fan.pojo.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper {

    @Select(" select id, usercode, username, emailadd, create_date, last_login_date from sys_user where userName like #{userName} and emailadd like #{emailadd} ")
    List<SysUser> queryUser(String userName,String emailadd);
}
