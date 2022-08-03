package cn.fan.job;

import cn.fan.dao.FundMainDao;
import cn.fan.dao.UserInvestDao;
import cn.fan.mapper.FundDataMapper;
import cn.fan.mapper.UserInvestMapper;
import cn.fan.pojo.FundData;
import cn.fan.pojo.FundMain;
import cn.fan.pojo.UserInvest;
import cn.fan.service.FundDataService;
import cn.fan.service.UserInvestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetFundDWJZJob {

    @Autowired
    FundDataMapper fundDataMapper;

    @Autowired
    UserInvestMapper userInvestMapper;

    @Autowired
    FundMainDao fundMainDao;

    @Autowired
    UserInvestDao userInvestDao;

    @Autowired
    UserInvestService userInvestService;

    @Scheduled(cron = "${myJobCron.getFundDWJZJob}")
    protected void executeInternal(){
        List<UserInvest> userInvestList=userInvestMapper.findIncomplete();

        for(UserInvest userInvest:userInvestList){
            userInvestService.getFundDWJZ(userInvest);
        }
    }
}
