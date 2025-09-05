package com.quanlyphongtro.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Phong")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "soPhong", nullable = false, length = 10)
    @NotBlank(message = "Số phòng không được để trống")
    private String soPhong;

    @Column(name = "dienTich", nullable = false, precision = 5, scale = 2)
    @NotNull(message = "Diện tích bắt buộc")
    @DecimalMin(value = "0.0", inclusive = false, message = "Diện tích phải > 0")
    private BigDecimal dienTich;

    @Column(name = "giaPhong", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Giá phòng bắt buộc")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải > 0")
    private BigDecimal giaPhong;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 20)
    private Status trangThai = Status.TRONG;

    @Column(name = "ghiChu", columnDefinition = "TEXT")
    private String ghiChu;

    public enum Status { TRONG, DANG_THUE }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSoPhong() { return soPhong; }
    public void setSoPhong(String soPhong) { this.soPhong = soPhong; }

    public BigDecimal getDienTich() { return dienTich; }
    public void setDienTich(BigDecimal dienTich) { this.dienTich = dienTich; }

    public BigDecimal getGiaPhong() { return giaPhong; }
    public void setGiaPhong(BigDecimal giaPhong) { this.giaPhong = giaPhong; }

    public Status getTrangThai() { return trangThai; }
    public void setTrangThai(Status trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
