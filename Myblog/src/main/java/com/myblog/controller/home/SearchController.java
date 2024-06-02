package com.myblog.controller.home;

import com.myblog.entity.Blog;
import com.myblog.entity.Tag;
import com.myblog.entity.User;
import com.myblog.mapper.BlogMapper;
import com.myblog.mapper.BlogRepository;
import com.myblog.service.BlogService;
import com.myblog.service.TagService;
import com.myblog.service.UserService;
import lombok.val;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
public class SearchController {
    @Autowired
    TagService tagService;

    @Autowired
    UserService userService;

    @Autowired
    BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @RequestMapping(value = {"/article/{blogId}.html", "{blogId}"}, method = RequestMethod.GET)
    public String searchById(Model model,HttpSession session,
                             @PathVariable Integer blogId){

        //1、查询文章
        Blog blog = blogService.getBlogById(blogId);
        //文章不存在404
        if (null == blog) {
            return "error/404";
        }

        User user = userService.getUserByUserId(blog.getUserId());
        blog.setUser(user);

        model.addAttribute("blog", blog);

        if(session.getAttribute("user") == null){
            model.addAttribute("isLoggedIn",false);
        }else model.addAttribute("isLoggedIn",true);

        return "user/article";
    }

    @RequestMapping(value = "/article/search",method = RequestMethod.GET)
    public String searchByKeyWord(@RequestParam("keyword") String keyword,Model model, HttpSession session){

        // 获取会话中的搜索历史记录
        LinkedList<String> searchHistory = (LinkedList<String>) session.getAttribute("searchHistory");
        if (searchHistory == null) {
            // 如果搜索历史记录为空，创建一个新的LinkedList
            searchHistory = new LinkedList<>();
            searchHistory.addFirst(keyword);
            session.setAttribute("searchHistory", searchHistory);
        } else {
            // 如果搜索历史记录不为空，添加新关键字
            searchHistory.addFirst(keyword);
            // 限制搜索历史记录的大小
            if (searchHistory.size() > 15) {
                searchHistory.removeLast();
            }
            session.setAttribute("searchHistory", searchHistory);
        }
        // 将搜索历史记录添加到模型中
        model.addAttribute("history", searchHistory);

        List<Blog> blogList = blogRepository.findByAllFieldsContainingIgnoreCase(keyword);
        model.addAttribute("blogList",blogList);

        return "user/search";
    }

    @RequestMapping(value = {"/about/{userId}", "{userId}"}, method = RequestMethod.GET)
    public String searchUser(Model model,HttpSession session,
                             @PathVariable Integer userId){

        User user = userService.getUserByUserId(userId);

        model.addAttribute("user",user);

        List<Blog> blogs = blogService.getBlogByUserId(user.getId());

        if(blogs.isEmpty()) System.out.println("无个人博客");
        model.addAttribute("blogs",blogs);

        User cur_user = (User)session.getAttribute("user");
        if(cur_user == null || cur_user.getId() != user.getId()){
            return "user/aboutUser";
        }else{
            return "user/userhome";
        }
    }

    @RequestMapping(value = "/search/clearHistory")
    public ResponseEntity<String> clearHistory(HttpSession session,Model model){
        try {
            LinkedList<String> searchHistory = (LinkedList<String>) session.getAttribute("searchHistory");
            searchHistory.clear();
            // 将搜索历史记录添加到模型中
            model.addAttribute("history", searchHistory);
            session.setAttribute("searchHistory", searchHistory);
            return ResponseEntity.ok("历史记录已清空");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("清空历史记录失败");
        }
    }
}
