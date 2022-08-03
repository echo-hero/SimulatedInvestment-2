package cn.fan.service;

import cn.fan.dao.EmailRecordDao;
import cn.fan.dao.FundGridTaskDao;
import cn.fan.dao.TaskRemindDao;
import cn.fan.mapper.FundGridTaskMapper;
import cn.fan.pojo.*;
import cn.fan.util.SendmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class EmailRecordService {

    @Autowired
    TaskRemindDao taskRemindDao;

    @Autowired
    FundMainService fundMainService;

    @Autowired
    EmailRecordDao emailRecordDao;

    @Autowired
    FundGridTaskMapper fundGridTaskMapper;

    @Autowired
    FundGridTaskDao fundGridTaskDao;

    public void remind(FundData fundData){
        List<FundMain> fundMainList=fundMainService.get(fundData.getFundcode());
        if(fundMainList.size()==0)
            return;

        String fundName=fundMainList.get(0).getFundname();
        Date date=new Date();

        List<TaskRemind> taskRemindList=taskRemindDao.findByFundcodeAndIsnotice(fundData.getFundcode(),0);
        for(TaskRemind taskRemind:taskRemindList){
            if( (taskRemind.getUpdown_flag()==0 && taskRemind.getDwjz()>=fundData.getDwjz()) ||
                    (taskRemind.getUpdown_flag()==1 && taskRemind.getDwjz()<=fundData.getDwjz()) ){

                EmailRecord emailRecord=new EmailRecord();
                String theme;
                if(taskRemind.getUpdown_flag()==0)
                    theme="【买入通知】"+fundName+"("+fundData.getFundcode()+")";
                else
                    theme="【卖出通知】"+fundName+"("+fundData.getFundcode()+")";
                emailRecord.setMail_theme(theme);
                emailRecord.setAddressee(taskRemind.getAddressee());
                String content=getContent(fundData,taskRemind,fundName);
                emailRecord.setMail_content(content);
                emailRecord.setUserid(taskRemind.getUserid());
                sendMail(emailRecord);

                if(emailRecord.getIssuccess()==1){
                    taskRemind.setIsnotice(1);
                    taskRemind.setUpdate_date(date);
                    taskRemind.setEmail_id(emailRecord.getId());
                    taskRemindDao.save(taskRemind);

                    //根据网格任务表数据 修改通知任务
                    updateTask(taskRemind,date);
                }
            }
        }

    }

    private String getContent(FundData fundData,TaskRemind taskRemind,String indexName){
        String content="基金【"+indexName+"】单位净值已";
        if(taskRemind.getUpdown_flag()==0)
            content+="跌至("+fundData.getDwjz()+")，" +"等于或低于预期值："+taskRemind.getDwjz()+",当前可进行买入！\n" ;
        else if(taskRemind.getUpdown_flag()==1)
            content+="涨至("+fundData.getDwjz()+")，" +"等于或高预期值："+taskRemind.getDwjz()+",当前可进行卖出！\n" ;

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String updownflag= taskRemind.getUpdown_flag()==0?"跌":"涨";

        content+="\n详细信息如下：" +
                "\n  当前基金信息：" +
                "\n    基金代码：" +fundData.getFundcode()+
                "\n      基金名称：" +indexName+
                "\n      截止日期：" +fundData.getFunddate()+
                "\n      单位净值：" +fundData.getDwjz()+
                "\n      创建时间：" +sdf.format(fundData.getCreate_date())+
                "\n"+
                "\n    任务提醒信息：" +
                "\n      涨跌标志：" +updownflag+
                "\n      单位净值：" +taskRemind.getDwjz()+
                "\n      创建时间：" +sdf.format(taskRemind.getCreate_date())+
                "\n";
        return content;
    }

    public void sendMail(EmailRecord emailRecord){
        emailRecord.setCreate_date(new Date());
        try {
            SendmailUtil.sendEmail(emailRecord.getAddressee(), emailRecord.getMail_theme(), emailRecord.getMail_content());
            emailRecord.setIssuccess(1);
        }catch (Exception e){
            emailRecord.setIssuccess(0);
            emailRecord.setFailure_cause(e.toString().substring(0,299));
        }
        emailRecordDao.save(emailRecord);
    }

    //更新网格任务信息，修改（未触发的一条）任务提醒，新增任务提醒
    private void updateTask(TaskRemind taskRemind,Date date){
        FundGridTask fundGridTask;
        TaskRemind edittaskRemind;
        if(taskRemind.getUpdown_flag()==0) {
            fundGridTask = fundGridTaskMapper.firstByFalltaskid(taskRemind.getId());
            if(null==fundGridTask)
                return;

            edittaskRemind=taskRemindDao.findFirstById(fundGridTask.getRise_taskid());

            if(edittaskRemind.getIsnotice()==1 || null==fundGridTask)
                return;


            float tempDwjz=edittaskRemind.getDwjz()/(1+fundGridTask.getGrid_rate()/100);
            float dwjz=new BigDecimal(tempDwjz).setScale(4,BigDecimal.ROUND_HALF_DOWN).floatValue();
            edittaskRemind.setDwjz(dwjz);

            fundGridTask.setGrid_step(fundGridTask.getGrid_step()-1);
        }else {
            fundGridTask = fundGridTaskMapper.firstByRisetaskid(taskRemind.getId());
            if(null==fundGridTask)
                return;

            edittaskRemind=taskRemindDao.findFirstById(fundGridTask.getFall_taskid());

            if(edittaskRemind.getIsnotice()==1 || null==fundGridTask)
                return;

            float tempDwjz=edittaskRemind.getDwjz()*(1+fundGridTask.getGrid_rate()/100);
            float dwjz=new BigDecimal(tempDwjz).setScale(4,BigDecimal.ROUND_HALF_DOWN).floatValue();
            edittaskRemind.setDwjz(dwjz);

            fundGridTask.setGrid_step(fundGridTask.getGrid_step()+1);
        }


        edittaskRemind.setCreate_date(date);
        taskRemindDao.save(edittaskRemind);

        TaskRemind addTaskRemind=new TaskRemind();
        addTaskRemind.setFundcode(taskRemind.getFundcode());
        addTaskRemind.setUpdown_flag(taskRemind.getUpdown_flag());

        double tempAdd;
        if(taskRemind.getUpdown_flag()==0)
            tempAdd=taskRemind.getDwjz()/(1+fundGridTask.getGrid_rate()/100);
        else
            tempAdd=taskRemind.getDwjz()*(1+fundGridTask.getGrid_rate()/100);

        float addDwjz=new BigDecimal(tempAdd).setScale(4,BigDecimal.ROUND_HALF_DOWN).floatValue();
        addTaskRemind.setDwjz(addDwjz);

        addTaskRemind.setAddressee(taskRemind.getAddressee());
        addTaskRemind.setIsnotice(0);
        addTaskRemind.setCreate_date(date);
        taskRemindDao.save(addTaskRemind);

        if(taskRemind.getUpdown_flag()==0)
            fundGridTask.setFall_taskid(addTaskRemind.getId());
        else
            fundGridTask.setRise_taskid(addTaskRemind.getId());

        fundGridTask.setUpdate_date(date);
        fundGridTaskDao.save(fundGridTask);
    }
}
