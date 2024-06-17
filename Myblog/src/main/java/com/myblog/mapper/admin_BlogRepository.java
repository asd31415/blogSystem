package com.myblog.mapper;

import com.myblog.entity.Blog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface admin_BlogRepository {

    @Select("SELECT * FROM t_blog")
    public List<Blog> getAllBlogs();

    //  根据用户编号筛选文章
    @Select("SELECT * FROM t_blog where user_id = #{userId}")
    List<Blog> getBlogsByUserId(int userId);

    //  筛选出点赞量最高的文章
    @Select("SELECT * FROM t_blog ORDER BY like_count DESC LIMIT 1")
    Blog getMostLikesBlog();

    //  根据文章编号筛选文章
    @Select("SELECT * FROM t_blog WHERE id = #{id}")
    Blog getBlogById(@Param("id") int id);

    //  根据文章编号删除文章
    @Delete("DELETE FROM t_blog WHERE id = #{id}")
    void deleteBlogById(@Param("id") int id);

}
