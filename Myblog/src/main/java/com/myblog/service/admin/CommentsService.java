package com.myblog.service.admin;

import com.myblog.entity.Comment;
import com.myblog.mapper.CommentsRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsService {
    private final CommentsRepository commentsRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    public List<Comment> getCommentsByBlogId(@Param("id") int id) {
        return commentsRepository.getCommentsByBlogId(id);
    }



}