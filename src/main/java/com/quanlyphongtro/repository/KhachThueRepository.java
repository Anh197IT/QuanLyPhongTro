package com.quanlyphongtro.repository;

import com.quanlyphongtro.entity.KhachThue;
import com.quanlyphongtro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachThueRepository extends JpaRepository<KhachThue, Integer> {
    boolean existsByCccd(String cccd);
    Optional<KhachThue> findByUser(User user);
}
