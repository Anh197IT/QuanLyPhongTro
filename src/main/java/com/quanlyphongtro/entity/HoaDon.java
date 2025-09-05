package com.quanlyphongtro.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hopDongId", nullable = false)
    private HopDong hopDong;

    // YYYY-MM, ví dụ 2025-08
    @Column(name = "thangNam", nullable = false, length = 7)
    private String thangNam;

    @Column(name = "chiSoDienCu")
    private Integer chiSoDienCu;

    @Column(name = "chiSoDienMoi")
    private Integer chiSoDienMoi;

    @Column(name = "chiSoNuocCu")
    private Integer chiSoNuocCu;

    @Column(name = "chiSoNuocMoi")
    private Integer chiSoNuocMoi;

    @Column(name = "donGiaDien", precision = 15, scale = 2)
    private BigDecimal donGiaDien;

    @Column(name = "donGiaNuoc", precision = 15, scale = 2)
    private BigDecimal donGiaNuoc;

    @Column(name = "tienPhong", precision = 15, scale = 2, nullable = false)
    private BigDecimal tienPhong;

    @Column(name = "tienDien", precision = 15, scale = 2)
    private BigDecimal tienDien;

    @Column(name = "tienNuoc", precision = 15, scale = 2)
    private BigDecimal tienNuoc;

    @Column(name = "phiDichVu", precision = 15, scale = 2)
    private BigDecimal phiDichVu = BigDecimal.ZERO;

    @Column(name = "tongTien", precision = 15, scale = 2)
    private BigDecimal tongTien;

    public enum TrangThai { CHUA_THANH_TOAN, DA_THANH_TOAN }

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 20)
    private TrangThai trangThai = TrangThai.CHUA_THANH_TOAN;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    // Log xác nhận thanh toán
    @Column(name = "xacNhanLuc")
    private LocalDateTime xacNhanLuc;

    @Column(name = "xacNhanBoi", length = 100)
    private String xacNhanBoi;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public HopDong getHopDong() { return hopDong; }
    public void setHopDong(HopDong hopDong) { this.hopDong = hopDong; }

    public String getThangNam() { return thangNam; }
    public void setThangNam(String thangNam) { this.thangNam = thangNam; }

    public Integer getChiSoDienCu() { return chiSoDienCu; }
    public void setChiSoDienCu(Integer chiSoDienCu) { this.chiSoDienCu = chiSoDienCu; }

    public Integer getChiSoDienMoi() { return chiSoDienMoi; }
    public void setChiSoDienMoi(Integer chiSoDienMoi) { this.chiSoDienMoi = chiSoDienMoi; }

    public Integer getChiSoNuocCu() { return chiSoNuocCu; }
    public void setChiSoNuocCu(Integer chiSoNuocCu) { this.chiSoNuocCu = chiSoNuocCu; }

    public Integer getChiSoNuocMoi() { return chiSoNuocMoi; }
    public void setChiSoNuocMoi(Integer chiSoNuocMoi) { this.chiSoNuocMoi = chiSoNuocMoi; }

    public BigDecimal getDonGiaDien() { return donGiaDien; }
    public void setDonGiaDien(BigDecimal donGiaDien) { this.donGiaDien = donGiaDien; }

    public BigDecimal getDonGiaNuoc() { return donGiaNuoc; }
    public void setDonGiaNuoc(BigDecimal donGiaNuoc) { this.donGiaNuoc = donGiaNuoc; }

    public BigDecimal getTienPhong() { return tienPhong; }
    public void setTienPhong(BigDecimal tienPhong) { this.tienPhong = tienPhong; }

    public BigDecimal getTienDien() { return tienDien; }
    public void setTienDien(BigDecimal tienDien) { this.tienDien = tienDien; }

    public BigDecimal getTienNuoc() { return tienNuoc; }
    public void setTienNuoc(BigDecimal tienNuoc) { this.tienNuoc = tienNuoc; }

    public BigDecimal getPhiDichVu() { return phiDichVu; }
    public void setPhiDichVu(BigDecimal phiDichVu) { this.phiDichVu = phiDichVu; }

    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }

    public TrangThai getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThai trangThai) { this.trangThai = trangThai; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public LocalDateTime getXacNhanLuc() { return xacNhanLuc; }
    public void setXacNhanLuc(LocalDateTime xacNhanLuc) { this.xacNhanLuc = xacNhanLuc; }

    public String getXacNhanBoi() { return xacNhanBoi; }
    public void setXacNhanBoi(String xacNhanBoi) { this.xacNhanBoi = xacNhanBoi; }
}
