package com.myblog.service;


import com.myblog.entity.Log;

import java.util.List;


public interface LogService {

    void removeAllLog();

    Integer delete(Integer id);

    Log insertOrUpdate(Log log);

    Log getById(Integer id);

    List<Log> findByBatchIds(List<Integer> ids);
}
