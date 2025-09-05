package com.quanlyphongtro.controller;

import com.quanlyphongtro.entity.KhachThue;
import com.quanlyphongtro.entity.Room;
import com.quanlyphongtro.entity.User;
import com.quanlyphongtro.service.KhachThueService;
import com.quanlyphongtro.service.RoomService;
import com.quanlyphongtro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Controller
@RequestMapping("/khachthue")
public class KhachThueController {

    private final KhachThueService service;
    private final RoomService roomService;
    private final UserService userService;

    @Autowired
    public KhachThueController(KhachThueService service, RoomService roomService, UserService userService) {
        this.service = service;
        this.roomService = roomService;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        List<KhachThue> list = service.findAll();
        model.addAttribute("items", list);
        return "khachthue/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("item", new KhachThue());
        // Phòng TRỐNG để gán
        List<Room> rooms = roomService.findAll().stream()
                .filter(r -> r.getTrangThai() == Room.Status.TRONG)
                .toList();
        model.addAttribute("rooms", rooms);
        // User role USER, chưa gán cho khách nào
        List<User> users = userService.findAll().stream()
                .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                .toList();
        Set<Integer> assigned = service.findAll().stream()
                .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                .collect(Collectors.toSet());
        List<User> availableUsers = users.stream()
                .filter(u -> !assigned.contains(u.getId()))
                .toList();
        model.addAttribute("users", availableUsers);
        return "khachthue/form";
    }

