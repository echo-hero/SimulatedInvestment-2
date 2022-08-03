package cn.fan.service;

import cn.fan.dao.FundDataDao;
import cn.fan.mapper.FundDataMapper;
import cn.fan.pojo.FundData;
import cn.fan.pojo.FundMain;
import cn.fan.util.MyUrlUtil;
import cn.fan.util.SpringContextUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@CacheConfig(cacheNames="fundDatas")
public class FundDataService {

    @Value("${customUrl.synchroUrl}")
    private String synchroUrl;

    @Value("${customUrl.thirdUrl}")
    private String thirdUrl;

    @Autowired
    FundDataDao fundDataDao;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FundDataMapper fundDataMapper;

    private Map<String,List<FundData>> stringListMap=new HashMap<>();

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }

    @CacheEvict(key="'funData-code'+ #p0")
    public void removeFund(String fundcode){//清空单个基金缓存
    }

    @Cacheable(key="'funData-code-'+ #p0")
    public List<FundData> get(String fundcode){
        if(null==fundcode)
            return null;

        if(CollUtil.toList().size()==0)
            return fresh(fundcode);
        else
            return CollUtil.toList();
    }

    @Cacheable(key="'funData-code-'+ #p0")
    public List<FundData> store(String fundcode){
        return stringListMap.get(fundcode);
    }

//    @HystrixCommand(fallbackMethod = "third_part_not_connected")    //阻断器
    public List<FundData> fresh(String fundcode){

        List<FundData> fundDataList=thirdParty(fundcode);
        addSJK(fundDataList);

        List<FundData> allFund=fundDataDao.findByFundcode(fundcode);
        stringListMap.put(fundcode,allFund);

        FundDataService fundDataService= SpringContextUtil.getBean(FundDataService.class);
        fundDataService.removeFund(fundcode);
        return fundDataService.store(fundcode);
    }

    public List<FundData> third_part_not_connected(){
        List<FundData> fundDataList=new ArrayList<>();
        FundData fundData=new FundData();
        fundData.setFundcode("000000");
        fundData.setFunddate("2000-01-01");
        fundData.setDwjz(0);
        fundData.setCreate_date(new Date());
        fundDataList.add(fundData);
        return fundDataList;
    }

    private List<FundData> thirdParty(String fundcode){
        String urlStr=thirdUrl+fundcode;
        String data= MyUrlUtil.getUrlStr(urlStr).replace("</td>","@</td>").replace("</th>","@</th>");

        //去除HTML格式
        String regEx_html = "<[^>]+>";
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(data);
        data = m_html.replaceAll("");

        if(data.contentEquals("暂无数据") || data.length()<180){
            return spare(fundcode);
        }else{
            int start=data.indexOf("分红送配")+5;
            int end=data.indexOf("records");
            String temp=data.substring(start,end);
            return transform(temp,fundcode);
        }
    }

    //备用接口，只能获取最新一条数据
    private List<FundData> spare(String fundcode){
        String urlStr="http://fundgz.1234567.com.cn/js/"+fundcode+".js?rt=1463558676006";
        String data=MyUrlUtil.getUrlStr(urlStr);
        //如果该接口无数据，则使用备用接口
        if(data.length()<60){
            return null;
        }
        String temp=data.substring(9,data.indexOf("});"));
        return spareTransform(temp);
    }

    //主接口的数据转换
    private List<FundData> transform(String temp,String fundcode) {
        List<FundData> fundDataList=new ArrayList<>();
        String[] tempList=temp.split("@");
        for(int i=0;i+1<tempList.length;i+=7){
            try {
                FundData fundData = new FundData();
                fundData.setFundcode(fundcode);

                fundData.setFunddate(tempList[i]);

                fundData.setDwjz(Float.parseFloat(tempList[i + 1]));
                fundData.setCreate_date(new Date());
                fundDataList.add(fundData);
            }catch (Exception e){            }
        }

        return fundDataList;
    }

    //备用接口的数据转换
    private List<FundData> spareTransform(String temp) {
        List<FundData> fundDataList=new ArrayList<>();
        String[] tempList=temp.split("\"");
        String[] temps=new String[7];
        for(int i=1;i<tempList.length;i++){
            if(i%4==3){
                temps[i/4]=tempList[i];
            }
        }
        try {
            FundData fundData = new FundData();
            fundData.setFundcode(temps[0]);

            fundData.setFunddate(tempList[2]);

            fundData.setDwjz(Float.parseFloat(tempList[3]));
            fundData.setCreate_date(new Date());
            fundDataList.add(fundData);
        }catch (Exception e){}

        return fundDataList;
    }

    private void addSJK(List<FundData> fundDataList){
        for(FundData fundData:fundDataList){
            if("000000".equals(fundData.getFundcode()))
                continue;
            if( null==fundDataMapper.firstByFundcodeAndFunddate(fundData.getFundcode(),fundData.getFunddate()) ){
                fundDataDao.save(fundData);

                //添加数据库后,执行一次邮箱提醒任务
                EmailRecordService emailRecordService=SpringContextUtil.getBean(EmailRecordService.class);
                emailRecordService.remind(fundData);
            }
        }

    }


    //同步单个基金数据
    public void synchro(String fundCode){
        String urlStr = synchroUrl + fundCode;
        JSONArray jsonArray=restTemplate.getForObject(urlStr, JSONObject.class).getJSONObject("data").getJSONArray("netWorthData");
        for(int i=0;i<jsonArray.size();i++){
            jsonArray.get(i).toString();
            JSONArray jsonObject=(JSONArray)jsonArray.get(i);
            //将未插入的数据添加到数据库中
            if(null==fundDataMapper.firstByFundcodeAndFunddate(fundCode,jsonObject.get(0).toString())){
                FundData fundData=new FundData();
                fundData.setFundcode(fundCode);
                fundData.setFunddate(jsonObject.get(0).toString());
                fundData.setDwjz(Float.parseFloat(jsonObject.get(1).toString()));
                fundData.setCreate_date(new Date());
                fundDataDao.save(fundData);
            }
        }
        fresh(fundCode);
    }

    //同步所有数据
    public void synchros(List<FundMain> fundMainList){
        for(FundMain fundMain:fundMainList){
            synchro(fundMain.getFundcode());
        }
    }

}
