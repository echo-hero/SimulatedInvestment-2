package cn.fan.dao;

import cn.fan.pojo.UserInvest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInvestDao extends JpaRepository<UserInvest,Integer> {
    List<UserInvest> findByFundcode(String fundCode);
    List<UserInvest> findByUseridAndFundcode(int userId,String fundCode);
}
