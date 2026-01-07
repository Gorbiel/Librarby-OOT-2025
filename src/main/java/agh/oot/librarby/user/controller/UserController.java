package agh.oot.librarby.user.controller;

import agh.oot.librarby.auth.model.CustomUserDetails;
import agh.oot.librarby.auth.service.CustomUserDetailsService;
import agh.oot.librarby.user.dto.MultipleUsersResponse;
import agh.oot.librarby.user.dto.UserUpdateRequest;
import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

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

//    @GetMapping(value = "/{userId}")
//    public ResponseEntity<UserResponse> getUserById(Authentication auth, @PathVariable("userId") Long userAccountId) {
//        if (auth == null || !auth.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        Set<String> roles = auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toSet());
//
//        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_LIBRARIAN")) {
//            UserResponse body = userService.getUserAccount(userAccountId);
//            return ResponseEntity.status(HttpStatus.OK).body(body);
//        }
//
//        if (roles.contains("ROLE_READER")) {
//            if (auth.getPrincipal() instanceof CustomUserDetails principal) {
//                if (principal.getId().equals(userAccountId)) {
//                    UserResponse body = userService.getUserAccount(userAccountId);
//                    return ResponseEntity.status(HttpStatus.OK).body(body);
//                }
//            }
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN') or #userId == principal.id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserAccount(userId));
    }

    //    @PatchMapping(value = "/{userId}")
//    public ResponseEntity<Void> updateUserById(Authentication auth,
//                                               @PathVariable("userId") Long userAccountId,
//                                               @RequestBody @Valid UserUpdateRequest request) {
//
//        if (auth == null || !auth.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        Set<String> roles = auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toSet());
//
//        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_LIBRARIAN")) {
//            userService.updateUserAccount(userAccountId, request);
//            return ResponseEntity.ok().build();
//        }
//
//        if (roles.contains("ROLE_READER")) {
//            if (auth.getPrincipal() instanceof CustomUserDetails principal) {
//                if (principal.getId().equals(userAccountId)) {
//                    userService.updateUserAccount(userAccountId, request);
//                    return ResponseEntity.ok().build();
//                }
//            }
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//    }
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
