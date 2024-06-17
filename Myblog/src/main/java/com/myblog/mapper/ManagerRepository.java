package com.myblog.mapper;

import com.myblog.entity.Manager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
@Mapper
public interface ManagerRepository extends JpaRepository<Manager, Integer> {
    Optional<Manager> findByUsername(String username);

    Manager save(Manager manager);

    boolean existsManagerByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
//
//    @Insert("INSERT into manager(id, username, password, created_at, created_at) values(#{id}, #{username}, #{password}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
//    void insertManager(int id, String username, String password);

//    @Modifying
//    @Insert("INSERT INTO manager(username, password, created_at, created_at) VALUES (:username, :password, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
//    void insertManager(@Param("username") String username, @Param("password") String password);

}