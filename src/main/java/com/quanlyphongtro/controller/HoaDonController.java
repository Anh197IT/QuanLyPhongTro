package com.quanlyphongtro.controller;

import com.quanlyphongtro.entity.HoaDon;
import com.quanlyphongtro.entity.HopDong;
import com.quanlyphongtro.entity.KhachThue;
import com.quanlyphongtro.entity.User;
import com.quanlyphongtro.service.HoaDonService;
import com.quanlyphongtro.service.HopDongService;
import com.quanlyphongtro.service.KhachThueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.math.RoundingMode;

@Controller
@RequestMapping("/hoadon")
public class HoaDonController {

    private final HoaDonService hoaDonService;
    private final HopDongService hopDongService;
    private final KhachThueService khachThueService;

    // VietQR cấu hình - vui lòng thay bằng thông tin tài khoản nhận tiền của bạn
    private static final String BANK_CODE = "VCB"; // ví dụ: VCB, MBB, BIDV...
    private static final String ACCOUNT_NO = "123456789"; // số tài khoản nhận
    private static final String ACCOUNT_NAME = "TEN CHU TAI KHOAN"; // Tên chủ tài khoản (không dấu càng tốt)

    // Đơn giá mặc định (có thể chuyển ra cấu hình sau)
    private static final BigDecimal DEFAULT_DG_DIEN = new BigDecimal("3500");
    private static final BigDecimal DEFAULT_DG_NUOC = new BigDecimal("15000");

    @Autowired
    public HoaDonController(HoaDonService hoaDonService, HopDongService hopDongService, KhachThueService khachThueService) {
        this.hoaDonService = hoaDonService;
        this.hopDongService = hopDongService;
        this.khachThueService = khachThueService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", hoaDonService.findAll());
        return "hoadon/list";
    }

    @GetMapping("/mine")
    public String myBills(HttpSession session, Model model) {
        User auth = (User) session.getAttribute("authUser");
        if (auth == null) return "redirect:/login";
        Optional<KhachThue> opt = khachThueService.findByUser(auth);
        List<HopDong> contracts = opt.map(h -> hopDongService.findByKhach(h)).orElseGet(List::of);
        List<HoaDon> items = contracts.stream()
                .flatMap(hd -> hoaDonService.findByHopDong(hd).stream())
                .collect(Collectors.toList());
        model.addAttribute("items", items);
        model.addAttribute("mine", true);
        return "hoadon/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(value = "hopDongId", required = false) Integer hopDongId,
                             Model model) {
        HoaDon hd = new HoaDon();
        // gợi ý tháng hiện tại YYYY-MM
        String ym = LocalDate.now().toString().substring(0,7);
        hd.setThangNam(ym);
        if (hopDongId != null) {
            hopDongService.findById(hopDongId).ifPresent(hd::setHopDong);
            // auto fill tienPhong
            hd.setTienPhong(hd.getHopDong() != null ? hd.getHopDong().getGiaPhong() : BigDecimal.ZERO);
            // prefill chi so cu từ hóa đơn gần nhất nếu có
            if (hd.getHopDong() != null) {
                hoaDonService.findLatestByHopDong(hd.getHopDong()).ifPresent(prev -> {
                    hd.setChiSoDienCu(prev.getChiSoDienMoi());
                    hd.setChiSoNuocCu(prev.getChiSoNuocMoi());
                });
            }
        }
        // Prefill đơn giá mặc định nếu chưa có
        if (hd.getDonGiaDien() == null) hd.setDonGiaDien(DEFAULT_DG_DIEN);
        if (hd.getDonGiaNuoc() == null) hd.setDonGiaNuoc(DEFAULT_DG_NUOC);
        model.addAttribute("item", hd);
        List<HopDong> contracts = hopDongService.findAll().stream()
                .filter(h -> h.getTrangThai() == HopDong.TrangThai.DANG_THUE)
                .toList();
        model.addAttribute("contracts", contracts);
        return "hoadon/form";
    }

