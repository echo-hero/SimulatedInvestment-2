package cn.fan.dao;

import cn.fan.pojo.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysUserDao extends JpaRepository<SysUser,Integer> {
    SysUser findFirstByUsercode(int usercode);
}
