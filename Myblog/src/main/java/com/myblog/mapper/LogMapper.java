package com.myblog.mapper;

import com.myblog.entity.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LogMapper {


    Integer deleteAll();

    Integer insert(Log log);

    Integer update(Log log);

    Integer delete(Integer id);

    List<Log> getAll();

    Log getById(Integer id);

    List<Log> selectByIds(@Param("ids") List<Integer> ids);
}
