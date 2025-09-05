package com.quanlyphongtro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "HopDong")
public class HopDong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phongId", nullable = false)
    private Room phong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "khachId", nullable = false)
    private KhachThue khach;

    @Column(name = "ngayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngayKetThuc")
    private LocalDate ngayKetThuc;

    @Column(name = "giaPhong", nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal giaPhong;

    @Column(name = "tienCoc", precision = 15, scale = 2)
    private BigDecimal tienCoc = BigDecimal.ZERO;

    public enum TrangThai { DANG_THUE, DA_KET_THUC }

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 20)
    private TrangThai trangThai = TrangThai.DANG_THUE;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Room getPhong() { return phong; }
    public void setPhong(Room phong) { this.phong = phong; }

    public KhachThue getKhach() { return khach; }
    public void setKhach(KhachThue khach) { this.khach = khach; }

    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public BigDecimal getGiaPhong() { return giaPhong; }
    public void setGiaPhong(BigDecimal giaPhong) { this.giaPhong = giaPhong; }

    public BigDecimal getTienCoc() { return tienCoc; }
    public void setTienCoc(BigDecimal tienCoc) { this.tienCoc = tienCoc; }

    public TrangThai getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThai trangThai) { this.trangThai = trangThai; }
}
