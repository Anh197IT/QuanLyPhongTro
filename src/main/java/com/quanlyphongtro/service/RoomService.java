package com.quanlyphongtro.service;

import com.quanlyphongtro.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> findAll();
    Page<Room> findAll(Pageable pageable);
    Optional<Room> findById(Integer id);
    Room save(Room room);
    void deleteById(Integer id);
    boolean existsBySoPhong(String soPhong);
}
