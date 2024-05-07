package com.myblog.mapper;

import com.myblog.entity.Link;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LinkMapper {

    Integer insert(Link link);

    Integer delete(Integer id);

    Integer update(Link link);

    List<Link> getAll();

    Link getById(Integer id);
}
