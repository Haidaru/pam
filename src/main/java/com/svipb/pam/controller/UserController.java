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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    // Retrieve all Users
    @GetMapping("/all")
    public ResponseEntity<Object> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        // Call the service method
        return userService.getAllUsersWithPagination(page, size);
    }

    // Handle the response for getAllUsersWithPagination
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>("Resource not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Add User
    @PostMapping("/add")
    public ResponseEntity<String> addUser(@Validated @RequestBody User user) {
        try {
            User existingUser = userService.getUserByEmail(user.getEmail());

            UserStatus userStatus = checkExistingUser(existingUser);
            return switch (userStatus) {
                case ALREADY_EXISTS -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User with email " + user.getEmail() + " already exists");
                case DOES_NOT_EXIST -> {
                    ResponseEntity<String> validationResult = validateUser(user);
                    yield validationResult.getStatusCode() == HttpStatus.OK ?
                            userService.addUser(user) :
                            validationResult;
                }
            };
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request: " + e.getMessage());
        }
    }

    private ResponseEntity<String> validateUser(User user) {
        if (StringUtils.isEmpty(user.getName()) || StringUtils.isEmpty(user.getEmail()) || StringUtils.isEmpty(user.getStatus())) {
            return ResponseEntity.badRequest().body("Validation errors: Name, email, and status cannot be empty");
        }

        // Additional validation logic if needed

        return ResponseEntity.ok("Validation successful");
    }

    private UserStatus checkExistingUser(User existingUser) {
        return existingUser != null ? UserStatus.ALREADY_EXISTS : UserStatus.DOES_NOT_EXIST;
    }

    private enum UserStatus {
        ALREADY_EXISTS,
        DOES_NOT_EXIST
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
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    @GetMapping("/sortByNameAscending")
    public ResponseEntity<Object> sortUsersByNameAscending(
            @RequestHeader(name = "page", defaultValue = "0") int page,
            @RequestHeader(name = "size", defaultValue = "10") int size) {

        return handleSortUsersResponse(() -> userService.sortUsersByName(page, size, Sort.Order.asc("name")));
    }

    @GetMapping("/sortByNameDescending")
    public ResponseEntity<Object> sortUsersByNameDescending(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return handleSortUsersResponse(() -> userService.sortUsersByName(page, size, Sort.Order.desc("name")));
    }

    private ResponseEntity<Object> handleSortUsersResponse(Supplier<Page<User>> action) {
        try {
            Page<User> usersPage = action.get();
            if (usersPage.isEmpty()) {
                return new ResponseEntity<>("No users found for sorting", HttpStatus.NOT_FOUND);
            } else {
                return ResponseEntity.ok(usersPage);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error sorting users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}