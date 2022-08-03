package cn.fan.dao;

import cn.fan.pojo.SysRolePower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SysRolePowerDao extends JpaRepository<SysRolePower,Integer> {
    List<SysRolePower> findByRoleid(int RoleId);
}
