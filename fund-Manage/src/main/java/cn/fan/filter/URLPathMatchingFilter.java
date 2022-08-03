package cn.fan.filter;

import cn.fan.service.SessionService;
import cn.fan.util.SpringContextUtil;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

public class URLPathMatchingFilter extends PathMatchingFilter {

    private String cookieName="WEBID";//需与application.yml配置中名称一致

    @Autowired
    SessionService sessionService;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        if (null == sessionService)
            sessionService = SpringContextUtil.getBean(SessionService.class);

        String requestURI = getPathWithinApplication(request);
        System.out.println("路径："+requestURI);
        //获取WEBID（原servlet的cookie）
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Cookie[] cookies=httpServletRequest.getCookies();
        String cookie="";

        if(cookies.length==0){
            WebUtils.toHttp(response).sendError(401);
            return false;
        }

        for(Cookie c:cookies){
            if(cookieName.equals(c.getName()))
                cookie=c.getValue();
        }
        System.out.println("cookie:"+cookie);

        Map<String,Integer> sessionList=sessionService.getList();
        System.out.println("sessionList.size:"+sessionList.size());

        boolean existence=false;
        for(String key:sessionList.keySet()){
            if(key.equals(cookie))
                existence=true;
        }

        if(!existence){
            WebUtils.toHttp(response).sendError(401);
//            WebUtils.issueRedirect(request, response, "/unauthorized");
            System.out.println("未登录");
            return false;
        }

        int userCode=sessionList.get(cookie);
        Map<String,Object> map=sessionService.get(userCode);

        // 看看这个路径权限里有没有维护，如果没有维护，一律放行(也可以改为一律不放行)
        boolean needInterceptor=false;
        Set<String> powerList=(Set) map.get("powerList");
        for (String powerUrl:powerList) {
            if(requestURI.indexOf(powerUrl)!=-1)
                needInterceptor=true;
        }

        if (!needInterceptor) {
            return true;
        } else {
            boolean hasPermission = false;
            Set<String> permissionUrls=(Set) map.get("permissionUrls");
            for (String url : permissionUrls) {
                // 这就表示当前用户有这个权限
                if (requestURI.indexOf(url)!=-1) {
                    hasPermission = true;
                    break;
                }
            }

            if (hasPermission)
                return true;
            else {
                UnauthorizedException ex = new UnauthorizedException("当前用户没有访问路径 " + requestURI + " 的权限");
//                session.setAttribute("ex", ex);
//                response.getWriter().println("");
//                WebUtils.issueRedirect(request, response, "/unauthorized");
                WebUtils.toHttp(response).sendError(401);
                return false;
            }
        }
    }
}

