package com.usermanagement.controller;

import com.usermanagement.entity.User;
import com.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WebController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                           @RequestParam(required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
        }
        if (logout != null) {
            model.addAttribute("message", "Bạn đã đăng xuất thành công!");
        }
        return "login";
    }
    
    @GetMapping("/users")
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(required = false) String keyword,
                           Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            userPage = userService.searchUsers(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            userPage = userService.getAllUsers(pageable);
        }
        
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("pageSize", size);
        
        return "users";
    }
    
    @GetMapping("/users/new")
    public String showUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("isEdit", false);
        return "user_form";
    }
    
    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var user = userService.getUserById(id);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            return "redirect:/users";
        }
        model.addAttribute("user", user.get());
        model.addAttribute("isEdit", true);
        return "user_form";
    }
    
    @PostMapping("/users/save")
    public String saveUser(@Valid @ModelAttribute User user,
                          BindingResult bindingResult,
                          @RequestParam(required = false) Long id,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        
        // Check for email uniqueness
        if (id == null) {
            // Creating new user
            if (userService.existsByEmail(user.getEmail())) {
                bindingResult.rejectValue("email", "error.email", "Email đã tồn tại!");
            }
        } else {
            // Updating existing user
            var existingUser = userService.getUserById(id);
            if (existingUser.isPresent() && 
                !existingUser.get().getEmail().equals(user.getEmail()) &&
                userService.existsByEmail(user.getEmail())) {
                bindingResult.rejectValue("email", "error.email", "Email đã tồn tại!");
            }
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", id != null);
            return "user_form";
        }
        
        try {
            if (id != null) {
                userService.updateUser(id, user);
                redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");
            } else {
                userService.createUser(user);
                redirectAttributes.addFlashAttribute("success", "Thêm người dùng thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/users";
    }
    
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/users";
    }
    
    @PostMapping("/users/import")
    public String importUsers(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file CSV!");
            return "redirect:/users";
        }
        
        try {
            List<User> importedUsers = userService.importUsersFromCSV(file);
            redirectAttributes.addFlashAttribute("success", 
                "Import thành công " + importedUsers.size() + " người dùng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi import: " + e.getMessage());
        }
        
        return "redirect:/users";
    }
    
    @GetMapping("/users/export")
    public org.springframework.http.ResponseEntity<byte[]> exportUsers() {
        try {
            byte[] csvData = userService.exportUsersToCSV();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "users_export.csv");
            return new org.springframework.http.ResponseEntity<>(csvData, headers, 
                org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            return new org.springframework.http.ResponseEntity<>(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

