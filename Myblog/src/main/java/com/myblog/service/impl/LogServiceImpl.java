package com.myblog.service.impl;

import com.myblog.entity.Log;
import com.myblog.mapper.LogMapper;
import com.myblog.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {
    @Autowired(required = false)
    private LogMapper logMapper;


    public Integer delete(Integer id){
        return logMapper.delete(id);
    }
    /**
     * 移除所有日志
     */
    @Override
    public void removeAllLog() {
        logMapper.deleteAll();
    }



    @Override
    public Log insertOrUpdate(Log entity) {
        if (entity.getId() == null) {
            logMapper.insert(entity);
        } else {
            logMapper.update(entity);
        }
        return entity;
    }

    @Override
    public Log getById(Integer id) {
        return logMapper.getById(id);
    }

    @Override
    public List<Log> findByBatchIds(List<Integer> ids) {
        return logMapper.selectByIds(ids);
    }


}
