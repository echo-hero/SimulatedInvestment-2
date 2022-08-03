package cn.fan.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@CacheConfig(cacheNames="session_users")
public class SessionService {

    @Cacheable(key="'user-'+ #p0")
    public Map<String,Object> get(int userCode){
        System.out.println("user未获取到数据："+userCode);
        Map<String,Object> result=new HashMap<>();
        return result;
    }

    @Cacheable(key="'userList'")
    public Map<String,Integer> getList(){
        System.out.println("（SessionService）userList未获取到数据！");
        Map<String,Integer> result=new HashMap<>();
        return result;
    }

}