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

    default Optional<User> findByEmailAndIdNot(String email, int id) {
        // Implement the method using JpaRepository's findById
        return findById(id).filter(user -> user.getEmail().equals(email));
    }

    default Optional<User> findByRfidAndIdNot(int rfid, int id) {
        // Implement the method using JpaRepository's findById
        return findById(id).filter(user -> user.getRfid() == rfid);
    }

    default Optional<User> findByFaceidAndIdNot(int faceid, int id) {
        // Implement the method using JpaRepository's findById
        return findById(id).filter(user -> user.getFaceid() == faceid);
    }

    default Optional<User> findByFingeridAndIdNot(int fingerid, int id) {
        // Implement the method using JpaRepository's findById
        return findById(id).filter(user -> user.getFingerid() == fingerid);
    }
}
