package com.quanlyphongtro.service;

import com.quanlyphongtro.entity.HoaDon;
import com.quanlyphongtro.entity.HopDong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HoaDonService {
    List<HoaDon> findAll();
    Page<HoaDon> findAll(Pageable pageable);
    Optional<HoaDon> findById(Integer id);
    HoaDon save(HoaDon hd);
    void deleteById(Integer id);
    boolean existsByHopDongAndThangNam(HopDong hopDong, String thangNam);
    Optional<HoaDon> findLatestByHopDong(HopDong hopDong);
    List<HoaDon> findByHopDong(HopDong hopDong);
}
