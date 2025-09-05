package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.entity.User;
import com.quanlyphongtro.repository.UserRepository;
import com.quanlyphongtro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    public void initAdmin() {
        // Migrate legacy role values at startup
        migrateLegacyRoles();
        if (repo.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(sha256("admin123"));
            admin.setRole(User.Role.ADMIN);
            admin.setFullName("Chủ trọ");
            admin.setEnabled(true);
            repo.save(admin);
        }
    }

    private void migrateLegacyRoles() {
        try {
            em.createNativeQuery("UPDATE users SET role='USER' WHERE role='STAFF'").executeUpdate();
        } catch (Exception ignored) {
            // ignore if table/column not present; best-effort migration
        }
    }

    @Override
    public List<User> findAll() {
        migrateLegacyRoles();
        return repo.findAll();
    }

    @Override
    public Optional<User> findById(Integer id) {
        migrateLegacyRoles();
        return repo.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        migrateLegacyRoles();
        return repo.findByUsername(username);
    }

    @Override
    public User save(User u, boolean encodePassword) {
        if (encodePassword && u.getPasswordHash() != null && !u.getPasswordHash().isBlank()) {
            u.setPasswordHash(sha256(u.getPasswordHash()));
        }
        return repo.save(u);
    }

    @Override
    public void deleteById(Integer id) { repo.deleteById(id); }

    @Override
    public Optional<User> authenticate(String username, String rawPassword) {
        return repo.findByUsername(username)
                .filter(u -> u.isEnabled() && u.getPasswordHash().equals(sha256(rawPassword)));
    }

    @Override
    public Optional<User> changePassword(Integer userId, String oldRaw, String newRaw) {
        if (userId == null || oldRaw == null || newRaw == null) return Optional.empty();
        return repo.findById(userId)
                .filter(User::isEnabled)
                .filter(u -> u.getPasswordHash() != null && u.getPasswordHash().equals(sha256(oldRaw)))
                .map(u -> {
                    u.setPasswordHash(sha256(newRaw));
                    // Clear first-login requirement after successful password change
                    u.setMustChangePassword(false);
                    return repo.save(u);
                });
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

