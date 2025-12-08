package agh.oot.librarby.user.controller;

import agh.oot.librarby.user.dto.CreateUserDto;
import agh.oot.librarby.user.dto.UserDto;
import agh.oot.librarby.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/readers")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{userId}")
    public UserDto getUserById(@PathVariable("userId") Long userAccountId) {
        return userService.getUserAccount(userAccountId);
    }

    @PatchMapping(value = "/{userId}")
    public UserDto updateUserById(@PathVariable("userId") Long userAccountId, @RequestBody UserDto userDto) {
        return userService.updateUserAccount(userAccountId, userDto);
    }

    @DeleteMapping(value = "/{userId}")
    public void deleteUserById(@PathVariable("userId") Long userAccountId) {
        userService.deleteUserAccount(userAccountId);
    }

    @PostMapping
    public void createReader(@RequestBody CreateUserDto request) {
        userService.createUserAccount(request);
    }
}
