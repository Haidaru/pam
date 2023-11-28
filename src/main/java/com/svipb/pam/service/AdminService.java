package com.svipb.pam.service;

import com.svipb.pam.entity.Admin;
import com.svipb.pam.exception.ResourceNotFoundException;
import com.svipb.pam.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(@Autowired AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // Retrieve Admin Data
    public Page<Admin> retrieveAdminData(int page, int size) {
        try {
            System.out.println("Retrieving Admin Data");

            // Create a sort object with the "username" property and specify ascending order
            Sort sort = Sort.by(Sort.Order.asc("username"));

            // Use the findAll method with sorting and pagination
            return adminRepository.findAll(PageRequest.of(page, size, sort));
        } catch (Exception e) {
            // Log the exception or rethrow as a more specific exception if necessary
            throw new RuntimeException("Error retrieving admin data", e);
        }
    }

    // Add Admin Data
    public Admin addAdminData(Admin admin) {
        try {
            System.out.println("Adding Admin Data");
            // Save the admin data
            return adminRepository.save(admin);
        } catch (Exception e) {
            // Log the exception or rethrow as a more specific exception if necessary
            throw new RuntimeException("Error adding admin data", e);
        }
    }

    // Update Admin Data
    public String updateAdmin(Admin admin) {
        Optional<Admin> existingAdminOptional = adminRepository.findById(admin.getId());

        if (existingAdminOptional.isPresent()) {
            Admin existingAdmin = existingAdminOptional.get();
            adminRepository.save(admin);
            return "Berhasil mengupdate admin";
        } else {
            throw new ResourceNotFoundException("Admin dengan id: " + admin.getId() + " tidak ditemukan.");
        }
    }

    // Delete Admin Data

    public String deleteAdmin(int id) {
        try {
            Optional<Admin> adminOptional = adminRepository.findById(id);

            if (adminOptional.isPresent()) {
                adminRepository.deleteById(id);
                return "Berhasil menghapus admin";
            } else {
                throw new ResourceNotFoundException("Admin tidak ditemukan dengan id: " + id);
            }
        } catch (Exception e) {
            // Log the exception or rethrow as a more specific exception if necessary
            throw new ResourceNotFoundException("Admin dengan id: " + id + " tidak ditemukan.", e);
        }
    }

    // Get Admin Data by Username
    public Admin getAdminByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        return adminRepository.findByUsername(username);
    }
}
