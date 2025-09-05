package com.quanlyphongtro.controller;

import com.quanlyphongtro.entity.User;
import com.quanlyphongtro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "account/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpServletRequest request,
                                 Model model) {
        if (newPassword == null || newPassword.isBlank()) {
            model.addAttribute("error", "Mật khẩu mới không được để trống");
            return "account/change-password";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Xác nhận mật khẩu không khớp");
            return "account/change-password";
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/login";
        }
        User auth = (User) session.getAttribute("authUser");
        if (auth == null) {
            return "redirect:/login";
        }

        Optional<User> updated = userService.changePassword(auth.getId(), oldPassword, newPassword);
        if (updated.isPresent()) {
            // cập nhật lại session với user mới
            session.setAttribute("authUser", updated.get());
            model.addAttribute("success", "Đổi mật khẩu thành công");
        } else {
            model.addAttribute("error", "Mật khẩu cũ không đúng hoặc tài khoản không hợp lệ");
        }
        return "account/change-password";
    }
}
