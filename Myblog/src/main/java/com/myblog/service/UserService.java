package com.myblog.service;

import com.myblog.entity.User;
import com.myblog.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;


public interface UserService {
    User getUserByUsername(String username);
}
