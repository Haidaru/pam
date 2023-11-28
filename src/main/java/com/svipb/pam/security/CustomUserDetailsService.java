//package com.svipb.pam.security;
//
//import com.svipb.pam.entity.Admin;
//import com.svipb.pam.exception.ResourceNotFoundException;
//import com.svipb.pam.repository.AdminRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final AdminRepository adminRepository;
//
//    @Autowired
//    public CustomUserDetailsService(AdminRepository adminRepository) {
//        this.adminRepository = adminRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) {
//        Admin admin = adminRepository.findByUsername(username);
//        if (admin == null) {
//            throw new ResourceNotFoundException("Admin not found with username: " + username);
//        }
//        return new CustomUserDetails(admin);
//    }
//}
