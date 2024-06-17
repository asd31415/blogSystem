package com.myblog.controller.home;

import com.myblog.entity.Blog;
import com.myblog.entity.Tag;
import com.myblog.service.BlogService;
import com.myblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CategoryContoller {

    @Autowired
    BlogService blogService;

    @Autowired
    TagService tagService;

    @RequestMapping("/user/category/{tagId}")
    public String handleTag(@PathVariable("tagId") Integer tagId, Model model, HttpSession session) {

        List<Blog> blogList = blogService.getBlogByTag(tagId);
        model.addAttribute("blogList",blogList);

        List<Tag> tagList = (List<Tag>) session.getAttribute("tags");
        model.addAttribute("tags",tagList);
        model.addAttribute("tag_select",tagId);

        return "user/category"; // 返回相应的视图
    }
}
