package cn.fan.dao;

import cn.fan.pojo.SysPower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SysPowerDao extends JpaRepository<SysPower,Integer> {
    List<SysPower> findByPowername(String powerName);
    List<SysPower> findByPowerurl(String powerUrl);
}
