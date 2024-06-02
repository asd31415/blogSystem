package com.myblog.mapper;

import com.myblog.entity.Blog;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    List<Blog> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String titleKeyword, String contentKeyword);
    List<Blog> findByDescriptionContainingIgnoreCase(String descriptionKeyword);
    List<Blog> findByCreateTimeBetween(Date startDate, Date endDate);

    @Query("SELECT b FROM Blog b JOIN b.user u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<Blog> findByUsernameContainingIgnoreCase(@Param("username") String username);

    // 综合搜索方法
    @Query("SELECT b FROM Blog b JOIN b.user u WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Blog> findByAllFieldsContainingIgnoreCase(@Param("keyword") String keyword);
}
