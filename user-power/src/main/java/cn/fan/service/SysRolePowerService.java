package cn.fan.service;

import cn.fan.dao.SysRolePowerDao;
import cn.fan.pojo.SysRolePower;
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
@CacheConfig(cacheNames = "sys_role_powers")
public class SysRolePowerService {

    @Autowired
    SysRolePowerDao sysRolePowerDao;

    private Map<Integer, List<SysRolePower>> listMap=new HashMap<>();

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }
    @CacheEvict(key="'sys_role_power-'+ #p0")
    public void removeUser(int roleId){
    }

    @Cacheable(key="'sys_role_power-'+ #p0")
    public List<SysRolePower> get(int roleId){
        if( !listMap.containsKey(roleId) )
            return fresh(roleId);
        else
            return listMap.get(roleId);
    }

    @Cacheable(key="'sys_role_power-'+ #p0")
    public List<SysRolePower> store(int roleId){
        return listMap.get(roleId);
    }

    public List<SysRolePower> fresh(int roleId){
        List<SysRolePower> userRoleList=sysRolePowerDao.findByRoleid(roleId);
        listMap.put(roleId,userRoleList);

        SysRolePowerService rolePowerService= SpringContextUtil.getBean(SysRolePowerService.class);
        rolePowerService.removeUser(roleId);
        return rolePowerService.store(roleId);
    }


    public void editRolePower(int[] powersId,int roleId){
        List<SysRolePower> rolePowerList=get(roleId);
        for(SysRolePower rolePower:rolePowerList){
            sysRolePowerDao.delete(rolePower);
        }

        for(int powerId:powersId){
            SysRolePower rolePower=new SysRolePower();
            rolePower.setRoleid(roleId);
            rolePower.setPowerid(powerId);
            rolePower.setCreate_date(new Date());
            rolePower.setCreate_by(123456);
            sysRolePowerDao.save(rolePower);
        }
        fresh(roleId);
    }

    public int[] getPowerByRoleId(int roleId){
        List<SysRolePower> rolePowerList=get(roleId);

        int[] result=new int[rolePowerList.size()];
        for(int i=0;i<result.length;i++){
            result[i]=rolePowerList.get(i).getPowerid();
        }
        return result;
    }
}
