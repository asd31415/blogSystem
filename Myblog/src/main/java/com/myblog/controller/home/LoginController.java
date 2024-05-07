package com.myblog.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {



    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(){
        return "index";
    }


    @RequestMapping("/login")
    public String loginPage(){
        return "good";
    }
}
