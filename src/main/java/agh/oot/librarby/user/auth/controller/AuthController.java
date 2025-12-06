package agh.oot.librarby.user.auth.controller;

import agh.oot.librarby.user.auth.dto.LoginResponse;
import agh.oot.librarby.user.auth.dto.LoginRequest;
import agh.oot.librarby.user.auth.dto.RegisterReaderRequest;
import agh.oot.librarby.user.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    ResponseEntity<Void> registerNewReader(@RequestBody @Valid RegisterReaderRequest request) {
        authService.registerReader(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
