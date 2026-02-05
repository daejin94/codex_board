package com.example.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGrantRepository extends JpaRepository<UserGrant, Long> {
}
