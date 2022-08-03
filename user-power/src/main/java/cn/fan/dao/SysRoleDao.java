package cn.fan.dao;

import cn.fan.pojo.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SysRoleDao extends JpaRepository<SysRole,Integer> {
    List<SysRole> findByRolename(String roleName);
}
