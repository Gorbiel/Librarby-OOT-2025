package agh.oot.librarby.user.controller;

import agh.oot.librarby.user.dto.MultipleUsersResponse;
import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.dto.UserUpdateRequest;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<MultipleUsersResponse> getAllUsers() {
        MultipleUsersResponse body = userService.getAllUserAccounts();
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN') or #userId == principal.id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserAccount(userId));
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN') or #userId == principal.id")
    public ResponseEntity<UserResponse> updateUserById(@PathVariable("userId") Long userId,
                                                       @RequestBody @Valid UserUpdateRequest request) {
        UserResponse userResponse = userService.updateUserAccount(userId, request);
        return ResponseEntity.ok(userResponse);
    }


    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable("userId") Long userId) {
        userService.deleteUserAccount(userId);
        return ResponseEntity.ok().build();
    }
}
