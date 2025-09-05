package com.quanlyphongtro.controller;

import com.quanlyphongtro.entity.HopDong;
import com.quanlyphongtro.entity.KhachThue;
import com.quanlyphongtro.entity.Room;
import com.quanlyphongtro.entity.User;
import com.quanlyphongtro.service.HopDongService;
import com.quanlyphongtro.service.KhachThueService;
import com.quanlyphongtro.service.RoomService;
import com.quanlyphongtro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hopdong")
public class HopDongController {

    private final HopDongService hopDongService;
    private final RoomService roomService;
    private final KhachThueService khachThueService;
    private final UserService userService;

    @Autowired
    public HopDongController(HopDongService hopDongService,
                             RoomService roomService,
                             KhachThueService khachThueService,
                             UserService userService) {
        this.hopDongService = hopDongService;
        this.roomService = roomService;
        this.khachThueService = khachThueService;
        this.userService = userService;
    }

    @GetMapping
    public String list(@RequestParam(value = "filter", required = false) String filter,
                       @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                       @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                       Model model) {
        List<HopDong> items = hopDongService.findAll();
        model.addAttribute("items", items);
        // days remaining to expiry for UI badges
        java.util.Map<Integer, Long> daysLeftMap = new java.util.HashMap<>();
        LocalDate today = LocalDate.now();
        for (HopDong it : items) {
            if (it.getNgayKetThuc() != null) {
                long days = ChronoUnit.DAYS.between(today, it.getNgayKetThuc());
                daysLeftMap.put(it.getId(), days);
            }
        }
        int warnDays = 15;
        long expiringCount = items.stream()
                .filter(h -> h.getTrangThai() == HopDong.TrangThai.DANG_THUE)
                .filter(h -> daysLeftMap.containsKey(h.getId()))
                .map(h -> daysLeftMap.get(h.getId()))
                .filter(d -> d != null && d >= 0 && d <= warnDays)
                .count();
        long overdueCount = items.stream()
                .filter(h -> h.getTrangThai() == HopDong.TrangThai.DANG_THUE)
                .filter(h -> daysLeftMap.containsKey(h.getId()))
                .map(h -> daysLeftMap.get(h.getId()))
                .filter(d -> d != null && d < 0)
                .count();
        if (filter != null && !filter.isBlank()) {
            if ("expiring".equalsIgnoreCase(filter)) {
                items = items.stream()
                        .filter(h -> h.getTrangThai() == HopDong.TrangThai.DANG_THUE)
                        .filter(h -> daysLeftMap.containsKey(h.getId()))
                        .filter(h -> {
                            Long d = daysLeftMap.get(h.getId());
                            return d != null && d >= 0 && d <= warnDays;
                        })
                        .toList();
            } else if ("overdue".equalsIgnoreCase(filter)) {
                items = items.stream()
                        .filter(h -> h.getTrangThai() == HopDong.TrangThai.DANG_THUE)
                        .filter(h -> daysLeftMap.containsKey(h.getId()))
                        .filter(h -> {
                            Long d = daysLeftMap.get(h.getId());
                            return d != null && d < 0;
                        })
                        .toList();
            }
            model.addAttribute("items", items);
        }
        // Pagination
        int totalItems = items.size();
        int pageSize = Math.max(1, size);
        int totalPages = (int) Math.ceil(totalItems / (double) pageSize);
        int currentPage = Math.min(Math.max(0, page), Math.max(0, totalPages - 1));
        int from = Math.min(currentPage * pageSize, totalItems);
        int to = Math.min(from + pageSize, totalItems);
        List<HopDong> pageItems = items.subList(from, to);
        model.addAttribute("items", pageItems);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", pageSize);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("daysLeftMap", daysLeftMap);
        model.addAttribute("warnDays", warnDays);
        model.addAttribute("expiringCount", expiringCount);
        model.addAttribute("overdueCount", overdueCount);
        model.addAttribute("filter", filter);
        return "hopdong/list";
    }

