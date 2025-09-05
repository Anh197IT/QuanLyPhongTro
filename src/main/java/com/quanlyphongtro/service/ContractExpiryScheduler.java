package com.quanlyphongtro.service;

import com.quanlyphongtro.entity.HopDong;
import com.quanlyphongtro.entity.KhachThue;
import com.quanlyphongtro.entity.Room;
import com.quanlyphongtro.entity.User;
import com.quanlyphongtro.repository.HopDongRepository;
import com.quanlyphongtro.repository.RoomRepository;
import com.quanlyphongtro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ContractExpiryScheduler {

    private final HopDongRepository hopDongRepo;
    private final RoomRepository roomRepo;
    private final UserRepository userRepo;

    @Autowired
    public ContractExpiryScheduler(HopDongRepository hopDongRepo, RoomRepository roomRepo, UserRepository userRepo) {
        this.hopDongRepo = hopDongRepo;
        this.roomRepo = roomRepo;
        this.userRepo = userRepo;
    }

    // Run daily at 02:00 local time
    @Transactional
    @Scheduled(cron = "0 0 2 * * *")
    public void expireContractsDaily() {
        LocalDate today = LocalDate.now();
        List<HopDong> expired = hopDongRepo.findActiveContractsEndedBefore(today);
        if (expired.isEmpty()) return;

        for (HopDong hd : expired) {
            // Update contract status
            hd.setTrangThai(HopDong.TrangThai.DA_KET_THUC);

            // Free up room
            Room room = hd.getPhong();
            if (room != null) {
                room.setTrangThai(Room.Status.TRONG);
                roomRepo.save(room);
            }

            // Disable linked user's account if any
            KhachThue khach = hd.getKhach();
            if (khach != null) {
                User u = khach.getUser();
                if (u != null && u.isEnabled()) {
                    u.setEnabled(false);
                    userRepo.save(u);
                }
            }
        }
        // Save all contracts after updates
        hopDongRepo.saveAll(expired);
    }
}
