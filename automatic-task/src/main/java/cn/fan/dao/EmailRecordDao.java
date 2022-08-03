package cn.fan.dao;

import cn.fan.pojo.EmailRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRecordDao extends JpaRepository<EmailRecord,Integer>{
}
