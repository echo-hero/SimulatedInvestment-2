package cn.fan.service;

import cn.fan.dao.TaskRemindDao;
import cn.fan.pojo.TaskRemind;
import cn.fan.pojo.UserInvest;
import cn.fan.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames="taskReminds")
public class TaskRemindService {

    private Map<String,List<TaskRemind>> stringListMap=new HashMap<>();

    @Autowired
    TaskRemindDao taskRemindDao;

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }

    //123456为用户id,后续进行调整
    @CacheEvict(key="'taskRemind-'+ #p0")
    public void removeUserId(int userId){
    }

    @Cacheable(key="'taskRemind-'+ #p0")
    public List<TaskRemind> get(int userId){
        if(0==userId)
            return null;

        if( !stringListMap.containsKey(userId+"") )
            return fresh(userId);
        else
            return stringListMap.get(userId+"");
    }

    @Cacheable(key="'taskRemind-'+ #p0")
    public List<TaskRemind> store(int userId){
        return stringListMap.get(userId+"");
    }

    public List<TaskRemind> fresh(int userId){

        List<TaskRemind> taskRemindList=taskRemindDao.findByUserid(userId);
        stringListMap.put(userId+"",taskRemindList);

        TaskRemindService taskRemindService=SpringContextUtil.getBean(TaskRemindService.class);
        taskRemindService.removeUserId(userId);
        return taskRemindService.store(userId);
    }


    public List<TaskRemind> selectPartCondition(int userId,TaskRemind taskRemind){
        List<TaskRemind> taskReminds=get(userId);

        List<TaskRemind> taskRemindList=new ArrayList<>();
        for(TaskRemind remind:taskReminds){
            if( (taskRemind.getFundcode()=="" || taskRemind.getFundcode().equals(remind.getFundcode())) &&
                    (taskRemind.getUpdown_flag()==-1 || taskRemind.getUpdown_flag()==remind.getUpdown_flag()) &&
                    (taskRemind.getIsnotice()==-1 || taskRemind.getIsnotice()==remind.getIsnotice())
            )
                taskRemindList.add(remind);
        }
        return taskRemindList;
    }

    public String addTaskRemind(int userId,TaskRemind taskRemind){
        taskRemind.setIsnotice(0);
        taskRemind.setUserid(userId);
        taskRemind.setCreate_date(new Date());

        taskRemindDao.save(taskRemind);
        fresh(userId);
        return "新增成功！";
    }

    public TaskRemind getRemind(int id){
        return taskRemindDao.findFirstById(id);
    }

    public String editTask(TaskRemind taskRemind){
        TaskRemind remind=taskRemindDao.findFirstById(taskRemind.getId());
        remind.setDwjz(taskRemind.getDwjz());
        remind.setAddressee(taskRemind.getAddressee());
        taskRemindDao.save(remind);
        fresh(taskRemind.getId());
        return "修改成功！";
    }

}
