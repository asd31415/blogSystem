package com.myblog.mapper;

import com.myblog.entity.Type;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TypeMapper {

    /**
     * 添加
     *
     * @param type 分类
     * @return 影响行数
     */
    int insert(Type type);


    /**
     * 更新
     *
     * @param type 分类
     * @return 影响行数
     */
    int update(Type type);

    /**
     * 根据分类id获得分类信息
     *
     * @param id ID
     * @return 分类
     */
    Type getTypeById(Integer id);


    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    int deleteType(Integer id);

    /**
     * 查询分类总数
     *
     * @return 数量
     */
    Integer countType();

    /**
     * 获得分类列表
     *
     * @return 列表
     */
    List<Type> listType();


    /**
     * 根据标签名获取标签
     *
     * @param name 名称
     * @return 分类
     */
    Type getTypeByName(String name);

    /**
     * 获取该分类下所有文章总数
     * @param id
     * @return
     */
    Integer getBlogCountWithTypeId(Integer id);


    /**
     * 获取该分类下所有文章总数 其那台
     * @param id
     * @return
     */
    Integer getBlogCountWithTypeIdAtHome(Integer id);
}
