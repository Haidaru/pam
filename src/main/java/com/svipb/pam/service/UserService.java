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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
    public Page<User> getAllUsersWithPagination(int page, int size) {
        // Create a PageRequest with the given page, size, and sorting (if needed)
        PageRequest pageRequest = PageRequest.of(page, size);

        // Use the findAll method with pagination
        return userRepository.findAll(pageRequest);
    }

    // Add User
    @Transactional
    public String addUser(User user) {
        try {
            checkDuplicateKeysForAdd(user);
            userRepository.save(user);
            return "Berhasil menambahkan user";
        } catch (DuplicateKeyException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolationException(e);
            throw new RuntimeException("Failed to add user: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to add user: " + e.getMessage(), e);
            throw new RuntimeException("Failed to add user: " + e.getMessage());
        }
    }


    // Update User
    @Transactional
    public String updateUser(User user) {
        try {
            // Check for duplicate entries
            checkDuplicateKeysForUpdate(user);
            userRepository.save(user);
            return "Berhasil mengupdate user";
        } catch (DuplicateKeyException e) {
            throw e;
        } catch (Exception e) {
            Optional<User> existingUser = userRepository.findByEmailAndIdNot(user.getEmail(), user.getId());
            existingUser.ifPresent(existing -> {
                if (existing.getId() != user.getId()) {
                    throw new DuplicateKeyException("Email already exists");
                }
            });

            // Handle other exceptions and return an appropriate response
            throw new RuntimeException("Failed to update user: " + "there's already a user with the same email", e);
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

    // Sort Users by Name in Ascending Order
    public Page<User> sortUsersByNameAscending(int page, int size) {
        try {
            System.out.println("Sorting Users by Name in Ascending Order");

            // Create a sort object with the "name" property and specify ascending order
            Sort sort = Sort.by(Sort.Order.asc("name"));

            // Use the findAll method with sorting and pagination
            return userRepository.findAll(PageRequest.of(page, size, sort));
        } catch (Exception e) {
            // Log the exception or rethrow as a more specific exception if necessary
            throw new RuntimeException("Error sorting users by name in ascending order", e);
        }
    }

    // Sort Users by Name in Descending Order
    public Page<User> sortUsersByNameDescending(int page, int size) {
        try {
            System.out.println("Sorting Users by Name in Descending Order");

            // Create a sort object with the "name" property and specify descending order
            Sort sort = Sort.by(Sort.Order.desc("name"));

            // Use the findAll method with sorting and pagination
            return userRepository.findAll(PageRequest.of(page, size, sort));
        } catch (Exception e) {
            // Log the exception or rethrow as a more specific exception if necessary
            throw new RuntimeException("Error sorting users by name in descending order", e);
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

    public void checkDuplicateKeysForAdd(User user) {
        // Check for duplicate keys directly in the database during add
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


    private void checkDuplicateKeysForUpdate(User user) {
        // Check for duplicate keys directly in the database during update
        Objects.requireNonNull(UserRepository.findByEmailAndIdNot(user.getEmail(), user.getId()))
                .ifPresent(existingUserWithEmail -> {
                    throw new DuplicateKeyException("Email already exists");
                });

        Objects.requireNonNull(UserRepository.findByRfidAndIdNot(user.getRfid(), user.getId()))
                .ifPresent(existingUserWithRfid -> {
                    throw new DuplicateKeyException("RFID " + user.getRfid() + " already exists");
                });

        Objects.requireNonNull(UserRepository.findByFaceidAndIdNot(user.getFaceid(), user.getId()))
                .ifPresent(existingUserWithFaceid -> {
                    throw new DuplicateKeyException("FaceID " + user.getFaceid() + " already exists");
                });

        Objects.requireNonNull(UserRepository.findByFingeridAndIdNot(user.getFingerid(), user.getId()))
                .ifPresent(existingUserWithFingerid -> {
                    throw new DuplicateKeyException("FingerID " + user.getFingerid() + " already exists");
                });
        if (!userRepository.existsById(user.getId())) {
            throw new ResourceNotFoundException("There's no such data exist.");
        }
    }

    private void handleDuplicateKeyException(Exception e) {
        if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException constraintViolationException) {
            String constraintName = constraintViolationException.getConstraintName();
            if (constraintName != null && constraintName.contains("user_email_key")) {
                throw new DuplicateKeyException("Email already exists");
            } else if (constraintName != null && constraintName.contains("user_rfid_key")) {
                throw new DuplicateKeyException("RFID already exists");
            } else if (constraintName != null && constraintName.contains("user_faceid_key")) {
                throw new DuplicateKeyException("FaceID already exists");
            } else if (constraintName != null && constraintName.contains("user_fingerid_key")) {
                throw new DuplicateKeyException("FingerID already exists");
            }
        }
    }
}