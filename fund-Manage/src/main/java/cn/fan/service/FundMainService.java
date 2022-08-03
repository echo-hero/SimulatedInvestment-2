package cn.fan.service;

import cn.fan.dao.FundMainDao;
import cn.fan.pojo.FundMain;
import cn.fan.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames="fundMains")
public class FundMainService {

    private List<FundMain> fundMains=new ArrayList<>();

    @Autowired
    FundMainDao fundMainDao;

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }

    @CacheEvict(key="'funMains'")
    public void removeMains(){
    }

    @Cacheable(key="'funMains'")
    public List<FundMain> getAll(){
        if(null==fundMains || fundMains.size()==0)
            return freshAll();
        else
            return fundMains;
    }

    @Cacheable(key="'funMains'")
    public List<FundMain> storeAll(){
        return fundMains;
    }

    public List<FundMain> freshAll1(){
        fundMains=fundMainDao.findAll();
        System.out.println("freshAll");
        FundMainService fundMainService= SpringContextUtil.getBean(FundMainService.class);
        fundMainService.removeMains();
        return fundMainService.storeAll();
    }

    public List<FundMain> freshAll(){
        fundMains=fundMainDao.findAll();

        FundMainService fundMainService= SpringContextUtil.getBean(FundMainService.class);
        fundMainService.removeMains();
        return fundMainService.storeAll();
    }

    public String addFundMain(FundMain fundMain){
        if(null!=fundMainDao.findByFundcode(fundMain.getFundcode()))
            return "基金已存在，不允许重复添加！";

        fundMain.setCreate_date(new Date());
        fundMainDao.save(fundMain);

        return "基金【"+fundMain.getFundname()+"】添加成功，历史数据正在后台同步中！";
    }

}
