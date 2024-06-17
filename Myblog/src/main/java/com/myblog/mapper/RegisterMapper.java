package com.myblog.mapper;

import com.myblog.entity.Register;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegisterMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Register record);

    int insertSelective(Register record);

    Register selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Register record);

    int updateByPrimaryKey(Register record);

    Register getRegisterByCode(String code);

    Register getRegisterByUserId(Integer userId);
}

