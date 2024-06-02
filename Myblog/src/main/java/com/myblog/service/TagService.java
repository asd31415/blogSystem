package com.myblog.service;

import com.myblog.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> listTag(Integer cnt);
}
