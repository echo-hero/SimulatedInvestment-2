package cn.fan.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FundViewController {
    @GetMapping("/index")
    public String index() throws Exception{
        return "index/index";
    }

    @GetMapping("/detail")
    public String detail() throws Exception{
        return "detail/index";
    }

    @GetMapping("/user")
    public String user() throws Exception{
        return "user/index";
    }

    @GetMapping("/login")
    public String login() throws Exception{
        return "login";
    }

}
