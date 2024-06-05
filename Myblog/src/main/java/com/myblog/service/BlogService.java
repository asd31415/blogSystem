package com.myblog.service;


import com.myblog.entity.Blog;
import org.springframework.stereotype.Service;

import java.util.List;


public interface BlogService {
    Integer findUserIdByBlogId(Integer id);

    //根据文章id获取文章
    Blog getBlogById(Integer id,boolean changeToHtml);

    //根据标签获取文章
    List<Blog> getBlogByTag(Integer id);

    //根据关键词获取文章
    List<Blog> getBlogByKeyword();

    //根据用户名获取文章
    List<Blog> getBlogByUserId(Integer userId);

    //获取所有文章
    List<Blog> getBlogLists();

    //最新文章
    List<Blog> getNewestBlogs(String key);

    //点赞最多
    List<Blog> getBlogsByAppreciation();

    //评论最多
    List<Blog> getBlogsByComment();

    Integer insert(Blog blog);

    void incrBlogLikes(Integer blogId);

    Integer update(Blog oldBlog);

    Integer deleteById(Integer blogId);
}