    @GetMapping("/mine")
    public String myContracts(@RequestParam(value = "filter", required = false) String filter,
                              @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                              @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                              HttpSession session, Model model) {
        User auth = (User) session.getAttribute("authUser");
        if (auth == null) return "redirect:/login";
        Optional<KhachThue> opt = khachThueService.findByUser(auth);
        List<HopDong> items = opt.map(hopDongService::findByKhach).orElseGet(List::of);
        model.addAttribute("items", items);
        model.addAttribute("mine", true);
        // days remaining
        java.util.Map<Integer, Long> daysLeftMap = new java.util.HashMap<>();
        LocalDate today = LocalDate.now();
        for (HopDong it : items) {
            if (it.getNgayKetThuc() != null) {
                long days = ChronoUnit.DAYS.between(today, it.getNgayKetThuc());
                daysLeftMap.put(it.getId(), days);
            }
        }
        int warnDays = 15;
        long expiringCount = items.stream()
                .filter(h -> h.getTrangThai() == HopDong.TrangThai.DANG_THUE)
                .filter(h -> daysLeftMap.containsKey(h.getId()))
                .map(h -> daysLeftMap.get(h.getId()))
                .filter(d -> d != null && d >= 0 && d <= warnDays)
                .count();
        long overdueCount = items.stream()
                .filter(h -> h.getTrangThai() == HopDong.TrangThai.DANG_THUE)
                .filter(h -> daysLeftMap.containsKey(h.getId()))
                .map(h -> daysLeftMap.get(h.getId()))
                .filter(d -> d != null && d < 0)
                .count();
        if (filter != null && !filter.isBlank()) {
            if ("expiring".equalsIgnoreCase(filter)) {
                items = items.stream()
                        .filter(h -> h.getTrangThai() == HopDong.TrangThai.DANG_THUE)
                        .filter(h -> daysLeftMap.containsKey(h.getId()))
                        .filter(h -> {
                            Long d = daysLeftMap.get(h.getId());
                            return d != null && d >= 0 && d <= warnDays;
                        })
                        .toList();
            } else if ("overdue".equalsIgnoreCase(filter)) {
                items = items.stream()
                        .filter(h -> h.getTrangThai() == HopDong.TrangThai.DANG_THUE)
                        .filter(h -> daysLeftMap.containsKey(h.getId()))
                        .filter(h -> {
                            Long d = daysLeftMap.get(h.getId());
                            return d != null && d < 0;
                        })
                        .toList();
            }
            model.addAttribute("items", items);
        }
        // Pagination
        int totalItems = items.size();
        int pageSize = Math.max(1, size);
        int totalPages = (int) Math.ceil(totalItems / (double) pageSize);
        int currentPage = Math.min(Math.max(0, page), Math.max(0, totalPages - 1));
        int from = Math.min(currentPage * pageSize, totalItems);
        int to = Math.min(from + pageSize, totalItems);
        List<HopDong> pageItems = items.subList(from, to);
        model.addAttribute("items", pageItems);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", pageSize);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("daysLeftMap", daysLeftMap);
        model.addAttribute("warnDays", warnDays);
        model.addAttribute("expiringCount", expiringCount);
        model.addAttribute("overdueCount", overdueCount);
        model.addAttribute("filter", filter);
        return "hopdong/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        HopDong hd = new HopDong();
        hd.setNgayBatDau(LocalDate.now());
        model.addAttribute("item", hd);
        // Phòng TRỐNG để lập hợp đồng
        List<Room> rooms = roomService.findAll().stream()
                .filter(r -> r.getTrangThai() == Room.Status.TRONG)
                .toList();
        model.addAttribute("rooms", rooms);
        // Tất cả khách thuê
        List<KhachThue> khachs = khachThueService.findAll();
        model.addAttribute("khachs", khachs);
        // User khả dụng (USER + enabled) chưa gán cho khách nào
        List<User> users = userService.findAll().stream()
                .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                .toList();
        Set<Integer> assigned = khachThueService.findAll().stream()
                .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                .collect(Collectors.toSet());
        List<User> availableUsers = users.stream().filter(u -> !assigned.contains(u.getId())).toList();
        model.addAttribute("users", availableUsers);
        return "hopdong/form";
    }

