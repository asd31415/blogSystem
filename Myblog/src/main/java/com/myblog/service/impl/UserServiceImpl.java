package com.myblog.service.impl;

import com.myblog.entity.User;
import com.myblog.mapper.UserMapper;
import com.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByName(username);
    }

    @Override
    public User getUserByUserId(Integer userId) {
        return userMapper.getUserById(userId);
    }

    @Override

    public void registerUser(User newUser) {
        //设置身份 true-管理者 false-用户
        newUser.setType(false);

        //设置状态 true-在线 false-离线
        newUser.setStatus(false);

        //获取当前时间 记录注册时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
        newUser.setRegisterTime(currentDate);

        //更新时间
        newUser.setUpdateTime(currentDate);

        //设置头像地址 未实现
        newUser.setAvatar("");
        //设置描述 未实现
        newUser.setDescription("");

        //id主键自增，暂时不设计

        userMapper.insert(newUser);
    }

    @Override
    public void loginIn(User user) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());

        user.setLastLoginTime(currentDate);
        user.setStatus(true);
    }

    public List<User> getUserList(){
        return userMapper.listUser();
    }

    @Override
    public void updateUser(User user) {
        userMapper.update(user);
    }

    @Override
    public void deleteUser(User user) {
        userMapper.deleteById(user.getId());
    }
}
