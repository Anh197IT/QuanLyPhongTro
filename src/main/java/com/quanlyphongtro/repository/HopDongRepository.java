package com.quanlyphongtro.repository;

import com.quanlyphongtro.entity.HopDong;
import com.quanlyphongtro.entity.KhachThue;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HopDongRepository extends JpaRepository<HopDong, Integer> {
    List<HopDong> findByKhach(KhachThue khach);

    @Query("select h from HopDong h where h.trangThai = com.quanlyphongtro.entity.HopDong$TrangThai.DANG_THUE and h.ngayKetThuc is not null and h.ngayKetThuc < :today")
    List<HopDong> findActiveContractsEndedBefore(@Param("today") LocalDate today);
}
