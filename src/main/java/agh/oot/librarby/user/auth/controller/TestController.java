package agh.oot.librarby.user.auth.controller;

import jakarta.persistence.GeneratedValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/controller/test")
class TestController {

    @GetMapping
    ResponseEntity<String> test() {
        return ResponseEntity.ok("Działa");
    }
}
