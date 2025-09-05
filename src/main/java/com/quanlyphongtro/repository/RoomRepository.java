package com.quanlyphongtro.repository;

import com.quanlyphongtro.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    boolean existsBySoPhong(String soPhong);
    List<Room> findByTrangThai(Room.Status trangThai);
}
