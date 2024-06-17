package com.myblog.controller.admin;

import com.myblog.entity.Blog;
import com.myblog.entity.User;
import com.myblog.service.admin.BlogsService;
import com.myblog.service.admin.ManagerService;
import com.myblog.service.admin.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class admin_userManageController {

    @Autowired
    private final UserManageService userManageService;

    @Autowired
    private final BlogsService blogsService;

    @Autowired
    private final ManagerService managerService;


    public admin_userManageController(UserManageService userManageService,
                                      BlogsService blogsService,
                                      ManagerService managerService) {
        this.userManageService = userManageService;
        this.blogsService = blogsService;
        this.managerService = managerService;
    }

    @GetMapping("/userManage")
    public String getAllUsers(Model model) {
        List<User> users = userManageService.getAllUsers();

        model.addAttribute("users", users);
        return "admin/userManage";
    }

    @RequestMapping("users/{id}/details")
    public String showUserDetails(@PathVariable("id") int userId, Model model) {

        // 根据用户ID查找对应的用户对象
        User user = userManageService.getUserById(userId);

        List<Blog> blogs = blogsService.getBlogsByUserId(userId);

        model.addAttribute("user", user);

        model.addAttribute("blogs", blogs);

        return "admin/userInfo";
    }

    @RequestMapping("users/{id}/delete")
    public String deleteUser(@PathVariable("id") int userId) {

        // 根据userId删除对象
        userManageService.deleteUserById(userId);

        return "redirect:/userManage";
    }

    @RequestMapping("users/{id}/upgrade")
    public String UserUpgrade(@PathVariable("id") int userId) {

        // 根据用户ID查找对应的用户对象
        User user = userManageService.getUserById(userId);

        String username = user.getUsername();
        String password = user.getPassword();

        if (!managerService.existsManagerByUsernameAndPassword(username, password)) {
            // 如果不存在，创建管理员
            managerService.createManager(username, password);
        } else {
            // 如果存在，创建失败，可以进行相应处理，比如重定向回用户管理页面并提示错误信息
            // 这里假设您的页面中有一个错误提示页面，名称为 error.html
            return "admin/upgrade_error";
        }



        managerService.createManager(username, password);

        return "redirect:/userManage";
    }

}
