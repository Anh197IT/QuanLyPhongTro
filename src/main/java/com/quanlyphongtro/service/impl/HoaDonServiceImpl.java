package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.entity.HoaDon;
import com.quanlyphongtro.entity.HopDong;
import com.quanlyphongtro.repository.HoaDonRepository;
import com.quanlyphongtro.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository repository;

    @Autowired
    public HoaDonServiceImpl(HoaDonRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDon> findAll() { return repository.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public Page<HoaDon> findAll(Pageable pageable) { return repository.findAll(pageable); }

    @Override
    @Transactional(readOnly = true)
    public Optional<HoaDon> findById(Integer id) { return repository.findById(id); }

    @Override
    public HoaDon save(HoaDon hd) { return repository.save(hd); }

    @Override
    public void deleteById(Integer id) { repository.deleteById(id); }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByHopDongAndThangNam(HopDong hopDong, String thangNam) {
        return repository.existsByHopDongAndThangNam(hopDong, thangNam);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HoaDon> findLatestByHopDong(HopDong hopDong) {
        return repository.findTopByHopDongOrderByThangNamDesc(hopDong);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDon> findByHopDong(HopDong hopDong) {
        return repository.findByHopDong(hopDong);
    }
}
