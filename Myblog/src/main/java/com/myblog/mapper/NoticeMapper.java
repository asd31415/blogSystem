package com.myblog.mapper;

import com.myblog.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    /**
     * 根据ID获取通知
     * @param id 通知ID
     * @return 通知对象
     */
    Notice getNoticeById(@Param("id") Integer id);

    /**
     * 删除通知
     * @param id 通知ID
     * @return 受影响的行数
     */
    Integer deleteById(@Param("id") Integer id);

    /**
     * 插入通知
     * @param notice 通知对象
     * @return 受影响的行数
     */
    Integer insert(Notice notice);

    /**
     * 更新通知
     * @param notice 通知对象
     * @return 受影响的行数
     */
    Integer update(Notice notice);

    /**
     * 获取通知列表
     * @param limit 限制条数
     * @return 通知列表
     */
    List<Notice> listNotices(@Param("limit") Integer limit);

    /**
     * 获取通知总数
     * @return 通知总数
     */
    Integer countNotices();

    /**
     * 查询通知并按创建时间排序
     * @return 通知列表
     */
    List<Notice> listNoticesByCreateTime();
}
