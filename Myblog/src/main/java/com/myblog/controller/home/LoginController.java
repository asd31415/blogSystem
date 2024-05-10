package com.myblog.controller.home;

import com.myblog.entity.User;
import com.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sun.security.util.Password;

@Controller
public class LoginController {

    UserService userService;

//    @RequestMapping("/error")
//    public String error(){
//        return "error/404";
//    }

    @RequestMapping("/")
    public String index(){
        return "user/login";
    }


    @RequestMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password){
        System.out.println(username);
        System.out.println(password);

        //判断帐号密码是否正确
        User user = userService.getUserByUsername(username);
        if(user == null){
            //用户不存在
            return "user/login";
        }

        if(password != user.getPassword()){
            //密码错误
            return "user/login";
        }

        return "user/index";
    }

    @RequestMapping("/register")
    public String register(){
        System.out.println("正在注册帐号！");

        return "user/index";
    }
}
