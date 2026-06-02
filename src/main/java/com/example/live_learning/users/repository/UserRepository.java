package com.example.live_learning.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.live_learning.users.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
