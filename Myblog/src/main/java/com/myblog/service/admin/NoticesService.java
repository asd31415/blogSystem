package com.myblog.service.admin;

import com.myblog.entity.Notice;
import com.myblog.mapper.NoticesRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.alibaba.druid.sql.ast.SQLCurrentTimeExpr.Type.CURRENT_TIMESTAMP;

@Service
public class NoticesService {
    private final NoticesRepository noticesRepository;


    public NoticesService(NoticesRepository noticesRepository) {
        this.noticesRepository = noticesRepository;
    }


    public List<Notice> getAllNotices() {
        return noticesRepository.getAllNotices();
    }

    public void deleteNoticeById(@Param("id") int id){
        noticesRepository.deleteNoticeById(id);
    }

    public int insertNotice(int id, String title, String content){
        Notice notice = new Notice();
        notice.setId(id);
        notice.setContent(content);
        notice.setTitle(title);
        notice.setOrder(id);
        notice.setStatus(1);
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Date date = Date.from(zonedDateTime.toInstant());
        notice.setCreateTime(date);
        notice.setUpdateTime(date);;

        noticesRepository.save(notice);
        return 1;
    }

}
