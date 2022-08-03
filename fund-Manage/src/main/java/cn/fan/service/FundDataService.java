package cn.fan.service;


import cn.fan.dao.FundDataDao;
import cn.fan.dao.FundMainDao;
import cn.fan.dao.UserInvestDao;
import cn.fan.mapper.FundDataMapper;
import cn.fan.pojo.FundData;
import cn.fan.pojo.FundMain;
import cn.fan.pojo.Trade;
import cn.fan.pojo.UserInvest;
import cn.fan.util.SpringContextUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
@CacheConfig(cacheNames="fundDatas")
public class FundDataService {

    @Value("${interfaceUrl.synchroUrl}")
    private String synchroUrl;

    @Autowired
    FundMainDao fundMainDao;

    @Autowired
    FundDataDao fundDataDao;

    @Autowired
    UserInvestDao userInvestDao;

    @Autowired
    FundDataMapper fundDataMapper;

    @Autowired
    RestTemplate restTemplate;

    private Map<String,List<FundData>> stringListMap=new HashMap<>();

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

    public List<FundData> fresh(String fundcode){
        List<FundData> allFund=fundDataMapper.findByFundcode(fundcode);
        stringListMap.put(fundcode,allFund);

        FundDataService fundDataService= SpringContextUtil.getBean(FundDataService.class);
        fundDataService.removeFund(fundcode);
        return fundDataService.store(fundcode);
    }



    public String synchro(String fundCode){
        String url=synchroUrl+fundCode;
        String res=restTemplate.getForObject(url,String.class);
        return res;
    }



    public List<Map<String,Object>> overview(List<FundMain> fundMainList){
        List<Map<String,Object>> result=new ArrayList<>();

        float indexSum_sumMoney=0;
        float indexSum_holdSum=0;
        float indexSum_profit=0;

        for(FundMain fundMain:fundMainList){
            Map<String,Object> map=new HashMap<>();
            float sumMoney=0;//投资总金额*
            float sellMoney=0;
            float holdSum=0;//持有总金额*
            float shareSum=0;//总投资份额*
            float shellShare=0;//卖出的份额
            float holdShare=0;//持有份额*
            float buyAVG=0;//买入均价*
            float holdAVG=0;//持有均价*
            float nowDwjz=0;//市值*(最新单位净值)
            float profit=0;//总亏盈*

            FundData newFunData=fundDataMapper.firstNewByFundCode(fundMain.getFundcode());
            nowDwjz=newFunData.getDwjz();

            List<UserInvest> userInvestList=userInvestDao.findByFundcode(fundMain.getFundcode());
            if(userInvestList.size()==0)
                continue;

            for(UserInvest userInvest:userInvestList){
                if(userInvest.getBuysell_flag()==0){
                    sumMoney+=userInvest.getSum_money();
                    shareSum+=userInvest.getShare();
                }else{
                    sellMoney+=userInvest.getSum_money();
                    shellShare+=userInvest.getShare();
                }
            }

            holdShare=shareSum-shellShare;
            holdSum=holdShare*nowDwjz;
            profit=holdSum-sumMoney+sellMoney;
            buyAVG=sumMoney/shareSum;
            holdAVG=(sumMoney-sellMoney)/holdShare;


            map.put("fundMain",fundMain);
            map.put("sumMoney",sumMoney);
            map.put("holdSum",holdSum);
            map.put("shareSum",shareSum);
            map.put("holdShare",holdShare);
            map.put("buyAVG",buyAVG);
            map.put("holdAVG",holdAVG);
            map.put("nowDwjz",nowDwjz);
            map.put("profit",profit);
            result.add(map);

            indexSum_sumMoney+=sumMoney;
            indexSum_holdSum+=holdSum;
            indexSum_profit+=profit;
        }

        //汇总
        Map<String,Object> indexSum=new HashMap<>();
        FundMain mainSum=new FundMain();
        mainSum.setFundcode("");
        mainSum.setFundname("汇总");
        indexSum.put("fundMain",mainSum);
        indexSum.put("sumMoney",indexSum_sumMoney);
        indexSum.put("holdSum",indexSum_holdSum);
        indexSum.put("shareSum","");
        indexSum.put("holdShare","");
        indexSum.put("buyAVG","");
        indexSum.put("holdAVG","");
        indexSum.put("nowDwjz","");
        indexSum.put("profit",indexSum_profit);
        result.add(indexSum);

        return result;
    }


    //时间区间内的基金数据
    public List<FundData> ByDateRange(List<FundData> fundDataList,String strStartDate,String strEndDate){
        if(StrUtil.isBlankOrUndefined(strStartDate) || StrUtil.isBlankOrUndefined(strEndDate) )
            return fundDataList;

        List<FundData> result=new ArrayList<>();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);

