package cn.fan.service;

import cn.fan.dao.SysUserRoleDao;
import cn.fan.pojo.SysRole;
import cn.fan.pojo.SysUser;
import cn.fan.pojo.SysUserRole;
import cn.fan.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames = "sys_user_roles")
public class SysUserRoleService {

    @Autowired
    SysUserRoleDao sysUserRoleDao;

    @Autowired
    SysRoleService sysRoleService;

    private Map<Integer,List<SysUserRole>> listMap=new HashMap<>();

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }
    @CacheEvict(key="'sys_user_role-'+ #p0")
    public void removeUser(int userId){
    }

    @Cacheable(key="'sys_user_role-'+ #p0")
    public List<SysUserRole> get(int userId){
        if( !listMap.containsKey(userId) )
            return fresh(userId);
        else
            return listMap.get(userId);
    }

    @Cacheable(key="'sys_user_role-'+ #p0")
    public List<SysUserRole> store(int userId){
        return listMap.get(userId);
    }

    public List<SysUserRole> fresh(int userId){
        List<SysUserRole> userRoleList=sysUserRoleDao.findByUserid(userId);
        listMap.put(userId,userRoleList);

        SysUserRoleService userRoleService= SpringContextUtil.getBean(SysUserRoleService.class);
        userRoleService.removeUser(userId);
        return userRoleService.store(userId);
    }

    public void editUserRole(int[] rolesId,SysUser user) throws Exception{
        List<SysUserRole> userRoleList=get(user.getId());
        for(SysUserRole userRole:userRoleList){
            sysUserRoleDao.delete(userRole);
        }

        for(int roleId:rolesId){
            SysUserRole userRole=new SysUserRole();
            userRole.setUserid(user.getId());
            userRole.setRoleid(roleId);
            userRole.setCreate_date(new Date());
            userRole.setCreate_by(123456);
            sysUserRoleDao.save(userRole);
        }
        fresh(user.getId());
    }

    public int[] getRoleByUserId(int userId){
        List<SysUserRole> userRoleList=get(userId);

        int[] result=new int[userRoleList.size()];
        for(int i=0;i<result.length;i++){
            result[i]=userRoleList.get(i).getRoleid();
        }
        return result;
    }

    public Set<String> listRoleName(int userId){
        List<SysUserRole> userRoleList=get(userId);
        Set<String> result=new HashSet<>();

        for(SysUserRole userRole:userRoleList){
            SysRole role=sysRoleService.get(userRole.getId());
            result.add(role.getRolename());
        }
        return result;
    }
}
