package com.myblog.controller.home;

import com.myblog.entity.User;
import com.myblog.service.UserService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sun.security.util.Password;

@Controller
public class LoginController {

    UserService userService;

    @RequestMapping(value={"/","/login"})
    public String login(){
        return "user/login";
    }

    @RequestMapping(value={"/register"})
    public String register(){
        return "user/register";
    }


    @RequestMapping("/loginVerify")
    public ResponseEntity<?> loginVerify(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode){
        System.out.println(username);
        System.out.println(password);
        System.out.println(verifyCode);

        if(username==null||username.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名不能为空！");
        }
        if(password==null||password.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码不能为空！");
        }
        if(verifyCode==null||verifyCode.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("验证码不能为空！");
        }

        //判断帐号密码是否正确
        User user = userService.getUserByUsername(username);
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户不存在！");
        }

        if(password != user.getPassword()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码错误！");
        }

        //更新登录状态
        userService.loginIn(user);

        return ResponseEntity.ok().body("登录成功！");
    }

    //能不能让前端直接传递一个User对象过来
    @RequestMapping("/registerVerify")
    public ResponseEntity<?> registerVerify(@RequestParam("email") String email,
                                      @RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      @RequestParam("nickname") String nickname){
        System.out.println("正在注册帐号！");

        User newUser = new User();
        if(email == null || email.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("电子邮箱不能为空！");
        }else{newUser.setEmail(email);}
        if(username == null || username.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名不能为空！");
        }else{
            User existUser = userService.getUserByUsername(username);
            if(existUser != null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户已存在！");
            }
            newUser.setUsername(username);
        }
        if(password == null || password.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码不能为空！");
        }else newUser.setPassword(password);
        if(nickname == null || nickname.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("昵称不能为空！");
        }else newUser.setNickname(nickname);

        userService.registerUser(newUser);

        return ResponseEntity.ok().body("登录成功!");
    }

    @RequestMapping("/user/index")
    public String index(){
        //进入到首页
        return "user/index";
    }

}
