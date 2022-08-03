package cn.fan.job;

import cn.fan.dao.FundDataDao;
import cn.fan.mapper.FundDataMapper;
import cn.fan.pojo.FundData;
import cn.fan.service.EmailRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailRemindJob {

    @Autowired
    EmailRecordService emailRecordService;

    @Autowired
    FundDataMapper fundDataMapper;

    @Scheduled(cron = "${myJobCron.emailRemindJob}")
    protected void executeInternal(){
        System.out.println("邮件提醒开始！");
        List<FundData> fundDataList=fundDataMapper.findNewIndex();
        for(FundData fundData:fundDataList){
            emailRecordService.remind(fundData);
        }
        System.out.println("邮件提醒结束！");
    }
}
