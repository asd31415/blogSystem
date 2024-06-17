package com.myblog.service;

import com.myblog.entity.Notice;

import java.util.List;

public interface NoticeService {

    /**
     * 根据ID获取通知
     * @param id 通知ID
     * @return 通知对象
     */
    Notice getNoticeById(Integer id);

    /**
     * 删除通知
     * @param id 通知ID
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 插入通知
     * @param notice 通知对象
     * @return 是否成功
     */
    boolean insert(Notice notice);

    /**
     * 更新通知
     * @param notice 通知对象
     * @return 是否成功
     */
    boolean update(Notice notice);

    /**
     * 获取通知列表
     * @param limit 限制条数
     * @return 通知列表
     */
    List<Notice> listNotices(Integer limit);

    /**
     * 获取通知总数
     * @return 通知总数
     */
    int countNotices();

    /**
     * 查询通知并按创建时间排序
     * @return 通知列表
     */
    List<Notice> listNoticesByCreateTime();
}
