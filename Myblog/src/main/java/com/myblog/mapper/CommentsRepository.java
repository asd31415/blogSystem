package com.myblog.mapper;

import com.myblog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentsRepository {

    @Select("SELECT * FROM t_comment where blog_id = #{id}")
    List<Comment> getCommentsByBlogId(@Param("id") int id);

}