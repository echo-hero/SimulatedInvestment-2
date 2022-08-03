package cn.fan.service;

import cn.fan.dao.SysRoleDao;
import cn.fan.mapper.SysRoleMapper;
import cn.fan.pojo.SysRole;
import cn.fan.pojo.SysUser;
import cn.fan.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames="sys_roles")
public class SysRoleService {

    @Autowired
    SysRoleDao sysRoleDao;

    @Autowired
    SysRoleMapper sysRoleMapper;

    private Map<Integer, SysRole> roleMap=new HashMap<>();
    private List<SysRole> roles=new ArrayList<>();

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }
    @CacheEvict(key="'role-'+ #p0")
    public void removeRole(int id){
    }

    @Cacheable(key="'role-'+ #p0")
    public SysRole get(int id){
        if( !roleMap.containsKey(id) )
            return fresh(id);
        else
            return roleMap.get(id);
    }

    @Cacheable(key="'role-'+ #p0")
    public SysRole store(int id){
        return roleMap.get(id);
    }

    public SysRole fresh(int id){
        SysRole sysRole=sysRoleDao.findById(id).get();

        if(null==sysRole)
            return null;

        roleMap.put(id,sysRole);

        SysRoleService roleService= SpringContextUtil.getBean(SysRoleService.class);
        roleService.removeRole(id);
        return roleService.store(id);
    }


    @CacheEvict(key="'roles'")
    public void removeRoles(){
    }

    @Cacheable(key="'roles'")
    public List<SysRole> getAll(){
        if( roles.size()==0 )
            return freshAll();
        else
            return roles;
    }

    @Cacheable(key="'roles'")
    public List<SysRole> storeAll(){
        return roles;
    }

    public List<SysRole> freshAll(){
        roles=sysRoleDao.findAll();

        SysRoleService roleService= SpringContextUtil.getBean(SysRoleService.class);
        roleService.removeRoles();
        return roleService.storeAll();
    }

    public List<SysRole> queryRole(SysRole role){
        return sysRoleMapper.queryRole("%"+role.getRolename()+"%","%"+role.getRoledesc()+"%");
    }

    public Map<String,Object> addRole(SysRole role){
        Map<String,Object> result=new HashMap<>();
        List<SysRole> roleList=sysRoleDao.findByRolename(role.getRolename());

        if(roleList.size()>0) {
            result.put("result",0);
            result.put("content","已存在相同名称的角色，不允许重复添加！");
            return result;
        }

        role.setCreate_date(new Date());
        role.setCreate_by(123456);
        sysRoleDao.save(role);
        fresh(role.getId());

        result.put("result",1);
        result.put("content","添加成功！");
        result.put("roleId",role.getId());
        return result;
    }

    public Map<String,Object> editRole(SysRole role){
        Map<String,Object> result=new HashMap<>();
        SysRole sysRole=sysRoleDao.findById(role.getId()).get();

        if(null==sysRole) {
            result.put("result",0);
            result.put("content","角色数据不存在，请刷新后重试！");
            return result;
        }

        if(!sysRole.getRolename().equals(role.getRolename())){
            List<SysRole> roleList=sysRoleDao.findByRolename(role.getRolename());
            if(roleList.size()>0) {
                result.put("result",0);
                result.put("content","已存在相同名称的角色，不允许重复！");
                return result;
            }else {
                sysRole.setRolename(role.getRolename());
            }
        }

        sysRole.setRoledesc(role.getRoledesc());
        sysRole.setUpdate_date(new Date());
        sysRole.setUpdate_by(123456);
        sysRoleDao.save(sysRole);
        fresh(sysRole.getId());

        result.put("result",1);
        result.put("content","修改成功！");
        return result;
    }
}
