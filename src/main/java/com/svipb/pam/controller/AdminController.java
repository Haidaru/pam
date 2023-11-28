package com.svipb.pam.controller;

import com.svipb.pam.entity.Admin;
import com.svipb.pam.exception.ResourceNotFoundException;
import com.svipb.pam.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(@Autowired AdminService adminService) {
        this.adminService = adminService;
    }

    // Retrieve Admin Data
    @GetMapping("/data")
    public ResponseEntity<Object> retrieveAdminData(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return handleRetrieveAdminDataResponse(() -> adminService.retrieveAdminData(page, size));
    }

    private ResponseEntity<Object> handleRetrieveAdminDataResponse(Supplier<Page<Admin>> action) {
        try {
            Page<Admin> adminsPage = action.get();
            if (adminsPage.isEmpty()) {
                return new ResponseEntity<>("No admin data found", HttpStatus.NOT_FOUND);
            } else {
                return ResponseEntity.ok(adminsPage);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving admin data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Add Admin Data
    @PostMapping("/add")
    public ResponseEntity<Object> addAdminData(@Valid @RequestBody Admin admin, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            String validationErrorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            return new ResponseEntity<>("Validation errors: " + validationErrorMessage, HttpStatus.BAD_REQUEST);
        }

        return handleAddAdminDataResponse(() -> adminService.addAdminData(admin));
    }

    private ResponseEntity<Object> handleAddAdminDataResponse(Supplier<Admin> action) {
        try {
            Admin addedAdmin = action.get();
            return new ResponseEntity<>("Admin data added successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding admin data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update Admin Data
    @PutMapping("/update")
    public ResponseEntity<String> updateAdmin(@Valid @RequestBody Admin admin, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            String validationErrorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            return new ResponseEntity<>("Validation errors: " + validationErrorMessage, HttpStatus.BAD_REQUEST);
        }

        try {
            String result = adminService.updateAdmin(admin);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            // Handle the exception and return an appropriate response
            return new ResponseEntity<>("Admin not found. " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle other exceptions and return an appropriate response
            return new ResponseEntity<>("Failed to update admin: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete Admin Data
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable int id) {
        try {
            String result = adminService.deleteAdmin(id);

            if (result.equals("Berhasil menghapus Admin")) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Kesalahan ketika menghapus admin. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}