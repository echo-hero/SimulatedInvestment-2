package cn.fan.web;

import cn.fan.config.IpConfiguration;
import cn.fan.pojo.SysPower;
import cn.fan.pojo.SysRole;
import cn.fan.pojo.SysUser;
import cn.fan.service.*;
import cn.fan.util.Page4Util;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserPowerController {
    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    SysPowerService sysPowerService;

    @Autowired
    SysUserRoleService sysUserRoleService;

    @Autowired
    SysRolePowerService sysRolePowerService;

    @Autowired
    SessionService sessionService;

    @Autowired
    IpConfiguration ipConfiguration;

    @PostMapping("/queryUser/{start}/{size}")
    @CrossOrigin
    public Page4Util<SysUser> queryUser(@RequestBody SysUser user,@PathVariable("start") int start,@PathVariable("size") int size) throws Exception{
        System.out.println("current instance's port is "+ ipConfiguration.getPort());
        List<SysUser> sysUserList=sysUserService.queryUser(user);

//        , @CookieValue("JSESSIONID") String cookie
//        try{
//            System.out.println("cookie:"+cookie);
//            Map<String,Integer> sessionList=sessionService.getList();
//            int userCode=sessionList.get(cookie);
//
//            Map<String,Object> map=sessionService.get(userCode);
//            System.out.println("date:"+map.get("date"));
//        }catch (Exception e){
//            System.out.println("查询失败！");
//        }

        int navigatePages=5;//默认前端显示5个页数
        Page4Util<SysUser> page4Util=new Page4Util<>(start,size,navigatePages);
        page4Util.page(sysUserList);
        return  page4Util;
    }

    @PostMapping("/addUser")
    @CrossOrigin
    public Map<String,Object> addUser(@RequestBody SysUser user) throws Exception{
        return sysUserService.addUser(user);
    }

    @GetMapping("/getUserByUserCode/{userCode}")
    @CrossOrigin
    public SysUser getUserByUserCode(@PathVariable("userCode") int userCode) throws Exception{
        SysUser user=sysUserService.get(userCode);
        user.setPassword("");
        user.setSalt("");//密码信息不给前端返回
        try{
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            System.out.println("seesionId:"+session.getId());
        }catch (Exception e){
            System.out.println("getUserByUserCode失败!");
        }
        return user;
    }

    @PostMapping("/editUser")
    @CrossOrigin
    public Map<String,Object> editUser(@RequestBody SysUser user) throws Exception{
        return sysUserService.editUser(user);
    }


    @GetMapping("/getRoles")
    public List<SysRole> getRoles() throws Exception{
        return sysRoleService.getAll();
    }

    @PostMapping("/queryRole/{start}/{size}")
    @CrossOrigin
    public Page4Util<SysRole> queryRole(@RequestBody SysRole role, @PathVariable("start") int start, @PathVariable("size") int size) throws Exception{
        List<SysRole> sysRoleList=sysRoleService.queryRole(role);

        int navigatePages=5;//默认前端显示5个页数
        Page4Util<SysRole> page4Util=new Page4Util<>(start,size,navigatePages);
        page4Util.page(sysRoleList);
        return  page4Util;
    }

    @PostMapping("/addRole")
    @CrossOrigin
    public Map<String,Object> addRole(@RequestBody SysRole role) throws Exception{
        return sysRoleService.addRole(role);
    }

    @GetMapping("/getRole/{id}")
    @CrossOrigin
    public SysRole getRole(@PathVariable("id") int id) throws Exception{
        return sysRoleService.get(id);
    }

    @PostMapping("/editRole")
    @CrossOrigin
    public Map<String,Object> editRole(@RequestBody SysRole role) throws Exception{
        return sysRoleService.editRole(role);
    }


    @GetMapping("/getPowers")
    @CrossOrigin
    public List<SysPower> getPowers() throws Exception{
        return sysPowerService.getAll();
    }

    @PostMapping("/queryPower/{start}/{size}")
    @CrossOrigin
    public Page4Util<SysPower> queryPower(@RequestBody SysPower power, @PathVariable("start") int start, @PathVariable("size") int size) throws Exception{
        List<SysPower> sysPowerList=sysPowerService.queryPower(power);

        int navigatePages=5;//默认前端显示5个页数
        Page4Util<SysPower> page4Util=new Page4Util<>(start,size,navigatePages);
        page4Util.page(sysPowerList);
        return  page4Util;
    }

    @PostMapping("/addPower")
    @CrossOrigin
    public String addPower(@RequestBody SysPower power) throws Exception{
        return sysPowerService.addPower(power);
    }

    @GetMapping("/getPower/{id}")
    @CrossOrigin
    public SysPower getPower(@PathVariable("id") int id) throws Exception{
        return sysPowerService.get(id);
    }

    @PostMapping("/editPower")
    @CrossOrigin
    public String editRole(@RequestBody SysPower power) throws Exception{
        return sysPowerService.editPower(power);
    }


    @PostMapping("/editUserRole/{userCode}")
    @CrossOrigin
    public int editUserRole(@RequestBody int[] rolesId,@PathVariable("userCode") int userCode) throws Exception{
        try {
            SysUser user=sysUserService.get(userCode);
            sysUserRoleService.editUserRole(rolesId,user);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    @GetMapping("/getRoleByUserId/{userId}")
    @CrossOrigin
    public int[] getRoleByUserId(@PathVariable("userId") int userId) throws Exception{
        return sysUserRoleService.getRoleByUserId(userId);
    }

    @PostMapping("/editRolePower/{roleId}")
    @CrossOrigin
    public int editRolePower(@RequestBody int[] powers,@PathVariable("roleId") int roleId) throws Exception{
        try {
            sysRolePowerService.editRolePower(powers,roleId);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    @GetMapping("/getPowerByRoleId/{roleId}")
    @CrossOrigin
    public int[] getPowerByRoleId(@PathVariable("roleId") int roleId) throws Exception{
        return sysRolePowerService.getPowerByRoleId(roleId);
    }

    @GetMapping("/getCookies")
    @CrossOrigin
    public Map<String,Integer> getCookies() throws Exception{
        return sessionService.getList();
    }

    @PostMapping("/forelogin")
    @CrossOrigin
    public Map<String,Object> forelogin(@RequestBody SysUser user)  {
        Map<String,Object> result=new HashMap<>();
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsercode()+"", user.getPassword());

        try{
            subject.login(token);
            Session session = subject.getSession();
            session.setAttribute("subject", subject);

            Map<String,Object> map=new HashMap<>();
            SysUser sysUser=sysUserService.get(user.getUsercode());
            sysUser.setPassword("");
            sysUser.setSalt("");

            Set<String> permissionUrls=sysPowerService.listPermissionURLs(user.getUsercode());
            Set<String> powerList=sysPowerService.getAllSet();

            map.put("user",sysUser);
            map.put("date",new Date());
            map.put("sessionId",session.getId());
            map.put("host",session.getHost());
            map.put("permissionUrls",permissionUrls);
            map.put("powerList",powerList);

            sessionService.fresh(map);
            sessionService.addList(session.getId()+"",user.getUsercode());

            result.put("code",1);
            result.put("message","登录成功！");
        }catch (Exception e){
            result.put("code",0);
            result.put("message","账号或密码错误！");
        }

        return result;
    }

    @GetMapping("/forelogout")
    @CrossOrigin
    public Map<String,Object> forelogout( @CookieValue("WEBID") String cookie) throws Exception{
        Map<String,Object> result=new HashMap<>();

        try {
            sessionService.forelogout(cookie);
            result.put("code",1);
            result.put("message","退出成功！");
        }catch (Exception e){
            result.put("code",0);
            result.put("message","退出失败！");
        }

        return result;
    }

}
