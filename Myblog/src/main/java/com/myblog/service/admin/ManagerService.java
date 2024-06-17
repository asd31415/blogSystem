package com.myblog.service.admin;


import com.myblog.entity.Manager;
import com.myblog.mapper.ManagerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagerService {
    private final ManagerRepository managerRepository;

    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public Optional<Manager> findByUsername(String username) {
        return managerRepository.findByUsername(username);
    }

    public Manager createManager(String username, String password) {
        Manager manager = new Manager();
        manager.setUsername(username);
        manager.setPassword(password);
        return managerRepository.save(manager);
    }


    public boolean existsManagerByUsernameAndPassword(String username, String password) {
        return managerRepository.existsManagerByUsernameAndPassword(username, password);
    }

}