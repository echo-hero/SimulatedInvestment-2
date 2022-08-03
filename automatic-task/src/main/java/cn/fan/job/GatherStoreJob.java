package cn.fan.job;

import cn.fan.pojo.FundMain;
import cn.fan.service.FundDataService;
import cn.fan.service.FundMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GatherStoreJob {

    @Autowired
    FundMainService fundMainService;

    @Autowired
    FundDataService fundDataService;

    @Scheduled(cron = "${myJobCron.gatherStoreJob}")
    protected void executeInternal(){
        System.out.println("数据采集开始！");
        List<FundMain> fundMainList=fundMainService.getAll();
        for(FundMain fundMain:fundMainList){
            fundDataService.fresh(fundMain.getFundcode());
        }
        System.out.println("数据采集结束！");
    }

}
