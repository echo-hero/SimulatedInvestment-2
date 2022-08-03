package cn.fan.service;

import cn.fan.dao.SysPowerDao;
import cn.fan.mapper.SysPowerMapper;
import cn.fan.pojo.*;
import cn.fan.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames="sys_powers")
public class SysPowerService {

    @Autowired
    SysPowerDao sysPowerDao;

    @Autowired
    SysPowerMapper sysPowerMapper;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysPowerService sysPowerService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRolePowerService sysRolePowerService;

    private Map<Integer, SysPower> powerMap=new HashMap<>();
    private List<SysPower> powers=new ArrayList<>();

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }
    @CacheEvict(key="'power-'+ #p0")
    public void removePower(int id){
    }

    @Cacheable(key="'power-'+ #p0")
    public SysPower get(int id){
        if( !powerMap.containsKey(id) )
            return fresh(id);
        else
            return powerMap.get(id);
    }

    @Cacheable(key="'power-'+ #p0")
    public SysPower store(int id){
        return powerMap.get(id);
    }

    public SysPower fresh(int id){
        SysPower sysPower=sysPowerDao.findById(id).get();
        if(null==sysPower)
            return null;

        powerMap.put(id,sysPower);

        SysPowerService powerService= SpringContextUtil.getBean(SysPowerService.class);
        powerService.removePower(id);
        return powerService.store(id);
    }

    @CacheEvict(key="'powers'")
    public void removePowers(){
    }

    @Cacheable(key="'powers'")
    public List<SysPower> getAll(){
        if( powers.size()==0 )
            return freshAll();
        else
            return powers;
    }

    @Cacheable(key="'powers'")
    public List<SysPower> storeAll(){
        return powers;
    }

    public List<SysPower> freshAll(){
        powers=sysPowerDao.findAll();

        SysPowerService powerService= SpringContextUtil.getBean(SysPowerService.class);
        powerService.removePowers();
        return powerService.storeAll();
    }

    public List<SysPower> queryPower(SysPower power){
        return sysPowerMapper.queryLikePower("%"+power.getPowername()+"%","%"+power.getPowerdesc()+"%","%"+power.getPowerurl()+"%");
    }

    public String addPower(SysPower power){
        List<SysPower> sysPowerList=sysPowerMapper.queryPower(power.getPowername(),power.getPowerurl());

        if(sysPowerList.size()>0)
            return "已存在相同名称或权限路径的权限，不允许重复添加！";

        power.setCreate_date(new Date());
        power.setCreate_by(123456);
        sysPowerDao.save(power);
        fresh(power.getId());
        freshAll();
        return "添加成功！";
    }

    public String editPower(SysPower power){
        SysPower sysPower=sysPowerDao.findById(power.getId()).get();

        if(null==sysPower)
            return "权限数据不存在，请刷新后重试";

        if(!sysPower.getPowername().equals(power.getPowername())){
            List<SysPower> powerList=sysPowerDao.findByPowername(power.getPowername());
            if(powerList.size()>0)
                return "已存在相同名称的权限，不允许重复";
            else
                sysPower.setPowername(power.getPowername());
        }
        if(!sysPower.getPowerurl().equals(power.getPowerurl())){
            List<SysPower> powerList=sysPowerDao.findByPowerurl(power.getPowerurl());
            if(powerList.size()>0)
                return "已存在相同权限路径的权限，不允许重复";
            else
                sysPower.setPowerurl(power.getPowerurl());
        }

        sysPower.setPowerdesc(power.getPowerdesc());
        sysPower.setUpdate_date(new Date());
        sysPower.setUpdate_by(123456);
        sysPowerDao.save(sysPower);
        fresh(sysPower.getId());
        return "修改成功!";
    }


    public Set<String> listPermissionURLs(int userCode){
        Set<String> permissions=new HashSet<>();

        SysUser sysUser = sysUserService.get(userCode);
        List<SysUserRole> userRoleList=sysUserRoleService.get(sysUser.getId());

        for(SysUserRole userRole:userRoleList){
            SysRole role=sysRoleService.get(userRole.getRoleid());
            List<SysRolePower> rolePowerList=sysRolePowerService.get(role.getId());
            for(SysRolePower rolePower:rolePowerList){
                SysPower power=sysPowerService.get(rolePower.getPowerid());
                permissions.add(power.getPowerurl());
            }
        }
        return permissions;
    }

    public Set<String> getAllSet(){
        List<SysPower> powerList=getAll();
        Set<String> result=new HashSet<>();

        for(SysPower power:powerList){
            result.add(power.getPowerurl());
        }
        return result;
    }
}
