package cn.fan.mapper;

import cn.fan.pojo.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper {
    @Select(" select * from sys_role where rolename like #{rolename} and roledesc like #{roledesc} ")
    List<SysRole> queryRole(String rolename,String roledesc);
}
