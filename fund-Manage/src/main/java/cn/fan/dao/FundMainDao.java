package cn.fan.dao;

import cn.fan.pojo.FundMain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FundMainDao extends JpaRepository<FundMain,Integer> {
    List<FundMain> findByFundcode(String fundCode);
}
