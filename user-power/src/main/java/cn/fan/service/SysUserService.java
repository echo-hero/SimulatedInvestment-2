package cn.fan.service;

import cn.fan.dao.SysUserDao;
import cn.fan.mapper.SysUserMapper;
import cn.fan.pojo.SysUser;
import cn.fan.util.SpringContextUtil;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
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
@CacheConfig(cacheNames="sys_users")
public class SysUserService {

    @Autowired
    SysUserDao sysUserDao;

    @Autowired
    SysUserMapper sysUserMapper;

    private Map<Integer,SysUser> userMap=new HashMap<>();

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }
    @CacheEvict(key="'user-'+ #p0")
    public void removeUser(int usercode){
    }

    @Cacheable(key="'user-'+ #p0")
    public SysUser get(int usercode){
        if( !userMap.containsKey(usercode) )
            return fresh(usercode);
        else
            return userMap.get(usercode);
    }

    @Cacheable(key="'user-'+ #p0")
    public SysUser store(int usercode){
        return userMap.get(usercode);
    }

    public SysUser fresh(int usercode){

        SysUser sysUser=sysUserDao.findFirstByUsercode(usercode);
        if(null==sysUser)
            return null;
//        sysUser.setPassword("");
//        sysUser.setSalt("");//密码和salt不存到redis中

        userMap.put(usercode,sysUser);

        SysUserService userService= SpringContextUtil.getBean(SysUserService.class);
        userService.removeUser(usercode);
        return userService.store(usercode);
    }

    public List<SysUser> queryUser(SysUser user){
        return sysUserMapper.queryUser("%"+user.getUsername()+"%","%"+user.getEmailadd()+"%");
    }

    public Map<String,Object> addUser(SysUser user){
        Map<String,Object> result=new HashMap<>();
        if(null!=sysUserDao.findFirstByUsercode(user.getUsercode())) {
            result.put("result",0);
            result.put("content","该用户已存在，不允许重复新增，仅允许修改处理。");
            return result;
        }

        user.setId(user.getUsercode());
        user.setCreate_date(new Date());
        getPasswordAndSalt(user);

        sysUserDao.save(user);
        fresh(user.getId());

        result.put("result",1);
        result.put("content","添加成功");
        result.put("userId",user.getId());
        return result;
    }

    public Map<String,Object> editUser(SysUser user){
        Map<String,Object> result=new HashMap<>();
        SysUser sysUser=sysUserDao.findFirstByUsercode(user.getUsercode());

        if(null==sysUser) {
            result.put("result",0);
            result.put("content","用户数据不存在，请刷新后重试！");
            return result;
        }

        sysUser.setUsername(user.getUsername());
        sysUser.setEmailadd(user.getEmailadd());

        if(!"".equals(user.getPassword())){
            getPasswordAndSalt(sysUser);
        }
        sysUserDao.save(sysUser);
        fresh(sysUser.getId());

        result.put("result",1);
        result.put("content","修改成功");
        return result;
    }

    private void getPasswordAndSalt(SysUser user){
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithmName = "md5";
        String encodedPassword = new SimpleHash(algorithmName,user.getPassword(),salt,times).toString();

        user.setPassword(encodedPassword);
        user.setSalt(salt);
    }

}