    @PostMapping
    public String create(@ModelAttribute("item") @Valid HopDong item,
                         BindingResult bindingResult,
                         @RequestParam("roomId") Integer roomId,
                         @RequestParam("khachId") Integer khachId,
                         @RequestParam(value = "userId", required = false) Integer userId,
                         @RequestParam(value = "durationMonths", required = false) Integer durationMonths,
                         Model model) {
        if (bindingResult.hasErrors()) {
            // repopulate
            List<Room> rooms = roomService.findAll().stream()
                    .filter(r -> r.getTrangThai() == Room.Status.TRONG)
                    .toList();
            model.addAttribute("rooms", rooms);
            List<KhachThue> khachs = khachThueService.findAll();
            model.addAttribute("khachs", khachs);
            List<User> users = userService.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                    .toList();
            Set<Integer> assigned = khachThueService.findAll().stream()
                    .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                    .collect(Collectors.toSet());
            List<User> availableUsers = users.stream().filter(u -> !assigned.contains(u.getId())).toList();
            model.addAttribute("users", availableUsers);
            return "hopdong/form";
        }
        Room room = roomService.findById(roomId).orElse(null);
        KhachThue khach = khachThueService.findById(khachId).orElse(null);
        if (room == null || khach == null) {
            return "redirect:/hopdong";
        }
        // Nếu khách chưa có tài khoản và có chọn userId, gán tài khoản này cho khách (đảm bảo không bị trùng)
        if (khach.getUser() == null && userId != null) {
            boolean taken = khachThueService.findAll().stream()
                    .anyMatch(kt -> kt.getUser() != null && kt.getUser().getId().equals(userId));
            if (!taken) {
                userService.findById(userId).ifPresent(u -> {
                    khach.setUser(u);
                    khachThueService.save(khach);
                });
            }
        }
        // Nếu có chọn kỳ hạn (tháng), tự tính ngày kết thúc từ ngày bắt đầu
        if (durationMonths != null && durationMonths > 0 && item.getNgayBatDau() != null) {
            item.setNgayKetThuc(item.getNgayBatDau().plusMonths(durationMonths));
        }

        // Cập nhật liên kết và trạng thái phòng
        item.setPhong(room);
        item.setKhach(khach);
        room.setTrangThai(Room.Status.DANG_THUE);
        roomService.save(room);
        // Đồng bộ khách thuê
        khach.setRoom(room);
        khachThueService.save(khach);

        hopDongService.save(item);
        return "redirect:/hopdong";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        HopDong item = hopDongService.findById(id).orElse(null);
        if (item == null) return "redirect:/hopdong";
        model.addAttribute("item", item);
        // Cho phép đổi phòng: chỉ phòng TRỐNG + phòng hiện tại
        List<Room> rooms = roomService.findAll().stream()
                .filter(r -> r.getTrangThai() == Room.Status.TRONG || r.getId().equals(item.getPhong().getId()))
                .toList();
        model.addAttribute("rooms", rooms);
        // Danh sách khách thuê (tất cả + khách hiện tại)
        List<KhachThue> khachs = khachThueService.findAll();
        model.addAttribute("khachs", khachs);
        // Users khả dụng để gán nếu khách hiện tại chưa có user
        List<User> users = userService.findAll().stream()
                .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                .toList();
        Set<Integer> assigned = khachThueService.findAll().stream()
                .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                .collect(Collectors.toSet());
        List<User> availableUsers = users.stream().filter(u -> !assigned.contains(u.getId())).toList();
        model.addAttribute("users", availableUsers);
        return "hopdong/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Integer id,
                         @ModelAttribute("item") @Valid HopDong form,
                         BindingResult bindingResult,
                         @RequestParam("roomId") Integer roomId,
                         @RequestParam("khachId") Integer khachId,
                         @RequestParam(value = "userId", required = false) Integer userId,
                         @RequestParam(value = "durationMonths", required = false) Integer durationMonths,
                         Model model) {
        if (bindingResult.hasErrors()) {
            HopDong current = hopDongService.findById(id).orElse(null);
            List<Room> rooms = roomService.findAll().stream()
                    .filter(r -> r.getTrangThai() == Room.Status.TRONG || (current != null && current.getPhong() != null && r.getId().equals(current.getPhong().getId())))
                    .toList();
            model.addAttribute("rooms", rooms);
            List<KhachThue> khachs = khachThueService.findAll();
            model.addAttribute("khachs", khachs);
            List<User> users = userService.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.USER && u.isEnabled())
                    .toList();
            Set<Integer> assigned = khachThueService.findAll().stream()
                    .map(KhachThue::getUser).filter(Objects::nonNull).map(User::getId)
                    .collect(Collectors.toSet());
            List<User> availableUsers = users.stream().filter(u -> !assigned.contains(u.getId())).toList();
            model.addAttribute("users", availableUsers);
            return "hopdong/form";
        }
        HopDong existing = hopDongService.findById(id).orElse(null);
        if (existing == null) return "redirect:/hopdong";

