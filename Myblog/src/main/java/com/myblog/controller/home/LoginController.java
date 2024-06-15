package com.myblog.controller.home;

import com.myblog.config.LogTypeEnum;
import com.myblog.config.SystemLog;
import com.myblog.entity.User;
import com.myblog.service.UserService;
import com.myblog.util.VerifyCodeUtil;
import lombok.Value;
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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.myblog.util.MyUtils.code;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @RequestMapping(value={"/","/login"})
    public String login(){
        return "user/login";
    }


    @RequestMapping(value={"/register"})
    public String register(){
        return "user/register";
    }


    @SystemLog(description = "用户登录", type = LogTypeEnum.LOGIN)
    @RequestMapping("/loginVerify")
    public ResponseEntity<?> loginVerify(@RequestParam("username") String username,
                                         @RequestParam("password") String password,
                                         @RequestParam("verifyCode") String verifyCode,
                                         HttpSession session){

        if(username==null||username.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名不能为空！");
        }
        if(password==null||password.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码不能为空！");
        }
        if(verifyCode==null||verifyCode.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("验证码不能为空！");
        }else if(!verifyCode.equals(session.getAttribute("verifyCode"))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("验证码错误！");
        }

        //判断帐号密码是否正确
        User user = userService.getUserByUsername(username);
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户不存在！");
        }

        if(!code(password).equals(code(user.getPassword()))){
            System.out.println("输入:"+password);
            System.out.println("正确:"+user.getPassword());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码错误！");
        }

        //更新登录状态
        session.setAttribute("user",user);

        return ResponseEntity.ok().body("登录成功！");
    }

    @SystemLog(description = "用户注册", type = LogTypeEnum.REGISTER)
    @RequestMapping("/registerVerify")
    public ResponseEntity<?> registerVerify(@RequestParam("email") String email,
                                      @RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      @RequestParam("nickname") String nickname,
                                      @RequestParam("description") String description){

        User newUser = new User();

        if(email == null || email.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("电子邮箱不能为空！");
        }else{newUser.setEmail(email);}

        if(username == null || username.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名不能为空！");
        }else{
            User existUser = userService.getUserByUsername(username);
            if(existUser != null){
                System.out.println("用户已存在");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户已存在！");
            }
            newUser.setUsername(username);
        }
        if(password == null || password.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码不能为空！");
        }else newUser.setPassword(code(password));

        if(nickname == null || nickname.equals("")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("昵称不能为空！");
        }else newUser.setNickname(nickname);

        newUser.setDescription(description);

        userService.registerUser(newUser);
        return ResponseEntity.ok().body("注册成功!");
    }

    /**
     * 刷新验证码
     * @param request
     * @param response
     */
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    public void verifyCode(HttpServletRequest request , HttpServletResponse response){
        System.out.println("verifyCode执行=====================");
        response.setContentType("image/jpeg");
        //设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        VerifyCodeUtil vcUtil = VerifyCodeUtil.Instance();
        //将随机码设置在session中,用于登录时判断校验
        request.getSession().setAttribute("verifyCode", vcUtil.getResult()+"");
        try {
            ImageIO.write(vcUtil.getImage(), "jpeg", response.getOutputStream());

            response.getOutputStream().flush();
            response.getOutputStream().close();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
