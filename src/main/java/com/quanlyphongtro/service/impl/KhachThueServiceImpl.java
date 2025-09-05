package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.entity.KhachThue;
import com.quanlyphongtro.entity.User;
import com.quanlyphongtro.repository.KhachThueRepository;
import com.quanlyphongtro.service.KhachThueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KhachThueServiceImpl implements KhachThueService {

    private final KhachThueRepository repository;

    @Autowired
    public KhachThueServiceImpl(KhachThueRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhachThue> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KhachThue> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KhachThue> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KhachThue> findByUser(User user) {
        return repository.findByUser(user);
    }

    @Override
    public KhachThue save(KhachThue kt) {
        return repository.save(kt);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCccd(String cccd) {
        return repository.existsByCccd(cccd);
    }
}

