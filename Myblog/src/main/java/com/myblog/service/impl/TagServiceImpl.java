package com.myblog.service.impl;

import com.myblog.entity.Tag;
import com.myblog.mapper.TagMapper;
import com.myblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    TagMapper tagMapper;

    @Override
    public List<Tag> listTag(Integer cnt) {
        return tagMapper.listTag(cnt);
    }
}