    @PostMapping
    public String create(@ModelAttribute("item") @Valid HoaDon item,
                         BindingResult bindingResult,
                         @RequestParam("hopDongId") Integer hopDongId,
                         Model model) {
        Optional<HopDong> opt = hopDongService.findById(hopDongId);
        if (opt.isEmpty()) return "redirect:/hoadon";
        item.setHopDong(opt.get());
        // chống trùng tháng
        if (hoaDonService.existsByHopDongAndThangNam(item.getHopDong(), item.getThangNam())) {
            bindingResult.rejectValue("thangNam", "duplicate", "Đã có hóa đơn cho tháng này");
        }
        // mặc định tiền phòng theo hợp đồng nếu chưa nhập
        if (item.getTienPhong() == null) item.setTienPhong(item.getHopDong().getGiaPhong());
        // gán đơn giá mặc định nếu trống
        if (item.getDonGiaDien() == null) item.setDonGiaDien(DEFAULT_DG_DIEN);
        if (item.getDonGiaNuoc() == null) item.setDonGiaNuoc(DEFAULT_DG_NUOC);
        // tính tiền điện nước và tổng cộng
        calcTotals(item);
        if (bindingResult.hasErrors()) {
            model.addAttribute("contracts", hopDongService.findAll());
            return "hoadon/form";
        }
        hoaDonService.save(item);
        return "redirect:/hoadon";
    }

    @PostMapping("/generate")
    public String generateMonthly(@RequestParam(value = "thang", required = false) String thang) {
        // default to current month YYYY-MM
        String ym = (thang == null || thang.isBlank()) ? LocalDate.now().toString().substring(0,7) : thang;
        // get all active contracts
        List<HopDong> contracts = hopDongService.findAll().stream()
                .filter(h -> h.getTrangThai() == HopDong.TrangThai.DANG_THUE)
                .toList();
        for (HopDong hd : contracts) {
            // skip if exists
            if (hoaDonService.existsByHopDongAndThangNam(hd, ym)) continue;
            HoaDon b = new HoaDon();
            b.setHopDong(hd);
            b.setThangNam(ym);
            // default values
            b.setTienPhong(hd.getGiaPhong());
            // prefill previous meter readings if any
            hoaDonService.findLatestByHopDong(hd).ifPresent(prev -> {
                b.setChiSoDienCu(prev.getChiSoDienMoi());
                b.setChiSoNuocCu(prev.getChiSoNuocMoi());
            });
            // đơn giá mặc định
            b.setDonGiaDien(DEFAULT_DG_DIEN);
            b.setDonGiaNuoc(DEFAULT_DG_NUOC);
            // compute amounts (will be zero if donGia not provided and moi==cu)
            calcTotals(b);
            hoaDonService.save(b);
        }
        return "redirect:/hoadon";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        HoaDon item = hoaDonService.findById(id).orElse(null);
        if (item == null) return "redirect:/hoadon";
        model.addAttribute("item", item);
        model.addAttribute("contracts", hopDongService.findAll());
        return "hoadon/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Integer id,
                         @ModelAttribute("item") @Valid HoaDon form,
                         BindingResult bindingResult,
                         @RequestParam("hopDongId") Integer hopDongId,
                         Model model) {
        HoaDon existing = hoaDonService.findById(id).orElse(null);
        if (existing == null) return "redirect:/hoadon";
        HopDong contract = hopDongService.findById(hopDongId).orElse(null);
        if (contract == null) return "redirect:/hoadon";

        existing.setHopDong(contract);
        existing.setThangNam(form.getThangNam());
        existing.setChiSoDienCu(form.getChiSoDienCu());
        existing.setChiSoDienMoi(form.getChiSoDienMoi());
        existing.setChiSoNuocCu(form.getChiSoNuocCu());
        existing.setChiSoNuocMoi(form.getChiSoNuocMoi());
        existing.setDonGiaDien(form.getDonGiaDien());
        existing.setDonGiaNuoc(form.getDonGiaNuoc());
        existing.setTienPhong(form.getTienPhong());
        existing.setPhiDichVu(form.getPhiDichVu());
        existing.setTrangThai(form.getTrangThai());
        calcTotals(existing);

        if (bindingResult.hasErrors()) {
            model.addAttribute("contracts", hopDongService.findAll());
            return "hoadon/form";
        }
        hoaDonService.save(existing);
        return "redirect:/hoadon";
    }