        for(FundData fundData:fundDataList){
            Date date=DateUtil.parse(fundData.getFunddate());
            if(date.getTime()>=startDate.getTime() &&
                    date.getTime()<=endDate.getTime()){
                result.add(fundData);
            }
        }
        return  result;
    }

    public void simulate(List<FundData> fundDatas,int ma,float buyThreshold,float sellThreshold,Map<String,Object> result){
        float initCash = 1000;
        float init=fundDatas.get(0).getDwjz();
        float cash = initCash;
        float share=0;

        float[] moveAvgs=new float[fundDatas.size()];
        float[] mals=new float[ma];

        float[] profitList=new float[fundDatas.size()];
        List<Trade> tradeList=new ArrayList<>();

        Map<String,Object> profitGlance=new HashMap<>();

        //盈利次数、盈利率、亏损次数、亏损率
        Map<String,Object> tradingStatistics=new HashMap<>();
        int winCount = 0;
        float totalWinRate = 0;
        int lossCount = 0;
        float totalLossRate = 0;

        for (int i=0;i<fundDatas.size();i++){
            //移动均线
            float nowDwjz=fundDatas.get(i).getDwjz();
            mals[i%ma]=nowDwjz;
            float avg=0;//平均值
            int m=0;//平均的天数
            float maSum=0;
            for(float d:mals){
                if(d!=0){
                    m++;
                    maSum+=d;
                }
            }
            if(m!=0)
                avg=maSum/m;

            moveAvgs[i]=avg;

            //收益率集合

            if(i > ma){
                float sectionMax= getMax(mals);//ma区间内的最大值
                float increase_rate = nowDwjz/avg;
                float decrease_rate = nowDwjz/sectionMax;

                if(increase_rate>buyThreshold){//超过买入比例
                    if(0==share){//且还未买入，则购买
                        share=cash/nowDwjz;
                        cash=0;

                        //新增一条交易记录
                        tradeList.add(getTradeByIndex(fundDatas.get(i)));
                    }
                }else if(decrease_rate<sellThreshold){//低于卖点
                    if(0!=share){//且未卖，则出售
                        cash=nowDwjz*share;
                        share=0;

                        //修改交易记录的出售信息
                        Trade trade=tradeList.get(tradeList.size()-1);
                        trade.setSellDate(fundDatas.get(i).getFunddate());
                        trade.setSellClosePoint(nowDwjz);
                        trade.setRate(cash/initCash);

                        //计算交易统计相关数据
                        if(trade.getSellClosePoint()-trade.getBuyClosePoint()>0) {
                            totalWinRate +=(trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            winCount++;
                        }
                        else {
                            totalLossRate +=(trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            lossCount ++;
                        }
                    }
                }
            }
            float value=0;//目前的所有金额（买入的份额按照当日金额转换）
            if(share!=0)
                value=nowDwjz*share;
            else
                value=cash;
            float rate=value/initCash;//当前盈利率

            if(i-1<ma){
                profitList[i]=nowDwjz;
            }else{
                profitList[i]=rate*init;
            }

        }

        float avgWinRate = totalWinRate / winCount;
        float avgLossRate = totalLossRate / lossCount;
        tradingStatistics.put("winCount",winCount);
        tradingStatistics.put("lossCount",lossCount);
        tradingStatistics.put("avgWinRate",avgWinRate);
        tradingStatistics.put("avgLossRate",avgLossRate);


        float years=getYear(fundDatas);
        float indexIncomeTotal = (fundDatas.get(fundDatas.size()-1).getDwjz() - fundDatas.get(0).getDwjz()) / fundDatas.get(0).getDwjz();
        float indexIncomeAnnual = (float) Math.pow(1+indexIncomeTotal, 1/years) - 1;
        float trendIncomeTotal = profitList.length>0 ?  (profitList[profitList.length-1] - profitList[0]) / profitList[0]  :0;
        float trendIncomeAnnual = (float) Math.pow(1+trendIncomeTotal, 1/years) - 1;
        profitGlance.put("years",years);
        profitGlance.put("indexIncomeTotal",indexIncomeTotal);
        profitGlance.put("indexIncomeAnnual",indexIncomeAnnual);
        profitGlance.put("trendIncomeTotal",trendIncomeTotal);
        profitGlance.put("trendIncomeAnnual",trendIncomeAnnual);


        result.put("moveAvgs",moveAvgs);
        result.put("profitList",profitList);
        result.put("tradeList",tradeList);
        result.put("profitGlance",profitGlance);
        result.put("tradingStatistics",tradingStatistics);
    }


    public List<Trade> grid(List<FundData> fundDatas,double gridWidth,double initAmount,double initShare){
        double lastAmount=initAmount;//最后一次买入金额网格线
        double waveAmount=initAmount*gridWidth;//波动金额

        double highestDwjz=initAmount+waveAmount*(initShare+1);
        System.out.println("highestDwjz:"+highestDwjz);

        List<Trade> tradeList=new ArrayList<>();//交易集合

        //建仓
        int build=0;//建仓的天数
        for(int i=0;i<fundDatas.size();i++){
            if(fundDatas.get(i).getDwjz()<=lastAmount){
                build=i+1;
                for(int j=0;j<initShare;j++){
                    Trade trade=getTradeByIndex(fundDatas.get(i));
                    tradeList.add(trade);
                }
                break;
            }
        }

        //网格交易
        for(int i=build;i<fundDatas.size();i++){
            FundData fundData=fundDatas.get(i);
            if(fundData.getDwjz()<=lastAmount-waveAmount){//买
                Trade trade=getTradeByIndex(fundDatas.get(i));
                tradeList.add(trade);
                lastAmount-=waveAmount;
            }else if(fundData.getDwjz()>=lastAmount+waveAmount && fundData.getDwjz()<highestDwjz){//卖(金额不能大于网格顶点)
                Trade trade=getUnsold(tradeList);
                if(null!=trade) {
                    trade.setSellDate(fundData.getFunddate());
                    trade.setSellClosePoint(fundData.getDwjz());
                    trade.setRate(fundData.getDwjz() / trade.getBuyClosePoint() - 1);
                    lastAmount+=waveAmount;
                }
            }
        }

        //未交易的网格按照最后一天的金额计算收益（卖出）
        SellAllGrid(tradeList,fundDatas.get(fundDatas.size()-1));

        return tradeList;
    }

    public Map<String,Object> gridStat(List<Trade> gridList,float perGridAmount){
        Map<String,Object> res=new HashMap<>();
        int gridCount=gridList.size();//交易次数
        int completeCount=0;//完成次数
        float completeProfit=0;//完成的收益
        float profitCount=0;//总收益
        float completeRate=0;//完成的收益率
        float rateProfit=0;//总收益率

        float complCost=0;//完成的成本
        float costSum=0;//总成本
        for(Trade trade:gridList){
            float share=perGridAmount/trade.getBuyClosePoint();//份额
            if(trade.getRate()>0){
                completeCount++;
                completeProfit+=(trade.getSellClosePoint()-trade.getBuyClosePoint())*share;
                complCost+=trade.getBuyClosePoint()*share;
            }
            profitCount+=(trade.getSellClosePoint()-trade.getBuyClosePoint())*share;
            costSum+=trade.getBuyClosePoint()*share;
        }

        if(complCost!=0)
            completeRate=completeProfit/complCost;
        if(costSum!=0)
            rateProfit=profitCount/costSum;



        res.put("gridCount",gridCount);
        res.put("completeCount",completeCount);
        res.put("completeProfit",completeProfit);
        res.put("profitCount",profitCount);
        res.put("completeRate",completeRate);
        res.put("rateProfit",rateProfit);
        return res;
    }

    public Map<String,Object> castSurely(List<FundData> fundDatas,float castProfitRate,int castCycle,float money){
        Map<String,Object> result=new HashMap<>();

        int number=0;//定投次数
        float shareSum=0;//总份额
        for(int i=0;i<fundDatas.size();i+=castCycle){
            shareSum += money/fundDatas.get(i).getDwjz();
            number++;

            float dwjz=fundDatas.get(i).getDwjz();
            float nowProfit=shareSum*dwjz-number*money;

            if(castProfitRate<=0 && nowProfit/number/money*100 >= castProfitRate){
                result.put("profit",nowProfit);
                result.put("sellDate",fundDatas.get(i).getFunddate());
                break;
            }

        }

        float nowDwjz=fundDatas.get(fundDatas.size()-1).getDwjz();

        if (!result.containsKey("profit")) {
            float profit = shareSum * nowDwjz - number * money;
            result.put("profit", profit);
        }
        if (!result.containsKey("sellDate"))
            result.put("sellDate","n/a");

        result.put("number",number);
        result.put("shareSum",shareSum);
        result.put("nowDwjz",nowDwjz);
        return result;
    }

    private float getMax(float[] floats){
        if(floats.length==0)
            return 0;

        float res=floats[0];
        for(float f:floats){
            if(res<f)
                res=f;
        }
        return res;
    }
    //获取区间年数（包含非工作日）
    public float getYear(List<FundData> fundDataList) {
        float years;
        String sDateStart = fundDataList.get(0).getFunddate();
        String sDateEnd = fundDataList.get(fundDataList.size()-1).getFunddate();
        Date dateStart = DateUtil.parse(sDateStart);
        Date dateEnd = DateUtil.parse(sDateEnd);
        long days = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
        years = days/365f;
        return years;
    }
    //新增交易记录
    private static Trade getTradeByIndex(FundData fundData){
        Trade trade=new Trade();
        trade.setBuyDate(fundData.getFunddate());
        trade.setBuyClosePoint(fundData.getDwjz());
        trade.setSellDate("n/a");
        trade.setSellClosePoint(0);
        return trade;
    }
    //获取最新一个未卖出的交易记录
    private static Trade getUnsold(List<Trade> tradeList){
        Trade trade=null;
        for(int i=tradeList.size()-1;i>=0;i--){
            if(tradeList.get(i).getSellClosePoint()==0){
                trade=tradeList.get(i);
                break;
            }
        }
        return trade;
    }
    //未交易的网格按照最后一天的金额计算收益（卖出）
    private static void SellAllGrid(List<Trade> tradeList,FundData lastFund){
        for(Trade trade:tradeList){
            if(trade.getSellClosePoint()==0){
                trade.setSellClosePoint(lastFund.getDwjz());
                trade.setSellDate(lastFund.getFunddate());
                trade.setRate(lastFund.getDwjz() / trade.getBuyClosePoint() - 1);
            }
        }
    }
}
