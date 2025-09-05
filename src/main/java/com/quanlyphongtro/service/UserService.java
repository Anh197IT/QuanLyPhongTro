package com.quanlyphongtro.service;

import com.quanlyphongtro.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Integer id);
    Optional<User> findByUsername(String username);
    User save(User u, boolean encodePassword);
    void deleteById(Integer id);
    Optional<User> authenticate(String username, String rawPassword);
    /**
     * Change password for a user by verifying old raw password, then setting new raw password.
     * @param userId user id
     * @param oldRaw old raw password supplied by user
     * @param newRaw new raw password to set
     * @return updated user if success; empty if verification failed or user not found
     */
    Optional<User> changePassword(Integer userId, String oldRaw, String newRaw);
}
