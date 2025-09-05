package com.quanlyphongtro.controller;

import com.quanlyphongtro.entity.User;
import com.quanlyphongtro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(value = "status", required = false, defaultValue = "all") String status,
                       @RequestParam(value = "q", required = false) String q,
                       Model model) {
        List<User> items = service.findAll();
        if (q != null && !q.isBlank()) {
            String s = q.toLowerCase();
            items = items.stream().filter(u ->
                    (u.getUsername() != null && u.getUsername().toLowerCase().contains(s)) ||
                            (u.getEmail() != null && u.getEmail().toLowerCase().contains(s))
            ).collect(Collectors.toList());
        }
        switch (status) {
            case "pending":
                items = items.stream().filter(u -> !u.isEnabled()).collect(Collectors.toList());
                break;
            case "active":
                items = items.stream().filter(User::isEnabled).collect(Collectors.toList());
                break;
            default:
                // all
        }
        model.addAttribute("items", items);
        model.addAttribute("status", status);
        model.addAttribute("q", q);
        model.addAttribute("roles", new User.Role[]{User.Role.ADMIN, User.Role.USER});
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        User u = new User();
        u.setEnabled(true);
        model.addAttribute("item", u);
        model.addAttribute("roles", new User.Role[]{User.Role.ADMIN, User.Role.USER});
        return "users/form";
    }

    @PostMapping
    public String create(@ModelAttribute("item") User item) {
        // passwordHash field contains raw password from form; encode=true
        service.save(item, true);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Integer id, Model model) {
        User u = service.findById(id).orElse(null);
        if (u == null) return "redirect:/users";
        // for security, not expose hash; empty to keep
        u.setPasswordHash("");
        model.addAttribute("item", u);
        model.addAttribute("roles", new User.Role[]{User.Role.ADMIN, User.Role.USER});
        return "users/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Integer id, @ModelAttribute("item") User form) {
        User u = service.findById(id).orElse(null);
        if (u == null) return "redirect:/users";
        u.setFullName(form.getFullName());
        u.setEmail(form.getEmail());
        u.setRole(form.getRole());
        u.setEnabled(form.isEnabled());
        if (form.getPasswordHash() != null && !form.getPasswordHash().isBlank()) {
            u.setPasswordHash(form.getPasswordHash());
            // Admin reset password -> force user to change on next login
            u.setMustChangePassword(true);
            service.save(u, true);
        } else {
            service.save(u, false);
        }
        return "redirect:/users";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        service.deleteById(id);
        return "redirect:/users";
    }

    @PostMapping("/{id}/enable")
    public String enable(@PathVariable("id") Integer id) {
        User u = service.findById(id).orElse(null);
        if (u != null) { u.setEnabled(true); service.save(u, false); }
        return "redirect:/users";
    }

    @PostMapping("/{id}/disable")
    public String disable(@PathVariable("id") Integer id) {
        User u = service.findById(id).orElse(null);
        if (u != null) { u.setEnabled(false); service.save(u, false); }
        return "redirect:/users";
    }

    @PostMapping("/{id}/role")
    public String updateRole(@PathVariable("id") Integer id, @RequestParam("role") User.Role role) {
        User u = service.findById(id).orElse(null);
        if (u != null) { u.setRole(role); service.save(u, false); }
        return "redirect:/users";
    }
}
