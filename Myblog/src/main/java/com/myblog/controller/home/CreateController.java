package com.myblog.controller.home;


import com.myblog.config.LogTypeEnum;
import com.myblog.config.SystemLog;
import com.myblog.entity.ArticleRequest;
import com.myblog.entity.Blog;
import com.myblog.entity.User;
import com.myblog.service.BlogService;
import com.myblog.service.DataAnalysisService;
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

    @Autowired
    DataAnalysisService dataAnalysisService;

    @SystemLog(description = "创建文章", type = LogTypeEnum.OPERATION)
    @RequestMapping(value = "/submitMarkdown",method = RequestMethod.POST)
    public ResponseEntity<?> createArticle(@RequestBody ArticleRequest articleRequest, HttpSession session){

        //如果是新加文章
        if(articleRequest.getBlogId() == -1){
            Blog newBlog = new Blog();
            newBlog.setTitle(articleRequest.getTitle());
            newBlog.setCommentabled(articleRequest.isCommentOption());
            newBlog.setAppreciation(articleRequest.isLikeOption());
            newBlog.setPublished(articleRequest.isPublishOption());

            newBlog.setContent(articleRequest.getMarkdownContent());
            newBlog.setTagIdList(articleRequest.getSelectedTags());

            newBlog.setCreateTime(new Date());
            newBlog.setUpdateTime(new Date());

            newBlog.setDescription(articleRequest.getDescription());

            newBlog.setLikeCount(0);
            newBlog.setViews(0);
            newBlog.setCommentCount(0);

            newBlog.setRecommend(true);
            newBlog.setShareStatement(true);

            User user = (User) session.getAttribute("user");
            if(user != null){
                newBlog.setUser(user);
                newBlog.setUserId(user.getId());
            }

            if(blogService.insert(newBlog) == 0){
                return new ResponseEntity<>("保存失败！", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            //如果是修改文章
            Blog oldBlog = blogService.getBlogById(articleRequest.getBlogId(),false);

            oldBlog.setTitle(articleRequest.getTitle());
            oldBlog.setCommentabled(articleRequest.isCommentOption());
            oldBlog.setAppreciation(articleRequest.isLikeOption());
            oldBlog.setPublished(articleRequest.isPublishOption());

            oldBlog.setContent(articleRequest.getMarkdownContent());
            oldBlog.setTagIdList(articleRequest.getSelectedTags());
            oldBlog.setDescription(articleRequest.getDescription());

            oldBlog.setUpdateTime(new Date());

            if(blogService.update(oldBlog) == 0){
                return new ResponseEntity<>("保存失败！", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        dataAnalysisService.reFlushSimilarity();

        return ResponseEntity.ok().body("保存成功！");
    }
}
