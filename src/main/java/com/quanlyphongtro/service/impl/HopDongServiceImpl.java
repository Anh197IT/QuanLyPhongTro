package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.entity.HopDong;
import com.quanlyphongtro.entity.KhachThue;
import com.quanlyphongtro.repository.HopDongRepository;
import com.quanlyphongtro.service.HopDongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HopDongServiceImpl implements HopDongService {

    private final HopDongRepository repository;

    @Autowired
    public HopDongServiceImpl(HopDongRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HopDong> findAll() { return repository.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public Page<HopDong> findAll(Pageable pageable) { return repository.findAll(pageable); }

    @Override
    @Transactional(readOnly = true)
    public Optional<HopDong> findById(Integer id) { return repository.findById(id); }

    @Override
    public HopDong save(HopDong hd) { return repository.save(hd); }

    @Override
    public void deleteById(Integer id) { repository.deleteById(id); }

    @Override
    @Transactional(readOnly = true)
    public List<HopDong> findByKhach(KhachThue khach) {
        return repository.findByKhach(khach);
    }
}
