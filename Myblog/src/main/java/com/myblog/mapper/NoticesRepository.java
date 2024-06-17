package com.myblog.mapper;

import com.myblog.entity.Manager;
import com.myblog.entity.Notice;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Mapper
public interface NoticesRepository {
    @Select("SELECT * FROM t_notice")
    List<Notice> getAllNotices();

    @Delete("DELETE FROM t_notice WHERE id = #{id}")
    void deleteNoticeById(@Param("id") int id);

    @Insert("INSERT INTO t_notice (id, title, content, createTime, updateTime, status, `order`) " +
            "VALUES (#{id}, #{title}, #{content}, #{createTime}, #{updateTime}, #{status}, #{order})")
    int save(Notice notice);
}
