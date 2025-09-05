package com.quanlyphongtro.repository;

import com.quanlyphongtro.entity.HoaDon;
import com.quanlyphongtro.entity.HopDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    boolean existsByHopDongAndThangNam(HopDong hopDong, String thangNam);
    Optional<HoaDon> findTopByHopDongOrderByThangNamDesc(HopDong hopDong);
    List<HoaDon> findByHopDong(HopDong hopDong);
}
