package cn.fan.service;

import cn.fan.pojo.SysUser;
import cn.fan.util.SpringContextUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@CacheConfig(cacheNames="session_users")
public class SessionService {

    private Map<Integer,Map<String,Object>> map=new HashMap<>();
    private Map<String,Integer> stringIntegerMap=new HashMap<>();

    @CacheEvict(allEntries=true)
    public void remove(){//清空全部缓存
    }
    @CacheEvict(key="'user-'+ #p0")
    public void removeUser(int userCode){
    }

    @Cacheable(key="'user-'+ #p0")
    public Map<String,Object> get(int userCode){
        if( !map.containsKey(userCode) )
            return null;
        else
            return map.get(userCode);
    }

    @Cacheable(key="'user-'+ #p0")
    public Map<String,Object> store(int userCode){
        return map.get(userCode);
    }

    public Map<String,Object> fresh(Map<String,Object> sessionMap){

        int userCode=((SysUser)sessionMap.get("user")).getUsercode();
        map.put(userCode,sessionMap);

        SessionService sessionService=SpringContextUtil.getBean(SessionService.class);
        sessionService.removeUser(userCode);
        return sessionService.store(userCode);
    }

    @CacheEvict(key="'userList'")
    public void removeUserList(){
    }

    @Cacheable(key="'userList'")
    public Map<String,Integer> getList(){
        if( stringIntegerMap.size()==0 )
            return null;
        else
            return stringIntegerMap;
    }

    @Cacheable(key="'userList'")
    public Map<String,Integer> storeList(){
        return stringIntegerMap;
    }


    public Map<String,Integer> addList(String sessionId,int userCode){
        SessionService sessionService=SpringContextUtil.getBean(SessionService.class);
        stringIntegerMap=sessionService.getList();
        for(Map.Entry<String, Integer> entry : stringIntegerMap.entrySet()){
            if(userCode==entry.getValue()){
                stringIntegerMap.remove(entry.getKey());
            }
        }

        stringIntegerMap.put(sessionId,userCode);

        sessionService.removeUserList();
        return sessionService.storeList();

    }

    public void forelogout(String cookie){
        SessionService sessionService=SpringContextUtil.getBean(SessionService.class);
        stringIntegerMap=sessionService.getList();

        if(stringIntegerMap.containsKey(cookie)){
            int userCode=stringIntegerMap.get(cookie);
            sessionService.removeUser(userCode);

            //删掉该用户自身及集合中的信息
            stringIntegerMap.remove(cookie);
            sessionService.removeUserList();
            sessionService.storeList();
        }

    }
}