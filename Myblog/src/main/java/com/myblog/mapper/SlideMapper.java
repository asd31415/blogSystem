package com.myblog.mapper;

import com.myblog.entity.Slide;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SlideMapper {

    /**
     * 查找所有幻灯片
     * @return
     */
    List<Slide> findAll();


    /**
     * 添加幻灯片
     * @param slide
     * @return
     */
    Integer insert(Slide slide);

    /**
     * 修改幻灯片
     * @param slide
     * @return
     */
    Integer update(Slide slide);

    /**
     * 根据id查找幻灯片
     * @param id
     * @return
     */
    Slide getSlideById(Integer id);

    /**
     * 根据id删除幻灯片
     * @param id
     * @return
     */
    Integer deleteSlide(Integer id);
}
