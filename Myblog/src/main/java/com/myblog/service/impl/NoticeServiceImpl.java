package com.myblog.service.impl;

import com.myblog.entity.Notice;
import com.myblog.mapper.NoticeMapper;
import com.myblog.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public Notice getNoticeById(Integer id) {
        return noticeMapper.getNoticeById(id);
    }

    @Override
    public boolean deleteById(Integer id) {
        return noticeMapper.deleteById(id) > 0;
    }

    @Override
    public boolean insert(Notice notice) {
        return noticeMapper.insert(notice) > 0;
    }

    @Override
    public boolean update(Notice notice) {
        return noticeMapper.update(notice) > 0;
    }

    @Override
    public List<Notice> listNotices(Integer limit) {
        return noticeMapper.listNotices(limit);
    }

    @Override
    public int countNotices() {
        return noticeMapper.countNotices();
    }

    @Override
    public List<Notice> listNoticesByCreateTime() {
        return noticeMapper.listNoticesByCreateTime();
    }
}
