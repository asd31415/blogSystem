package com.myblog.service.impl;

import com.myblog.entity.Blog;
import com.myblog.mapper.BlogMapper;
import com.myblog.mapper.CommentMapper;
import com.myblog.service.BlogService;
import com.myblog.util.MyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    BlogMapper blogMapper;

    @Autowired
    CommentMapper commentMapper;

    @Override
    public Integer findUserIdByBlogId(Integer id) {
        return blogMapper.findUserIdByBlogId(id);
    }

    @Override
    public Blog getBlogById(Integer id,boolean changeToHtml) {

        Blog blog = blogMapper.getBlogById(id);
        if (blog == null) {
            return null;
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog,b);

        String content = b.getContent();
        if(changeToHtml){
            b.setContent(MyUtils.markdownToHtmlExtensions(content));
        }

        b.setUserId(blogMapper.findUserIdByBlogId(id));
        b.setTags(blogMapper.getBlogTags(id));
        b.setLikeCount(blogMapper.getLikeCount(id));

        return b;
    }

    @Override
    public List<Blog> getBlogByTag(Integer id) {
        return blogMapper.getBlogByTag(id);
    }

    @Override
    public List<Blog> getBlogByKeyword() {
        return null;
    }

    @Override
    public List<Blog> getBlogByUserId(Integer userId) {
        return blogMapper.getBlogByUserId(userId);
    }

    @Override
    public List<Blog> getBlogLists() {
        return blogMapper.listBlog();
    }

    @Override
    public List<Blog> getNewestBlogs(String key) {
        return blogMapper.getNewestBlogs(key);
    }

    @Override
    public List<Blog> getBlogsByAppreciation() {
        return null;
    }

    @Override
    public List<Blog> getBlogsByComment() {
        return blogMapper.listBlogByCommentCount(10);
    }

    @Override
    public Integer insert(Blog blog) {
        return blogMapper.insert(blog);
    }

    @Override
    public void incrBlogLikes(Integer blogId) {
        blogMapper.incrBlogLikes(blogId);
    }

    @Override
    public Integer update(Blog oldBlog) {
        return blogMapper.update(oldBlog);
    }

    @Override
    public Integer deleteById(Integer blogId) {
        return blogMapper.deleteById(blogId);
    }
}
