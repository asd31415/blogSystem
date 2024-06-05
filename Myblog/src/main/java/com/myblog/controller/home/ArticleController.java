package com.myblog.controller.home;

import com.myblog.entity.Blog;
import com.myblog.entity.Comment;
import com.myblog.entity.Tag;
import com.myblog.entity.User;
import com.myblog.mapper.CommentMapper;
import com.myblog.mapper.TagMapper;
import com.myblog.service.BlogService;
import com.myblog.service.TagService;
import com.myblog.service.UserService;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Controller
public class ArticleController {

    @Autowired
    BlogService blogService;

    @Autowired
    UserService userService;

    @Autowired
    TagService tagService;

    @Autowired
    CommentMapper commentMapper;


    @RequestMapping(value = {"article/like/{blogId}", "{blogId}"})
    public String like(@PathVariable Integer blogId, Model model, HttpSession session){

        blogService.incrBlogLikes(blogId);

        //1、查询文章
        Blog blog = blogService.getBlogById(blogId,true);

        User user = userService.getUserByUserId(blog.getUserId());
        blog.setUser(user);

        model.addAttribute("blog", blog);

        if(session.getAttribute("user") == null){
            model.addAttribute("isLoggedIn",false);
        }else model.addAttribute("isLoggedIn",true);

        List<Comment> comments = commentMapper.findByBlogId(blogId);
        model.addAttribute("comments",comments);

        return "user/article";
    }

    @RequestMapping(value = "article/comment")
    public ResponseEntity<String> comment(Model model, HttpSession session, HttpServletRequest request,
                                          @RequestBody Map<String, Object> postData){

        User user = (User) session.getAttribute("user");
        if(user == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("请登录后评论");
        }

        Comment newComment = new Comment();
        newComment.setAvatar(user.getAvatar());
        newComment.setContent((String) postData.get("content"));
        newComment.setEmail(user.getEmail());
        newComment.setNickname(user.getNickname());
        newComment.setBlogId((Integer) postData.get("blogId"));
        newComment.setIp(request.getRemoteAddr());
        newComment.setPass(1);
        newComment.setAdminComment(user.getType());

        LocalDateTime currentDateTime = LocalDateTime.now();
        Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
        newComment.setCreateTime(currentDate);

        newComment.setParentCommentId(-1);

        if(commentMapper.insertComment(newComment) == 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("评论失败");
        }

        List<Comment> comments = commentMapper.findByBlogId((Integer) postData.get("blogId"));
        Collections.reverse(comments);//让最新的评论在上面
        model.addAttribute("comments",comments);

        return ResponseEntity.status(HttpStatus.OK).body("评论成功");
    }

    @RequestMapping(value = {"article/comments/{blogId}","{blogId}"})
    public String comments(@PathVariable Integer blogId,Model model){

        System.out.println("hello");
        List<Comment> comments = commentMapper.findByBlogId(blogId);
        Collections.reverse(comments);//让最新的评论在上面

        model.addAttribute("comments",comments);

        return "user/comments";
    }

    @RequestMapping(value = {"article/edit/{blogId}","{blogId}"})
    public String edit(@PathVariable Integer blogId,Model model){

        Blog blog = blogService.getBlogById(blogId,false);
        model.addAttribute("blog",blog);

        List<Tag> tagList = tagService.listTag(100);
        model.addAttribute("tags",tagList);

        return "user/create";
    }

    @RequestMapping(value = {"article/delete/{blogId}","{blogId}"})
    public String delete(@PathVariable Integer blogId,Model model,HttpSession session){

        blogService.deleteById(blogId);

        User user = (User) session.getAttribute("user");
        if(user == null){
            return "user/login";
        }
        model.addAttribute("user",user);

        List<Blog> blogs = blogService.getBlogByUserId(user.getId());

        model.addAttribute("blogList",blogs);

        return "user/userhome";
    }

}
