package com.myblog.service.admin;

import com.myblog.entity.Tag;
import com.myblog.entity.User;
import com.myblog.mapper.TagRepository;
import com.myblog.mapper.TagRepository;
import com.myblog.mapper.UserManageRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TagsService {

    private final TagRepository tagRepository;

    @Autowired
    public TagsService(TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAllTags() {
        return tagRepository.getAllTags();
    }

    public Tag getTagByName(String name) {
        return tagRepository.getTagByName(name);
    }

    public void deleteTagById(int id){
        tagRepository.deleteTagById(id);
    }

    public void insertTags(int id, String name){
        tagRepository.insertTags(id, name);
    }

}
