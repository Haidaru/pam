package com.svipb.pam.controller;

import com.svipb.pam.entity.User;
import com.svipb.pam.exception.ResourceNotFoundException;
import com.svipb.pam.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private Process logger;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    // Retrieve all Users
    @GetMapping("/all")
    public ResponseEntity<Object> getAllUsersWithPagination(
            @RequestHeader(name = "page", defaultValue = "0") int page,
            @RequestHeader(name = "size", defaultValue = "10") int size) {

        return handleGetAllUsersResponse(() -> userService.getAllUsersWithPagination(page, size));
    }

    // Handle the response for getAllUsersWithPagination
    private ResponseEntity<Object> handleGetAllUsersResponse(Supplier<Page<User>> action) {
        try {
            Page<User> usersPage = action.get();
            if (usersPage.isEmpty()) {
                return new ResponseEntity<>("Tidak ditemukan user.", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(usersPage, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Add User
    @PostMapping("/add")
    public ResponseEntity<String> addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            String validationErrorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>("Validation errors: " + validationErrorMessage, HttpStatus.BAD_REQUEST);
        } else {
            try {
                String result = userService.addUser(user);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } catch (Exception e) {
                // Handle other exceptions and return an appropriate response
                return new ResponseEntity<>("Failed to add user: " + "there's already a user with the same email", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }




    // Update User
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@Valid @RequestBody User user, BindingResult bindingResult) throws ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            String validationErrorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            return new ResponseEntity<>("Validation errors: " + validationErrorMessage, HttpStatus.BAD_REQUEST);
        }

        try {
            String result = userService.updateUser(user);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Failed to update user: " + "There's no such data exist.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // Delete User
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        try {
            String result = userService.deleteUser(id);

            if (result.equals("Berhasil menghapus user")) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Kesalahan ketika menghapus user. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Search User by Part of Name
    @GetMapping("/search")
    public ResponseEntity<Object> searchUserByName(@RequestHeader("name") String name) {
        return handleSearchUserResponse(() -> userService.searchUserByName(name));
    }

    // Handle the response for searchUserByName
    private ResponseEntity<Object> handleSearchUserResponse(Supplier<List<User>> action) {
        try {
            List<User> users = action.get();
            if (users.isEmpty()) {
                return new ResponseEntity<>("Tidak ditemukan User dengan nama tersebut.", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(users, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Filter User by Status
    @GetMapping("/status")
    public ResponseEntity<Object> filterStatus(
            @RequestHeader("status") String status,
            @RequestHeader(name = "page", defaultValue = "0") int page,
            @RequestHeader(name = "size", defaultValue = "10") int size) {

        return handleFilterUsersByStatusResponse(() -> userService.filterStatus(status, PageRequest.of(page, size)), status);
    }

    // Handle the response for filterUsersByStatus
    private ResponseEntity<Object> handleFilterUsersByStatusResponse(Supplier<Page<User>> action, String status) {
        try {
            Page<User> usersPage = action.get();
            if (usersPage.isEmpty()) {
                System.out.println("No users found with status: " + status);
                return new ResponseEntity<>("No users found with status: " + status, HttpStatus.NOT_FOUND);
            } else {
                return ResponseEntity.ok(usersPage);
            }
        } catch (Exception e) {
            System.out.println("Error filtering users by status: " + e.getMessage());
            return new ResponseEntity<>("Error filtering users by status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Sort Users by Name in Ascending Order
    @GetMapping("/sortByNameAscending")
    public ResponseEntity<Object> sortUsersByNameAscending(
            @RequestHeader(name = "page", defaultValue = "0") int page,
            @RequestHeader(name = "size", defaultValue = "10") int size) {

        return handleSortUsersByNameAscendingResponse(() -> userService.sortUsersByNameAscending(page, size));
    }

    // Handle the response for sortUsersByNameAscending
    private ResponseEntity<Object> handleSortUsersByNameAscendingResponse(Supplier<Page<User>> action) {
        try {
            Page<User> usersPage = action.get();
            if (usersPage.isEmpty()) {
                return new ResponseEntity<>("No users found for sorting by name in ascending order", HttpStatus.NOT_FOUND);
            } else {
                return ResponseEntity.ok(usersPage);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error sorting users by name in ascending order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Sort Users by Name in Descending Order
    @GetMapping("/sortByNameDescending")
    public ResponseEntity<Object> sortUsersByNameDescending(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return handleSortUsersByNameDescendingResponse(() -> userService.sortUsersByNameDescending(page, size));
    }

    // Handle the response for sortUsersByNameDescending
    private ResponseEntity<Object> handleSortUsersByNameDescendingResponse(Supplier<Page<User>> action) {
        try {
            Page<User> usersPage = action.get();
            if (usersPage.isEmpty()) {
                return new ResponseEntity<>("No users found for sorting by name in descending order", HttpStatus.NOT_FOUND);
            } else {
                return ResponseEntity.ok(usersPage);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error sorting users by name in descending order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}