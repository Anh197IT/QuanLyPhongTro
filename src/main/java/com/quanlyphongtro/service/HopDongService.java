package com.quanlyphongtro.service;

import com.quanlyphongtro.entity.HopDong;
import com.quanlyphongtro.entity.KhachThue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HopDongService {
    List<HopDong> findAll();
    Page<HopDong> findAll(Pageable pageable);
    Optional<HopDong> findById(Integer id);
    HopDong save(HopDong hd);
    void deleteById(Integer id);
    List<HopDong> findByKhach(KhachThue khach);
}
