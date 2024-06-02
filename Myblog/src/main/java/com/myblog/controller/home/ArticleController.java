package com.myblog.controller.home;

import com.myblog.entity.Blog;
import com.myblog.entity.User;
import com.myblog.service.BlogService;
import com.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ArticleController {

    @Autowired
    BlogService blogService;

    @Autowired
    UserService userService;

    @RequestMapping(value = {"article/like/{blogId}", "{blogId}"})
    public String like(@PathVariable Integer blogId, Model model, HttpSession session){

        blogService.incrBlogLikes(blogId);

        //1、查询文章
        Blog blog = blogService.getBlogById(blogId);

        User user = userService.getUserByUserId(blog.getUserId());
        blog.setUser(user);

        model.addAttribute("blog", blog);

        if(session.getAttribute("user") == null){
            model.addAttribute("isLoggedIn",false);
        }else model.addAttribute("isLoggedIn",true);

        return "user/article";
    }

    @RequestMapping(value = "article/comment")
    public void comment(Model model, HttpSession session, @RequestParam("blogId") Integer blogId,
                        @RequestParam("parentId") Integer parentId,
                        @RequestParam("content") String content){

        System.out.println(blogId);
        System.out.println(parentId);
        System.out.println(content);

    }
}
