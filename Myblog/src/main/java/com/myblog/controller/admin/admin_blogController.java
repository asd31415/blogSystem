package com.myblog.controller.admin;

import com.myblog.entity.Blog;
import com.myblog.entity.Comment;
import com.myblog.entity.User;
import com.myblog.service.BlogService;
import com.myblog.service.UserService;
import com.myblog.service.admin.BlogsService;
//import com.myblog.service.admin.CommentService;
import com.myblog.service.admin.CommentsService;
import com.myblog.service.admin.UserManageService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class admin_blogController {


    @Getter
    private final BlogsService blogsService;

    @Autowired
    private  BlogService blogService;

    @Getter
    private final UserManageService userManageService;


    private final CommentsService commentsService;

    private final UserService userService;


//    public admin_blogController(BlogService blogService) {
//        this.blogService = blogService;
//    }

    public admin_blogController(UserManageService userManageService,
                                BlogsService blogsService
                              , CommentsService commentsService
                              , BlogService blogService
                              , UserService userService
                              ) {
        this.blogsService = blogsService;
        this.userManageService = userManageService;
        this.commentsService = commentsService;
        this.blogService = blogService;
        this.userService = userService;
    }

    @GetMapping("/blogsManage")
    public String getAllBlogs(Model model) {

        List<Blog> blogs = blogService.getBlogLists();

        model.addAttribute("blogs", blogs);
        return "admin/blogs";
    }

    @RequestMapping("blogs/{id}/details")
    public String showArticlesDetails(@PathVariable("id") int id, Model model, HttpSession session) {

        //  根据文章编号筛选文章

        Blog blog = blogService.getBlogById(id,true);

        model.addAttribute("blog", blog);


        //  根据文章编号筛选评论

        List<Comment> comments = commentsService.getCommentsByBlogId(id);
        User user = userService.getUserByUserId(blog.getUserId());

        blog.setUser(user);



        if(session.getAttribute("manager") == null){
            model.addAttribute("isLoggedIn",false);
        }else model.addAttribute("isLoggedIn",true);

        model.addAttribute("comments", comments);

        return "admin/blogExhibit";
    }

    @RequestMapping("blogs/{id}/delete")
    public String BlogsDelete(@PathVariable("id") int id) {

        blogsService.deleteBlogById(id);

        return "redirect:/blogsManage";
    }




}