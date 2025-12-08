package agh.oot.librarby.user.model.controller;

import agh.oot.librarby.user.model.UserAccount;
import agh.oot.librarby.user.model.dto.UserDto;
import agh.oot.librarby.user.model.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
