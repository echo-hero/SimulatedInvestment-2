package cn.fan.service;

import cn.fan.dao.FundMainDao;
import cn.fan.dao.UserInvestDao;
import cn.fan.mapper.FundDataMapper;
import cn.fan.pojo.FundData;
import cn.fan.pojo.FundMain;
import cn.fan.pojo.UserInvest;
import cn.fan.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames="userInvests")
public class UserInvestService {

    private Map<String,List<UserInvest>> stringListMap=new HashMap<>();

    @Autowired
    UserInvestDao userInvestDao;

    @Autowired
    FundDataMapper fundDataMapper;

    @Autowired
    FundMainDao fundMainDao;

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }

    //123456为用户id,后续进行调整
    @CacheEvict(key="'userInvest-123456-'+ #p0")
    public void removeInv(String fundcode){
    }

    @Cacheable(key="'userInvest-123456-'+ #p0")
    public List<UserInvest> get(String fundcode){
        if(null==fundcode)
            return null;

        if( !stringListMap.containsKey(fundcode) )
            return fresh(fundcode);
        else
            return stringListMap.get(fundcode);
    }

    @Cacheable(key="'userInvest-123456-'+ #p0")
    public List<UserInvest> store(String fundcode){
        return stringListMap.get(fundcode);
    }

    public List<UserInvest> fresh(String fundcode){

        List<UserInvest> userInvestList=userInvestDao.findByFundcode(fundcode);
        stringListMap.put(fundcode,userInvestList);

        UserInvestService userInvestService= SpringContextUtil.getBean(UserInvestService.class);
        userInvestService.removeInv(fundcode);
        return userInvestService.store(fundcode);
    }

    public void getFundDWJZ(UserInvest userInvest){
        FundData fundData=fundDataMapper.firstRecentlyByFundCodeAndFunddate(userInvest.getFundcode(),userInvest.getOperate_date());
        FundMain fundMain=fundMainDao.findFirstByFundcode(userInvest.getFundcode());

        if(null==fundData || null==fundMain)
            return;

        userInvest.setDwjz(fundData.getDwjz());

        if(userInvest.getShare()==0){
            float share=userInvest.getSum_money() / (1+fundMain.getCommission_rate()/100) / userInvest.getDwjz();
            userInvest.setShare(share);
        }else if(userInvest.getSum_money()==0){
            float sumMoney=userInvest.getDwjz() * userInvest.getShare() * (1+fundMain.getCommission_rate()/100);
            userInvest.setSum_money(sumMoney);
        }

        float com=userInvest.getSum_money()-userInvest.getDwjz()*userInvest.getShare();
        userInvest.setCommission(com);
        userInvest.setUpdate_date(new Date());
        userInvestDao.save(userInvest);

        fresh(userInvest.getFundcode());
    }

}
