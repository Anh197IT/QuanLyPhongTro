package com.quanlyphongtro.controller;

import com.quanlyphongtro.entity.Room;
import com.quanlyphongtro.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public String list(Model model) {
        List<Room> rooms = roomService.findAll();
        model.addAttribute("rooms", rooms);
        return "rooms/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("statuses", Room.Status.values());
        return "rooms/form";
    }

    @PostMapping
    public String create(@ModelAttribute("room") @Valid Room room,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", Room.Status.values());
            return "rooms/form";
        }
        // Duplicate soPhong validation
        if (roomService.existsBySoPhong(room.getSoPhong())) {
            bindingResult.rejectValue("soPhong", "duplicate", "Số phòng đã tồn tại");
            model.addAttribute("statuses", Room.Status.values());
            return "rooms/form";
        }
        if (room.getTrangThai() == null) {
            room.setTrangThai(Room.Status.TRONG);
        }
        roomService.save(room);
        return "redirect:/rooms";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        Room room = roomService.findById(id).orElse(null);
        if (room == null) {
            return "redirect:/rooms";
        }
        model.addAttribute("room", room);
        model.addAttribute("statuses", Room.Status.values());
        return "rooms/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Integer id,
                         @ModelAttribute("room") @Valid Room room,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", Room.Status.values());
            return "rooms/form";
        }
        // Ensure update target exists
        Room existing = roomService.findById(id).orElse(null);
        if (existing == null) {
            return "redirect:/rooms";
        }
        // Prevent changing to duplicate soPhong of another record
        if (!existing.getSoPhong().equals(room.getSoPhong()) && roomService.existsBySoPhong(room.getSoPhong())) {
            bindingResult.rejectValue("soPhong", "duplicate", "Số phòng đã tồn tại");
            model.addAttribute("statuses", Room.Status.values());
            return "rooms/form";
        }
        // Keep the same id
        room.setId(id);
        roomService.save(room);
        return "redirect:/rooms";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Integer id, @RequestParam("value") Room.Status value) {
        Room room = roomService.findById(id).orElse(null);
        if (room != null) {
            room.setTrangThai(value);
            roomService.save(room);
        }
        return "redirect:/rooms";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        roomService.deleteById(id);
        return "redirect:/rooms";
    }
}
