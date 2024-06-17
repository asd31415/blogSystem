package com.myblog.mapper;

import com.myblog.entity.Tag;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;
@Mapper
public interface TagRepository {
    @Select("SELECT * FROM t_tag")
    List<Tag> getAllTags();

    @Insert("INSERT into t_tag(id, name) values(#{id}, #{name})")
    void insertTags(int id, String name);

    @Select("SELECT * FROM t_tag WHERE name = #{name}")
    Tag getTagByName(@Param("name") String name);

    @Delete("DELETE FROM t_tag WHERE id = #{id}")
    void deleteTagById(@Param("id") int id);

}