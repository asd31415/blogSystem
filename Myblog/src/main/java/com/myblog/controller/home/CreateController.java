package com.myblog.controller.home;


import com.myblog.entity.ArticleRequest;
import com.myblog.entity.Blog;
import com.myblog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

@Controller
public class CreateController {

    @Autowired
    BlogService blogService;

    @RequestMapping(value = "/submitMarkdown",method = RequestMethod.POST)
    public ResponseEntity<?> createArticle(@RequestBody ArticleRequest articleRequest, HttpSession session){

        Blog newBlog = new Blog();
        newBlog.setTitle(articleRequest.getTitle());
        newBlog.setCommentabled(articleRequest.isCommentOption());
        newBlog.setAppreciation(articleRequest.isLikeOption());
        newBlog.setPublished(articleRequest.isPublishOption());

        newBlog.setContent(articleRequest.getMarkdownContent());
        newBlog.setTagIdList(articleRequest.getSelectedTags());

        newBlog.setCreateTime(new Date());
        newBlog.setUpdateTime(new Date());

        //newBlog.setUserId(session.getAttribute("user"));

        if(blogService.insert(newBlog) == 0){
            return new ResponseEntity<>("保存失败！", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok().body("保存成功！");
    }
}
