package cn.fan.dao;

import cn.fan.pojo.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SysUserRoleDao extends JpaRepository<SysUserRole,Integer> {
    List<SysUserRole> findByUserid(int userId);
}
