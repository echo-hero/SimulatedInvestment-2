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

    public List<UserInvest> findInvByUseridAndFundCode(int userid,String fundCode){
        return userInvestDao.findByUseridAndFundcode(userid,fundCode);
    }

    public String addUserInvest(UserInvest userInvest) {
        List<FundMain> fundMainList=fundMainDao.findByFundcode(userInvest.getFundcode());
        if (fundMainList.size()==0)
            return "基金不存在,不允许添加投资清空!";

        FundMain fundMain=fundMainList.get(0);
        FundData fundData=fundDataMapper.firstRecentlyByFundCodeAndFunddate(userInvest.getFundcode(),userInvest.getOperate_date());

        if(null!=fundData){
            userInvest.setDwjz(fundData.getDwjz());
        }else{
            userInvest.setDwjz(0);
        }

        setInvPartAttr(userInvest,fundMain);
        userInvest.setUserid(123456);
        userInvest.setCreate_date(new Date());
        userInvestDao.save(userInvest);
        fresh(userInvest.getFundcode());

        return "新增成功!";
    }

    public UserInvest getInvById(int id){
        return userInvestDao.findById(id).get();
    }

    public String editInv(UserInvest userInvest){
        List<FundMain> fundMainList=fundMainDao.findByFundcode(userInvest.getFundcode());
        if (fundMainList.size()==0 ||null==userInvestDao.findById(userInvest.getId()))
            return "基金不存在,请刷新后重试!";

        FundMain fundMain=fundMainList.get(0);
        UserInvest invest=userInvestDao.findById(userInvest.getId()).get();
        userInvest.setUserid(invest.getUserid());
        userInvest.setCreate_date(invest.getCreate_date());
        userInvest.setDwjz(invest.getDwjz());

        setInvPartAttr(userInvest,fundMain);
        userInvest.setUpdate_date(new Date());

        userInvestDao.save(userInvest);
        fresh(userInvest.getFundcode());
        return "修改成功!";
    }

    private void setInvPartAttr(UserInvest userInvest,FundMain fundMain){
        if(userInvest.getDwjz()!=0){
            if(userInvest.getShare()==0){
                float share=userInvest.getSum_money() / (1+fundMain.getCommission_rate()/100) / userInvest.getDwjz();
                userInvest.setShare(share);
            }else if(userInvest.getSum_money()==0){
                float summoney=userInvest.getDwjz() * userInvest.getShare() * (1+fundMain.getCommission_rate()/100);
                userInvest.setSum_money(summoney);
            }

            float com=userInvest.getSum_money()-userInvest.getDwjz()*userInvest.getShare();
            userInvest.setCommission(com);
        }else{//单位净值为0的数据,做自动任务处理 也可新增一张表来记录需要跑自动任务的数据
            userInvest.setCommission(0);
        }
    }

}
