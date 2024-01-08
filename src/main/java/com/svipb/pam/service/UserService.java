package com.svipb.pam.service;

import com.svipb.pam.entity.User;
import com.svipb.pam.exception.ResourceNotFoundException;
import com.svipb.pam.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    // Inject the UserRepository
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Retrieve all Users
    public ResponseEntity<Object> getAllUsersWithPagination(int page, int size) {
        try {
            Page<User> usersPage = retrieveAllUsersWithPagination(page, size);
            if (usersPage.isEmpty()) {
                return new ResponseEntity<>("Tidak ditemukan user.", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(usersPage, HttpStatus.OK);
            }
        } catch (ResourceNotFoundException e) {
            // Handle the specific exception and return a custom response
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle other exceptions and return an appropriate response
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Handle the response for getAllUsersWithPagination
    private Page<User> retrieveAllUsersWithPagination(int page, int size) throws ResourceNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    // Add User
    @Transactional
    public ResponseEntity<String> addUser(User user) {
        try {
            checkDuplicateKeysForAdd(user);
            userRepository.save(user);
            return ResponseEntity.ok("Berhasil menambahkan user");
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolationException(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add user: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to add user: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add user: " + e.getMessage());
        }
    }


    // Update User
    @Transactional
    public String updateUser(User user) {
        try {
            // Check for duplicate entries
            checkDuplicateKeysForUpdate(user);

            // Retrieve the existing user from the database
            Optional<User> existingUserOptional = userRepository.findById(user.getId());

            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();

                // Update only the email field
                existingUser.setEmail(user.getEmail());

                // Save the updated user
                userRepository.save(existingUser);

                return "Berhasil mengupdate user";
            } else {
                throw new ResourceNotFoundException("User not found with id: " + user.getId());
            }
        } catch (DuplicateKeyException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user. " + e.getMessage(), e);
        }
    }

    // Delete User
    public String deleteUser(int id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);

            if (userOptional.isPresent()) {
                userRepository.deleteById(id);
                return "Berhasil menghapus user";
            } else {
                throw new ResourceNotFoundException("User tidak ditemukan dengan id: " + id);
            }
        } catch (Exception e) {
            // Log the exception or rethrow as a more specific exception if necessary
            throw new ResourceNotFoundException("User dengan id: " + id + " tidak ditemukan.", e);
        }
    }

    // Search User by Part of Name
    public List<User> searchUserByName(String name) {

        System.out.println("Received search request with name: " + name);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnorePaths("id", "email", "status", "gender", "phone", "faceid", "fingerid", "rfid");


        User user = new User();
        user.setName(name);

        Example<User> example = Example.of(user, matcher);

        return userRepository.findAll(example);
    }

    // Filter User by Status
    public Page<User> filterStatus(String status, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnorePaths("id", "name", "email", "gender", "phone", "faceid", "fingerid", "rfid");


        User user = new User();
        user.setStatus(status);

        Example<User> example = Example.of(user, matcher);

        return userRepository.findAll(example, pageable);
    }

    public Page<User> sortUsersByName(int page, int size, Sort.Order order) {
        try {
            System.out.println("Sorting Users by Name in " + (order.isAscending() ? "Ascending" : "Descending") + " Order");

            Sort sort = Sort.by(order);
            return userRepository.findAll(PageRequest.of(page, size, sort));
        } catch (Exception e) {
            throw new RuntimeException("Error sorting users: " + e.getMessage(), e);
        }
    }

    // Handle DataIntegrityViolationException
    private void handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        if (e.getMessage().contains("user_email_key")) {
            throw new DuplicateKeyException("Email already exists");
        } else if (e.getMessage().contains("user_rfid_key")) {
            throw new DuplicateKeyException("RFID already exists");
        } else if (e.getMessage().contains("user_faceid_key")) {
            throw new DuplicateKeyException("FaceID already exists");
        } else if (e.getMessage().contains("user_fingerid_key")) {
            throw new DuplicateKeyException("FingerID already exists");
        } else {
            logger.error("Failed to add user: " + e.getMessage(), e);
            throw new RuntimeException("Failed to add user: " + e.getMessage());
        }
    }

    // Check Duplicate Keys for Adding User
    public void checkDuplicateKeysForAdd(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUserWithEmail -> {
                    throw new DuplicateKeyException("Email already exists");
                });

        userRepository.findByRfid(user.getRfid())
                .ifPresent(existingUserWithRfid -> {
                    throw new DuplicateKeyException("RFID " + user.getRfid() + " already exists");
                });

        userRepository.findByFaceid(user.getFaceid())
                .ifPresent(existingUserWithFaceid -> {
                    throw new DuplicateKeyException("FaceID " + user.getFaceid() + " already exists");
                });

        userRepository.findByFingerid(user.getFingerid())
                .ifPresent(existingUserWithFingerid -> {
                    throw new DuplicateKeyException("FingerID " + user.getFingerid() + " already exists");
                });
    }


    // Check Duplicate Keys for Updating User
    private void checkDuplicateKeysForUpdate(User user) {
        userRepository.findByEmailAndIdNot(user.getEmail(), user.getId())
                .ifPresent(existingUserWithEmail -> {
                    throw new DuplicateKeyException("Email already exists");
                });

        userRepository.findByRfidAndIdNot(user.getRfid(), user.getId())
                .ifPresent(existingUserWithRfid -> {
                    throw new DuplicateKeyException("RFID " + user.getRfid() + " already exists");
                });

        userRepository.findByFaceidAndIdNot(user.getFaceid(), user.getId())
                .ifPresent(existingUserWithFaceid -> {
                    throw new DuplicateKeyException("FaceID " + user.getFaceid() + " already exists");
                });

        userRepository.findByFingeridAndIdNot(user.getFingerid(), user.getId())
                .ifPresent(existingUserWithFingerid -> {
                    throw new DuplicateKeyException("FingerID " + user.getFingerid() + " already exists");
                });
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}