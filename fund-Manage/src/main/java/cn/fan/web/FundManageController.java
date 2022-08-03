package cn.fan.web;

import cn.fan.pojo.*;
import cn.fan.service.*;
import cn.fan.util.Page4Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FundManageController {

    @Autowired
    FundMainService fundMainService;

    @Autowired
    FundDataService fundDataService;

    @Autowired
    UserInvestService userInvestService;

    @Autowired
    TaskRemindService taskRemindService;

    @Autowired
    SessionService sessionService;

    @GetMapping("/fundMains")
//    @ResponseBody
    @CrossOrigin
    public List<FundMain> fundMains() throws Exception {//@CookieValue("JSESSIONID") String cookie
//        try{
//
//            System.out.println("cookie:"+cookie);
//            Map<String,Integer> sessionList=sessionService.getList();
//            int userCode=sessionList.get(cookie);
//
//            Map<String,Object> map=sessionService.get(userCode);
//            System.out.println("date:"+(Date)map.get("date"));
//
//        }catch (Exception e){
//            System.out.println("查询失败！");
//        }
        return fundMainService.getAll();
    }

    @GetMapping("/fundMains/{start}/{size}")
    @CrossOrigin
    public Page4Util<FundMain> fundMains(@PathVariable("start") int start,@PathVariable("size") int size) throws Exception {
        List<FundMain> fundMainList=fundMainService.getAll();

        int navigatePages=5;//默认前端显示5个页数
        Page4Util<FundMain> page4Util=new Page4Util<>(start,size,navigatePages);
        page4Util.page(fundMainList);
        return  page4Util;
    }

    @PostMapping("/addFundMain")
    @CrossOrigin
    public String addFundMain(@RequestBody FundMain fundMain) throws Exception{
        try{
            String res=fundMainService.addFundMain(fundMain);

            //多线程后台跑一下历史数据
            Thread thread=new Thread(){
                public void run(){
                    System.out.println("开始同步："+fundMain.getFundname());
                    String str=fundDataService.synchro(fundMain.getFundcode());
                    System.out.println("同步结果："+str);
                }
            };
            thread.start();

            return res;
        }catch (Exception e) {
            return "添加失败";
        }
    }

    @GetMapping("/overview")
    @CrossOrigin
    public List<Map<String,Object>> overview(){
        List<FundMain> fundMainList=fundMainService.getAll();
        return fundDataService.overview(fundMainList);
    }

    @GetMapping("/userInvests/{fundCode}/{start}/{size}")
    @CrossOrigin
    public Page4Util<UserInvest> userInvests(@PathVariable("fundCode") String fundCode,@PathVariable("start") int start,@PathVariable("size") int size) throws Exception {
        int userid=123456;
        List<UserInvest> userInvestList=userInvestService.findInvByUseridAndFundCode(userid,fundCode);

        int navigatePages=5;//默认前端显示5个页数
        Page4Util<UserInvest> page4Util=new Page4Util<>(start,size,navigatePages);
        page4Util.page(userInvestList);
        return  page4Util;
    }

    @PostMapping("/addInvest")
    @CrossOrigin
    public String addInvest(@RequestBody UserInvest userInvest) throws Exception{
        try {
            return  userInvestService.addUserInvest(userInvest);
        }catch (Exception e){
            return "新增失败!";
        }
    }

    @GetMapping("/getInvById/{id}")
    @CrossOrigin
    public UserInvest getInvById(@PathVariable("id") int id) throws Exception{
        return userInvestService.getInvById(id);
    }

    @PostMapping("/editInv")
    @CrossOrigin
    public String editInv(@RequestBody UserInvest userInvest) throws Exception{
        return userInvestService.editInv(userInvest);
    }

    @PostMapping("/getTaskRemind/{start}/{size}")
    @CrossOrigin
    public Page4Util<TaskRemind> getTaskRemind(@RequestBody TaskRemind taskRemind, @PathVariable("start") int start, @PathVariable("size") int size) throws Exception{
        int userid=123456;
        List<TaskRemind> taskRemindList=taskRemindService.selectPartCondition(userid,taskRemind);

        int navigatePages=5;//默认前端显示5个页数
        Page4Util<TaskRemind> page4Util=new Page4Util<>(start,size,navigatePages);
        page4Util.page(taskRemindList);
        return  page4Util;
    }

    @PostMapping("/addTaskRemind")
    @CrossOrigin
    public String addTaskRemind(@RequestBody TaskRemind taskRemind) throws Exception{
        try {
            int userId=123546;
            return  taskRemindService.addTaskRemind(userId,taskRemind);
        }catch (Exception e){
            return "新增失败!";
        }
    }

    @GetMapping("/getTask/{id}")
    @CrossOrigin
    public TaskRemind getTask(@PathVariable("id") int id) throws Exception{
        return taskRemindService.getRemind(id);
    }

    @PostMapping("/editTask")
    @CrossOrigin
    public String editTask(@RequestBody TaskRemind taskRemind) throws Exception{
        return taskRemindService.editTask(taskRemind);
    }

    @GetMapping("/simulate/{fundCode}/{startDate}/{endDate}/{ma}/{buyThreshold}/{sellThreshold}")
    @CrossOrigin
    public Map<String,Object> simulate(@PathVariable("fundCode") String fundCode,
                                       @PathVariable("startDate") String startDate,
                                       @PathVariable("endDate") String endDate,
                                       @PathVariable("ma") int ma,
                                       @PathVariable("buyThreshold") float buyThreshold,
                                       @PathVariable("sellThreshold") float sellThreshold) throws Exception{

        Map<String,Object> result=new HashMap<>();
        List<FundData> fundDataList=fundDataService.get(fundCode);

        List<FundData> fundDatas=fundDataService.ByDateRange(fundDataList,startDate,endDate);
        String indexStartDate=fundDatas.get(0).getFunddate();
        String indexEndDate=fundDatas.get(fundDatas.size()-1).getFunddate();

        result.put("fundDatas",fundDatas);
        result.put("indexStartDate",indexStartDate);
        result.put("indexEndDate",indexEndDate);

        fundDataService.simulate(fundDatas,ma,buyThreshold,sellThreshold,result);
        return result;
    }

    @GetMapping("/grid/{fundCode}/{startDate}/{endDate}/{gridWidth}/{initAmount}/{initShare}")
    @CrossOrigin
    public Map<String,Object> grid(@PathVariable("fundCode") String fundCode,
                                   @PathVariable("startDate") String startDate,
                                   @PathVariable("endDate") String endDate,
                                   @PathVariable("gridWidth") float gridWidth,
                                   @PathVariable("initAmount") float initAmount,
                                   @PathVariable("initShare") int initShare) throws Exception{

        Map<String,Object> result=new HashMap<>();

        List<FundData> fundDataList=fundDataService.get(fundCode);
        List<FundData> fundDatas=fundDataService.ByDateRange(fundDataList,startDate,endDate);

        List<Trade> gridList=fundDataService.grid(fundDatas,gridWidth,initAmount,initShare);


        float perGridAmount=1000;//每网投资金额
        Map<String,Object> gridStatistics=fundDataService.gridStat(gridList,perGridAmount);

        float years=fundDataService.getYear(fundDatas);
        float rateProfit=(float)gridStatistics.get("rateProfit");
        double rateAnnual=Math.pow(1+rateProfit, 1/years) - 1;
        gridStatistics.put("rateAnnual",rateAnnual);

        result.put("gridList",gridList);
        result.put("gridStatistics",gridStatistics);
        return result;
    }

    @GetMapping("/castSurely/{fundCode}/{startDate}/{endDate}/{castProfitRate}/{castCycle}/{money}")
    @CrossOrigin
    public Map<String,Object> castSurely(@PathVariable("fundCode") String fundCode,
                           @PathVariable("startDate") String startDate,
                           @PathVariable("endDate") String endDate,
                           @PathVariable("castProfitRate") float castProfitRate,
                           @PathVariable("castCycle") int castCycle,
                           @PathVariable("money") float money){

        List<FundData> fundDataList=fundDataService.get(fundCode);
        List<FundData> fundDatas=fundDataService.ByDateRange(fundDataList,startDate,endDate);

        return fundDataService.castSurely(fundDatas,castProfitRate,castCycle,money);
    }

}
