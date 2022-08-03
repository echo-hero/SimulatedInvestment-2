package cn.fan.dao;

import cn.fan.pojo.FundData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface FundDataDao extends JpaRepository<FundData,Integer> {
//    FundData findFirstByFundcodeAndFunddate(String fundCode, Date fundDate);
    List<FundData> findByFundcode(String fundCode);

}