    @GetMapping("/{id}/pay")
    public String pay(@PathVariable("id") Integer id) {
        hoaDonService.findById(id).ifPresent(b -> {
            b.setTrangThai(HoaDon.TrangThai.DA_THANH_TOAN);
            calcTotals(b);
            hoaDonService.save(b);
        });
        return "redirect:/hoadon";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        hoaDonService.deleteById(id);
        return "redirect:/hoadon";
    }

    // Hiển thị QR thanh toán cho USER (hoặc ADMIN)
    @GetMapping("/{id}/vietqr")
    public String vietqr(@PathVariable("id") Integer id, HttpSession session, Model model) {
        Optional<HoaDon> opt = hoaDonService.findById(id);
        if (opt.isEmpty()) return "redirect:/hoadon";
        HoaDon bill = opt.get();
        // Quyền: ADMIN hoặc chính chủ sở hữu hóa đơn
        User auth = (User) session.getAttribute("authUser");
        if (auth == null) return "redirect:/login";
        boolean isAdmin = auth.getRole() == User.Role.ADMIN;
        boolean isOwner = khachThueService.findByUser(auth)
                .map(k -> bill.getHopDong() != null && bill.getHopDong().getKhach() != null && bill.getHopDong().getKhach().getId().equals(k.getId()))
                .orElse(false);
        if (!(isAdmin || isOwner)) return "redirect:/hoadon";

        String qrUrl = buildVietQrImageUrl(bill);
        model.addAttribute("item", bill);
        model.addAttribute("qrUrl", qrUrl);
        model.addAttribute("accountNo", ACCOUNT_NO);
        model.addAttribute("accountName", ACCOUNT_NAME);
        model.addAttribute("bankCode", BANK_CODE);
        return "hoadon/pay";
    }

    // Trả về thông tin QR ở dạng JSON để hiển thị popup trên list
    @GetMapping("/{id}/vietqr-info")
    @ResponseBody
    public java.util.Map<String, Object> vietqrInfo(@PathVariable("id") Integer id, HttpSession session) {
        java.util.Map<String, Object> res = new java.util.HashMap<>();
        Optional<HoaDon> opt = hoaDonService.findById(id);
        if (opt.isEmpty()) {
            res.put("ok", false);
            res.put("message", "Không tìm thấy hóa đơn");
            return res;
        }
        HoaDon bill = opt.get();
        User auth = (User) session.getAttribute("authUser");
        if (auth == null) {
            res.put("ok", false);
            res.put("message", "Chưa đăng nhập");
            return res;
        }
        boolean isAdmin = auth.getRole() == User.Role.ADMIN;
        boolean isOwner = khachThueService.findByUser(auth)
                .map(k -> bill.getHopDong() != null && bill.getHopDong().getKhach() != null && bill.getHopDong().getKhach().getId().equals(k.getId()))
                .orElse(false);
        if (!(isAdmin || isOwner)) {
            res.put("ok", false);
            res.put("message", "Không có quyền");
            return res;
        }
        String qrUrl = buildVietQrImageUrl(bill);
        res.put("ok", true);
        res.put("qrUrl", qrUrl);
        res.put("amount", bill.getTongTien());
        res.put("thangNam", bill.getThangNam());
        res.put("accountNo", ACCOUNT_NO);
        res.put("accountName", ACCOUNT_NAME);
        res.put("bankCode", BANK_CODE);
        return res;
    }

