package com.usermanagement.service;

import com.usermanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    Page<User> getAllUsers(Pageable pageable);
    
    Page<User> searchUsers(String keyword, Pageable pageable);
    
    Optional<User> getUserById(Long id);
    
    User createUser(User user);
    
    User updateUser(Long id, User user);
    
    void deleteUser(Long id);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByEmail(String email);
    
    List<User> importUsersFromCSV(MultipartFile file);
    
    byte[] exportUsersToCSV();
    
    List<User> getAllUsers();
}

