package com.myblog.service;

import com.myblog.entity.User;
import com.myblog.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    /**
     * @author
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    User getUserByUserId(Integer userId);

    void registerUser(User newUser);

    void loginIn(User user);

    List<User> getUserList();

    //增删改查
    void updateUser(User user);

    void deleteUser(User user);
}