        Room prevRoom = existing.getPhong();
        KhachThue prevKhach = existing.getKhach();

        Room targetRoom = roomService.findById(roomId).orElse(null);
        KhachThue targetKhach = khachThueService.findById(khachId).orElse(null);
        if (targetRoom == null || targetKhach == null) return "redirect:/hopdong";

        // Nếu khách được chọn chưa có user và có userId, gán nếu chưa bị dùng
        if (targetKhach.getUser() == null && userId != null) {
            boolean taken = khachThueService.findAll().stream()
                    .anyMatch(kt -> kt.getUser() != null && kt.getUser().getId().equals(userId));
            if (!taken) {
                userService.findById(userId).ifPresent(u -> {
                    targetKhach.setUser(u);
                    khachThueService.save(targetKhach);
                });
            }
        }
        // Trả phòng cũ nếu đổi phòng
        if (!prevRoom.getId().equals(targetRoom.getId())) {
            prevRoom.setTrangThai(Room.Status.TRONG);
            roomService.save(prevRoom);
            targetRoom.setTrangThai(Room.Status.DANG_THUE);
            roomService.save(targetRoom);
        }
        // Cập nhật liên kết khách ↔ phòng
        if (prevKhach != null && (targetKhach == null || !prevKhach.getId().equals(targetKhach.getId()))) {
            if (prevKhach.getRoom() != null && prevKhach.getRoom().getId().equals(prevRoom.getId())) {
                prevKhach.setRoom(null);
                khachThueService.save(prevKhach);
            }
        }
        targetKhach.setRoom(targetRoom);
        khachThueService.save(targetKhach);

        existing.setPhong(targetRoom);
        existing.setKhach(targetKhach);
        existing.setNgayBatDau(form.getNgayBatDau());
        // Nếu có chọn kỳ hạn (tháng), tự tính ngày kết thúc từ ngày bắt đầu
        if (durationMonths != null && durationMonths > 0 && form.getNgayBatDau() != null) {
            existing.setNgayKetThuc(form.getNgayBatDau().plusMonths(durationMonths));
        } else {
            existing.setNgayKetThuc(form.getNgayKetThuc());
        }
        existing.setGiaPhong(form.getGiaPhong());
        existing.setTienCoc(form.getTienCoc());
        existing.setTrangThai(form.getTrangThai());

        hopDongService.save(existing);
        return "redirect:/hopdong";
    }

    @GetMapping("/{id}/end")
    public String endContract(@PathVariable("id") Integer id) {
        HopDong hd = hopDongService.findById(id).orElse(null);
        if (hd == null) return "redirect:/hopdong";
        // Kết thúc hợp đồng
        hd.setTrangThai(HopDong.TrangThai.DA_KET_THUC);
        if (hd.getNgayKetThuc() == null) hd.setNgayKetThuc(LocalDate.now());
        hopDongService.save(hd);
        // Trả phòng và bỏ liên kết khách
        Room r = hd.getPhong();
        r.setTrangThai(Room.Status.TRONG);
        roomService.save(r);
        KhachThue k = hd.getKhach();
        if (k != null && k.getRoom() != null && k.getRoom().getId().equals(r.getId())) {
            k.setRoom(null);
            khachThueService.save(k);
        }
        // Vô hiệu hóa tài khoản User (nếu có) khi hợp đồng kết thúc
        if (k != null && k.getUser() != null) {
            User u = k.getUser();
            if (u.isEnabled()) {
                u.setEnabled(false);
                userService.save(u, false);
            }
        }
        return "redirect:/hopdong";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        hopDongService.findById(id).ifPresent(hd -> {
            // Nếu còn đang thuê thì trả phòng
            if (hd.getTrangThai() == HopDong.TrangThai.DANG_THUE) {
                Room r = hd.getPhong();
                r.setTrangThai(Room.Status.TRONG);
                roomService.save(r);
                KhachThue k = hd.getKhach();
                if (k.getRoom() != null && k.getRoom().getId().equals(r.getId())) {
                    k.setRoom(null);
                    khachThueService.save(k);
                }
            }
            hopDongService.deleteById(id);
        });
        return "redirect:/hopdong";
    }
}
