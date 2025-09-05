package com.quanlyphongtro.controller;

import com.quanlyphongtro.entity.User;
import com.quanlyphongtro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class RegisterController {

    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String form() {
        return "register";
    }

    @PostMapping("/register")
    public String submit(@RequestParam("username") String username,
                         @RequestParam("email") String email,
                         @RequestParam("password") String password,
                         Model model) {
        // Basic validations
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ username và password");
            return "register";
        }
        // Check exists
        Optional<User> exists = userService.findByUsername(username);
        if (exists.isPresent()) {
            model.addAttribute("error", "Username đã tồn tại");
            return "register";
        }
        // Create disabled USER
        User u = new User();
        u.setUsername(username.trim());
        u.setEmail(email);
        u.setPasswordHash(password); // raw, will be encoded in service.save(..., true)
        u.setRole(User.Role.USER);
        u.setEnabled(false);
        userService.save(u, true);
        model.addAttribute("success", "Đăng ký thành công! Tài khoản đang chờ quản trị viên duyệt.");
        return "register";
    }
}
