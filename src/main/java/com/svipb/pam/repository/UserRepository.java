package com.svipb.pam.repository;

import com.svipb.pam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByRfid(int rfid);

    Optional<User> findByFaceid(int faceid);

    Optional<User> findByFingerid(int fingerid);

    Optional<User> findByEmailAndIdNot(String email, int id);

    Optional<User> findByRfidAndIdNot(int rfid, int id);

    Optional<User> findByFaceidAndIdNot(int faceid, int id);

    Optional<User> findByFingeridAndIdNot(int fingerid, int id);
}
