package com.myblog.mapper;

import com.myblog.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;
@Mapper
public interface UserManageRepository {
    @Select("SELECT * FROM t_user")
    List<User> getAllUsers();
    void insertUser(User user);

    @Select("SELECT * FROM t_user WHERE id = #{userId}")
    User getUserById(@Param("userId") int userId);

    @Delete("DELETE FROM t_user WHERE id = #{userId}")
    void deleteUserById(@Param("userId") int userId);

}