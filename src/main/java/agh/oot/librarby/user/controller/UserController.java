package agh.oot.librarby.user.controller;

import agh.oot.librarby.user.dto.MultipleUsersResponse;
import agh.oot.librarby.user.dto.UserUpdateRequest;
import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<MultipleUsersResponse> getAllUsers() {
        MultipleUsersResponse body = userService.getAllUserAccounts();
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long userAccountId) {
        UserResponse body = userService.getUserAccount(userAccountId);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<Void> updateUserById(@PathVariable("userId") Long userAccountId, @RequestBody @Valid UserUpdateRequest request) {
        userService.updateUserAccount(userAccountId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("userId") Long userAccountId) {
        userService.deleteUserAccount(userAccountId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