    // Người dùng bấm xác nhận đã chuyển khoản -> đánh dấu đã thanh toán
    @PostMapping("/{id}/confirm")
    public String confirmPaid(@PathVariable("id") Integer id, HttpSession session) {
        Optional<HoaDon> opt = hoaDonService.findById(id);
        if (opt.isEmpty()) return "redirect:/hoadon";
        HoaDon bill = opt.get();
        User auth = (User) session.getAttribute("authUser");
        if (auth == null) return "redirect:/login";
        boolean isAdmin = auth.getRole() == User.Role.ADMIN;
        boolean isOwner = khachThueService.findByUser(auth)
                .map(k -> bill.getHopDong() != null && bill.getHopDong().getKhach() != null && bill.getHopDong().getKhach().getId().equals(k.getId()))
                .orElse(false);
        if (!(isAdmin || isOwner)) return "redirect:/hoadon";
        bill.setTrangThai(HoaDon.TrangThai.DA_THANH_TOAN);
        bill.setXacNhanLuc(java.time.LocalDateTime.now());
        bill.setXacNhanBoi(auth.getUsername() + " (" + auth.getRole().name() + ")");
        hoaDonService.save(bill);
        return isAdmin ? "redirect:/hoadon" : "redirect:/hoadon/mine";
    }

    // ADMIN xác nhận đã nhận tiền
    @PostMapping("/{id}/admin-confirm")
    public String adminConfirm(@PathVariable("id") Integer id, HttpSession session) {
        Optional<HoaDon> opt = hoaDonService.findById(id);
        if (opt.isEmpty()) return "redirect:/hoadon";
        User auth = (User) session.getAttribute("authUser");
        if (auth == null || auth.getRole() != User.Role.ADMIN) return "redirect:/login";
        HoaDon bill = opt.get();
        bill.setTrangThai(HoaDon.TrangThai.DA_THANH_TOAN);
        bill.setXacNhanLuc(java.time.LocalDateTime.now());
        bill.setXacNhanBoi(auth.getUsername() + " (ADMIN)");
        hoaDonService.save(bill);
        return "redirect:/hoadon";
    }

    private void calcTotals(HoaDon item) {
        int dien = safeDiff(item.getChiSoDienMoi(), item.getChiSoDienCu());
        int nuoc = safeDiff(item.getChiSoNuocMoi(), item.getChiSoNuocCu());
        BigDecimal dgDien = item.getDonGiaDien() != null ? item.getDonGiaDien() : BigDecimal.ZERO;
        BigDecimal dgNuoc = item.getDonGiaNuoc() != null ? item.getDonGiaNuoc() : BigDecimal.ZERO;
        BigDecimal tienDien = dgDien.multiply(BigDecimal.valueOf(dien));
        BigDecimal tienNuoc = dgNuoc.multiply(BigDecimal.valueOf(nuoc));
        item.setTienDien(tienDien);
        item.setTienNuoc(tienNuoc);
        BigDecimal tienPhong = item.getTienPhong() != null ? item.getTienPhong() : BigDecimal.ZERO;
        BigDecimal phiDichVu = item.getPhiDichVu() != null ? item.getPhiDichVu() : BigDecimal.ZERO;
        item.setTongTien(tienPhong.add(tienDien).add(tienNuoc).add(phiDichVu));
    }

    private int safeDiff(Integer moi, Integer cu) {
        int m = moi != null ? moi : 0;
        int c = cu != null ? cu : 0;
        return Math.max(m - c, 0);
    }

    // Tạo URL ảnh VietQR (img.vietqr.io) dựa trên số tiền và nội dung chuyển khoản
    private String buildVietQrImageUrl(HoaDon bill) {
        // Tổng tiền làm tròn xuống VND (không phần lẻ)
        long amount = bill.getTongTien() != null ? bill.getTongTien().setScale(0, RoundingMode.DOWN).longValue() : 0L;
        String addInfo = "HD" + bill.getId() + "_" + (bill.getThangNam() != null ? bill.getThangNam() : "");
        String encodedInfo = URLEncoder.encode(addInfo, StandardCharsets.UTF_8);
        String encodedName = URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8);
        // Mẫu VietQR: https://img.vietqr.io/image/{bank}:{account}-compact.png?amount=...&addInfo=...&accountName=...
        return String.format("https://img.vietqr.io/image/%s:%s-compact.png?amount=%d&addInfo=%s&accountName=%s",
                BANK_CODE, ACCOUNT_NO, amount, encodedInfo, encodedName);
    }
}
