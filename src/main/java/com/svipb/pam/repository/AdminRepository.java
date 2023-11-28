package com.svipb.pam.repository;

import com.svipb.pam.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findById(int id);


    Admin findByUsername(String username);
}
