package com.myblog.service.admin;

import com.myblog.entity.Blog;
import com.myblog.entity.User;
import com.myblog.mapper.UserMapper;
import com.myblog.mapper.admin_BlogRepository;
import com.myblog.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogsService {

    private final admin_BlogRepository admin_blogRepository;

    @Autowired
    private  UserMapper userMapper;


    public BlogsService(admin_BlogRepository admin_blogRepository) {
        this.admin_blogRepository = admin_blogRepository;
    }


    public List<Blog> getAllBlogs() {
        List<Blog> blogList = admin_blogRepository.getAllBlogs();

        for(Blog b : blogList){
            b.setUser(userMapper.getUserById(b.getUserId()));
        }
        return blogList;
    }

    public List<Blog> getBlogsByUserId(int userId) {
        return admin_blogRepository.getBlogsByUserId(userId);
    }

    public Blog getBlogById(@Param("id") int id){
        return admin_blogRepository.getBlogById(id);
    }

    public void deleteBlogById(@Param("id") int id){
        admin_blogRepository.deleteBlogById(id);
    }

    public Blog getMostLikesBlog(){ return admin_blogRepository.getMostLikesBlog();}

}
