package cn.fan.web;

import cn.fan.pojo.FundMain;
import cn.fan.service.FundDataService;
import cn.fan.service.FundMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//处理请求
@RestController
public class AutomaticTaskController {

    @Autowired
    FundDataService fundDataService;

    @Autowired
    FundMainService fundMainService;

    @GetMapping("/synchro/{fundCode}")
    public String synchro(@PathVariable("fundCode") String fundCode){
        List<FundMain> fundMainList=fundMainService.getMainByFundCode(fundCode);
        if(fundMainList.size()==0)
            return "未添加该基金，请先进行新增操作！";

        fundDataService.synchro(fundCode);
        return "基金【"+fundMainList.get(0).getFundname()+"】已同步成功！";
    }

    @GetMapping("/synchros")
    public String synchros(){
        try{
            List<FundMain> fundMainList=fundMainService.getAll();
            fundDataService.synchros(fundMainList);
            return "同步成功！！";
        }catch (Exception e){
            return "同步失败！！";
        }

    }

}
