package cn.fan.realm;

import cn.fan.pojo.*;
import cn.fan.service.*;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseRealm extends AuthorizingRealm {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysPowerService sysPowerService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
//        目前通过redis数据获取用户权限信息，该部分暂不使用

//         能进入到这里，表示账号已经通过验证了
        int userCode = (int) principalCollection.getPrimaryPrincipal();
        SysUser user = sysUserService.get(userCode);
        // 通过service获取角色和权限

        List<SysUserRole> userRoleList=sysUserRoleService.get(user.getId());
        Set<String> roles=new HashSet<>();
        Set<String> permissions=sysPowerService.listPermissionURLs(userCode);
        for(SysUserRole userRole:userRoleList){
            SysRole role=sysRoleService.get(userRole.getRoleid());
            roles.add(role.getRolename());
        }

        // 授权对象
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        // 把通过service获取到的角色和权限放进去
        s.setStringPermissions(permissions);
        s.setRoles(roles);
        return s;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 获取账号密码
        UsernamePasswordToken t = (UsernamePasswordToken) token;
        String userCode = token.getPrincipal().toString();
        // 获取数据库中的密码
        SysUser user = sysUserService.get(Integer.parseInt(userCode));
        String passwordInDB = user.getPassword();
        String salt = user.getSalt();
        // 认证信息里存放账号密码, getName() 是当前Realm的继承方法,通常返回当前类名 :databaseRealm
        // 盐也放进去
        SimpleAuthenticationInfo a = new SimpleAuthenticationInfo(userCode, passwordInDB, ByteSource.Util.bytes(salt),
                getName());
        return a;
    }
}
