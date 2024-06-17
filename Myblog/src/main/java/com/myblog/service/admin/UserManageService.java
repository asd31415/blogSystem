package com.myblog.service.admin;

import com.myblog.entity.User;
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
public class UserManageService {
//    private final JdbcTemplate jdbcTemplate;

    private final UserManageRepository userManageRepository;

    @Autowired
    public UserManageService(UserManageRepository userManageRepository){
        this.userManageRepository = userManageRepository;
    }



    public List<User> getAllUsers() {
        return userManageRepository.getAllUsers();
    }

    public User getUserById(int userId) {
        return userManageRepository.getUserById(userId);
    }


    public void deleteUserById(int userId){
        userManageRepository.deleteUserById(userId);
    }


}