    @PostMapping
    public String create(@ModelAttribute("item") @Valid KhachThue item,
                         BindingResult bindingResult,
                         Model model,
                         @RequestParam(value = "roomId", required = false) Integer roomId,
                         @RequestParam(value = "userId", required = false) Integer userId) {
        if (bindingResult.hasErrors()) {
            // repopulate rooms & users
            List<Room> rooms = roomService.findAll().stream()
                    .filter(r -> r.getTrangThai() == Room.Status.TRONG)
                    .toList();
            model.addAttribute("rooms", rooms);
            List<User> users = userService.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                    .toList();
            Set<Integer> assigned = service.findAll().stream()
                    .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                    .collect(Collectors.toSet());
            List<User> availableUsers = users.stream()
                    .filter(u -> !assigned.contains(u.getId()))
                    .toList();
            model.addAttribute("users", availableUsers);
            return "khachthue/form";
        }
        if (service.existsByCccd(item.getCccd())) {
            bindingResult.rejectValue("cccd", "duplicate", "CCCD đã tồn tại");
            // repopulate rooms & users
            List<Room> rooms = roomService.findAll().stream()
                    .filter(r -> r.getTrangThai() == Room.Status.TRONG)
                    .toList();
            model.addAttribute("rooms", rooms);
            List<User> users = userService.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                    .toList();
            Set<Integer> assigned = service.findAll().stream()
                    .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                    .collect(Collectors.toSet());
            List<User> availableUsers = users.stream()
                    .filter(u -> !assigned.contains(u.getId()))
                    .toList();
            model.addAttribute("users", availableUsers);
            return "khachthue/form";
        }
        // Validate và gán User nếu có chọn
        if (userId != null) {
            boolean taken = service.findAll().stream()
                    .anyMatch(kt -> kt.getUser() != null && kt.getUser().getId().equals(userId));
            if (taken) {
                bindingResult.reject("userAssigned", "Tài khoản đã được gán cho khách thuê khác");
                List<Room> rooms = roomService.findAll().stream()
                        .filter(r -> r.getTrangThai() == Room.Status.TRONG)
                        .toList();
                model.addAttribute("rooms", rooms);
                List<User> users = userService.findAll().stream()
                        .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                        .toList();
                Set<Integer> assigned = service.findAll().stream()
                        .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                        .collect(Collectors.toSet());
                List<User> availableUsers = users.stream()
                        .filter(u -> !assigned.contains(u.getId()))
                        .toList();
                model.addAttribute("users", availableUsers);
                return "khachthue/form";
            }
            userService.findById(userId).ifPresent(item::setUser);
        }
        // Gán phòng nếu chọn
        if (roomId != null) {
            Room room = roomService.findById(roomId).orElse(null);
            if (room != null) {
                item.setRoom(room);
                room.setTrangThai(Room.Status.DANG_THUE);
                roomService.save(room);
            }
        }
        service.save(item);
        return "redirect:/khachthue";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        KhachThue item = service.findById(id).orElse(null);
        if (item == null) return "redirect:/khachthue";
        model.addAttribute("item", item);
        // Nạp phòng TRỐNG + phòng hiện tại của khách (nếu có) để không bị mất chọn
        List<Room> rooms = roomService.findAll().stream()
                .filter(r -> r.getTrangThai() == Room.Status.TRONG || (item.getRoom() != null && r.getId().equals(item.getRoom().getId())))
                .toList();
        model.addAttribute("rooms", rooms);
        // Nạp user khả dụng + user đang gán
        List<User> usersAll = userService.findAll().stream()
                .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                .toList();
        Set<Integer> assigned = service.findAll().stream()
                .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                .collect(Collectors.toSet());
        List<User> availableUsers = usersAll.stream()
                .filter(u -> !assigned.contains(u.getId()))
                .toList();
        if (item.getUser() != null && item.getUser().getId() != null &&
                availableUsers.stream().noneMatch(u -> u.getId().equals(item.getUser().getId()))) {
            availableUsers = new ArrayList<>(availableUsers);
            availableUsers.add(item.getUser());
        }
        model.addAttribute("users", availableUsers);
        return "khachthue/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Integer id,
                         @ModelAttribute("item") @Valid KhachThue item,
                         BindingResult bindingResult,
                         Model model,
                         @RequestParam(value = "roomId", required = false) Integer roomId,
                         @RequestParam(value = "userId", required = false) Integer userId) {
        if (bindingResult.hasErrors()) {
            // repopulate rooms & users
            KhachThue current = service.findById(id).orElse(null);
            List<Room> rooms = roomService.findAll().stream()
                    .filter(r -> r.getTrangThai() == Room.Status.TRONG || (current != null && current.getRoom() != null && r.getId().equals(current.getRoom().getId())))
                    .toList();
            model.addAttribute("rooms", rooms);
            List<User> usersAll = userService.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                    .toList();
            Set<Integer> assigned = service.findAll().stream()
                    .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                    .collect(Collectors.toSet());
            List<User> availableUsers = usersAll.stream()
                    .filter(u -> !assigned.contains(u.getId()))
                    .toList();
            if (current != null && current.getUser() != null && current.getUser().getId() != null &&
                    availableUsers.stream().noneMatch(u -> u.getId().equals(current.getUser().getId()))) {
                availableUsers = new ArrayList<>(availableUsers);
                availableUsers.add(current.getUser());
            }
            model.addAttribute("users", availableUsers);
            return "khachthue/form";
        }
        KhachThue existing = service.findById(id).orElse(null);
        if (existing == null) return "redirect:/khachthue";
        if (!existing.getCccd().equals(item.getCccd()) && service.existsByCccd(item.getCccd())) {
            bindingResult.rejectValue("cccd", "duplicate", "CCCD đã tồn tại");
            // repopulate rooms & users
            List<Room> rooms = roomService.findAll().stream()
                    .filter(r -> r.getTrangThai() == Room.Status.TRONG || (existing.getRoom() != null && r.getId().equals(existing.getRoom().getId())))
                    .toList();
            model.addAttribute("rooms", rooms);
            List<User> usersAll = userService.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                    .toList();
            Set<Integer> assigned = service.findAll().stream()
                    .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                    .collect(Collectors.toSet());
            List<User> availableUsers = usersAll.stream()
                    .filter(u -> !assigned.contains(u.getId()))
                    .toList();
            if (existing.getUser() != null && existing.getUser().getId() != null &&
                    availableUsers.stream().noneMatch(u -> u.getId().equals(existing.getUser().getId()))) {
                availableUsers = new ArrayList<>(availableUsers);
                availableUsers.add(existing.getUser());
            }
            model.addAttribute("users", availableUsers);
            return "khachthue/form";
        }
        // Xử lý gán/trả phòng
        Room previous = existing.getRoom();
        Room target = null;
        if (roomId != null) {
            target = roomService.findById(roomId).orElse(null);
        }
        // Trả phòng cũ nếu đổi hoặc bỏ chọn
        if (previous != null && (target == null || !previous.getId().equals(target.getId()))) {
            previous.setTrangThai(Room.Status.TRONG);
            roomService.save(previous);
        }
        // Gán phòng mới nếu có chọn
        if (target != null) {
            target.setTrangThai(Room.Status.DANG_THUE);
            roomService.save(target);
            item.setRoom(target);
        } else {
            item.setRoom(null);
        }
        // Gán/huỷ gán User
        if (userId != null) {
            boolean taken = service.findAll().stream()
                    .anyMatch(kt -> kt.getId() != null && !kt.getId().equals(id) && kt.getUser() != null && kt.getUser().getId().equals(userId));
            if (taken) {
                bindingResult.reject("userAssigned", "Tài khoản đã được gán cho khách thuê khác");
                // repopulate rooms & users
                List<Room> rooms = roomService.findAll().stream()
                        .filter(r -> r.getTrangThai() == Room.Status.TRONG || (existing.getRoom() != null && r.getId().equals(existing.getRoom().getId())))
                        .toList();
                model.addAttribute("rooms", rooms);
                List<User> usersAll = userService.findAll().stream()
                        .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                        .toList();
                Set<Integer> assigned = service.findAll().stream()
                        .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                        .collect(Collectors.toSet());
                List<User> availableUsers = usersAll.stream()
                        .filter(u -> !assigned.contains(u.getId()))
                        .toList();
                if (existing.getUser() != null && existing.getUser().getId() != null &&
                        availableUsers.stream().noneMatch(u -> u.getId().equals(existing.getUser().getId()))) {
                    availableUsers = new ArrayList<>(availableUsers);
                    availableUsers.add(existing.getUser());
                }
                model.addAttribute("users", availableUsers);
                return "khachthue/form";
            }
            userService.findById(userId).ifPresent(item::setUser);
        } else {
            item.setUser(null);
        }
        item.setId(id);
        service.save(item);
        return "redirect:/khachthue";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        // Trả phòng nếu khách đang gán phòng
        service.findById(id).ifPresent(kt -> {
            if (kt.getRoom() != null) {
                Room r = kt.getRoom();
                r.setTrangThai(Room.Status.TRONG);
                roomService.save(r);
            }
            service.deleteById(id);
        });
        return "redirect:/khachthue";
    }
}

