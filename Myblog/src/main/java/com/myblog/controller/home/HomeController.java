package com.myblog.controller.home;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.myblog.entity.Blog;
import com.myblog.entity.Notice;
import com.myblog.entity.Tag;
import com.myblog.entity.User;
import com.myblog.service.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    @Autowired
    TagService tagService;

    @Autowired
    BlogService blogService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @RequestMapping("user/index")
    public String index(Model model, HttpSession session){

        //左侧栏目：获取登录用户的信息：头像/账号/文件数/点赞数
        User user = (User) session.getAttribute("User");
        model.addAttribute("User",user);

        //中间栏目：推荐文章列表,包括最新，最热门（阅读量/发布时间+评论最多），点赞最多
        List<Blog>  newBlogList = blogService.getNewestBlogs("createTime");
        model.addAttribute("blogList",newBlogList);

        //右侧栏目：公告，网站信息
        List<Notice> noticeList = noticeService.listNoticesByCreateTime();
        model.addAttribute("noticeList",noticeList);

        ArrayNode dataForm = dataAnalysisService.getLoginCountByHour();
        model.addAttribute("dataForm",dataForm);

        ArrayNode dataKeyword = dataAnalysisService.getCountOfSearch();
        model.addAttribute("dataKeyword",dataKeyword);

        return "user/index";
    }

    @RequestMapping("user/search")
    public String search(Model model,HttpSession session){

        LinkedList<String> searchHistory = (LinkedList<String>) session.getAttribute("searchHistory");
        model.addAttribute("history",searchHistory);

        List<Blog> hotBlogs = blogService.getBlogsByComment();
        model.addAttribute("blogList",hotBlogs);

        return "user/search";
    }

    @RequestMapping("user/category")
    public String category(Model model,HttpSession session){

        List<Tag> tagList = tagService.listTag(30);
        model.addAttribute("tags",tagList);
        session.setAttribute("tags",tagList);

        if(!tagList.isEmpty()){
            Tag first = tagList.get(0);
            List<Blog> blogList = blogService.getBlogByTag(first.getId());
            model.addAttribute("blogList",blogList);
            model.addAttribute("tag_select",first.getId());
        }

        return "user/category";
    }

    @RequestMapping("user/create")
    public String create(Model model,HttpSession session){

        List<Tag> tagList = tagService.listTag(20);
        model.addAttribute("tags",tagList);

        if(session.getAttribute("user") == null) return "user/login";
        return "user/create";
    }

    @RequestMapping("user/logout")
    public String logout(Model model,HttpSession session){

        if(session.getAttribute("user") != null){
            session.removeAttribute("user");
        }

        return index(model,session);
    }

    @RequestMapping("user/userhome")
    public String userhome(Model model,HttpSession session){

        User user = (User) session.getAttribute("user");
        if(user == null){
            return "user/login";
        }
        model.addAttribute("user",user);

        List<Blog> blogs = blogService.getBlogByUserId(user.getId());

        if(blogs.isEmpty()) System.out.println("无个人博客");
        model.addAttribute("blogList",blogs);

        return "user/userhome";
    }


    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                          HttpSession session) throws IOException {

        System.out.println("上传文件？");
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用用户名命名文件
        User user = (User) session.getAttribute("user");
        String uniqueFilename = user.getUsername() + fileExtension;

        // 保存文件到指定目录
        File uploadPath = new File("F:/IDEA/ideaworkplace/blogSystem/Myblog/src/main/webapp/static/images");
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        File destFile = new File(uploadPath, uniqueFilename);
        file.transferTo(destFile);

        // 保存文件
        String fileUrl = "/images/" + uniqueFilename;

        user.setAvatar(fileUrl);
        userService.updateUser(user);

        return ResponseEntity.ok().body(fileUrl);

    }


}
