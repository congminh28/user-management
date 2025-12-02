package com.usermanagement.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.usermanagement.entity.User;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers(pageable);
        }
        return userRepository.searchUsers(keyword.trim(), pageable);
    }
    
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public User createUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Check if email is being changed and if it's already taken
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        
        // Only update password if a new one is provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public List<User> importUsersFromCSV(MultipartFile file) {
        List<User> importedUsers = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            List<String[]> records = reader.readAll();
            
            if (records.isEmpty()) {
                throw new RuntimeException("File CSV rỗng!");
            }
            
            int startIndex = 0;
            // Check if first row is header (contains "name", "email", "password" keywords)
            String[] firstRow = records.get(0);
            if (firstRow.length > 0) {
                String firstCell = firstRow[0].toLowerCase().trim();
                if (firstCell.contains("name") || firstCell.contains("email") || 
                    firstCell.contains("password") || firstCell.contains("id")) {
                    startIndex = 1; // Skip header row
                }
            }
            
            // Process data rows (format: Name,Email,Password)
            for (int i = startIndex; i < records.size(); i++) {
                String[] record = records.get(i);
                
                // Skip empty rows
                if (record.length == 0 || (record.length == 1 && record[0].trim().isEmpty())) {
                    continue;
                }
                
                // Expected format: Name,Email,Password (3 columns)
                if (record.length >= 3) {
                    String name = record[0].trim();
                    String email = record[1].trim();
                    String password = record[2].trim();
                    
                    // Validate and create user
                    if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                        // Check if email already exists
                        if (!userRepository.existsByEmail(email)) {
                            User user = new User(name, email, password);
                            user.setPassword(passwordEncoder.encode(password));
                            importedUsers.add(userRepository.save(user));
                        }
                    }
                } else if (record.length == 2) {
                    // Try format: Name,Email (without password - will use default)
                    String name = record[0].trim();
                    String email = record[1].trim();
                    
                    if (!name.isEmpty() && !email.isEmpty() && !userRepository.existsByEmail(email)) {
                        User user = new User(name, email, "default123"); // Default password
                        user.setPassword(passwordEncoder.encode("default123"));
                        importedUsers.add(userRepository.save(user));
                    }
                }
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Error importing CSV file: " + e.getMessage(), e);
        }
        
        return importedUsers;
    }
    
    @Override
    public byte[] exportUsersToCSV() {
        List<User> users = userRepository.findAll();
        
        try (StringWriter stringWriter = new StringWriter();
             CSVWriter writer = new CSVWriter(stringWriter)) {
            
            // Write header (without ID)
            writer.writeNext(new String[]{"Name", "Email", "Created At", "Updated At"});
            
            // Write data (without ID)
            for (User user : users) {
                writer.writeNext(new String[]{
                    user.getName(),
                    user.getEmail(),
                    user.getCreatedAt() != null ? user.getCreatedAt().toString() : "",
                    user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : ""
                });
            }
            
            return stringWriter.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error exporting CSV file: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

