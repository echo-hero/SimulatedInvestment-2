package cn.fan.service;

import cn.fan.dao.FundMainDao;
import cn.fan.pojo.FundMain;
import cn.fan.util.SpringContextUtil;
import cn.hutool.core.collection.CollUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames="fundMains")
public class FundMainService {

    @Value("${customUrl.fundMainUrl}")
    private String fundMainUrl;

    @Value("${customUrl.allFundMainUrl}")
    private String allFundMainUrl;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FundMainDao fundMainDao;

    private Map<String,List<FundMain>> stringListMap=new HashMap<>();
    private List<FundMain> fundMains=new ArrayList<>();

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }

    @CacheEvict(key="'funMain-code'+ #p0")
    public void removeFund(String fundcode){
    }

    @Cacheable(key="'funMain-code-'+ #p0")
    public List<FundMain> get(String fundcode){
        if(null==fundcode)
            return null;

        if(CollUtil.toList().size()==0)
            return fresh(fundcode);
        else
            return CollUtil.toList();
    }

    @Cacheable(key="'funMain-code-'+ #p0")
    public List<FundMain> store(String fundcode){
        return stringListMap.get(fundcode);
    }

//    @HystrixCommand(fallbackMethod = "fresh_hystrixCommand")
    public List<FundMain> fresh(String fundcode){
//        String url=fundMainUrl+fundcode;
//        List<FundMain> fundMainList=restTemplate.getForObject(url,List.class);
//        stringListMap.put(fundcode,fundMainList);
//
//        FundMainService fundMainService= SpringContextUtil.getBean(FundMainService.class);
//        fundMainService.remove();
//        return fundMainService.store(fundcode);

        List<FundMain> fundMainList=fundMainDao.findByFundcode(fundcode);
        stringListMap.put(fundcode,fundMainList);

        FundMainService fundMainService= SpringContextUtil.getBean(FundMainService.class);
        fundMainService.remove();
        return fundMainService.store(fundcode);
    }

//    public List<FundMain> fresh_hystrixCommand(String fundcode){
//        System.out.println("阻断器单个实例查询");
//        List<FundMain> fundMainList=fundMainDao.findByFundcode(fundcode);
//        stringListMap.put(fundcode,fundMainList);
//
//        FundMainService fundMainService= SpringContextUtil.getBean(FundMainService.class);
//        fundMainService.remove();
//        return fundMainService.store(fundcode);
//    }


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

//    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<FundMain> freshAll(){
//        fundMains=restTemplate.getForObject(allFundMainUrl,List.class);
//
//        FundMainService fundMainService= SpringContextUtil.getBean(FundMainService.class);
//        fundMainService.removeMains();
//        return fundMainService.getAll();
        fundMains=fundMainDao.findAll();

        FundMainService fundMainService= SpringContextUtil.getBean(FundMainService.class);
        fundMainService.removeMains();
        return fundMainService.storeAll();
    }

//    public List<FundMain> third_part_not_connected(){
//        fundMains=fundMainDao.findAll();
//
//        FundMainService fundMainService= SpringContextUtil.getBean(FundMainService.class);
//        fundMainService.removeMains();
//        return fundMainService.getAll();
//    }

    public List<FundMain> getMainByFundCode(String fundCode){
        return fundMainDao.findByFundcode(fundCode);
    }

}
