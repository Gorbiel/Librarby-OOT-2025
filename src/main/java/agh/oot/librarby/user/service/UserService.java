package agh.oot.librarby.user.service;

import agh.oot.librarby.user.repository.UserAccountRepository;
import agh.oot.librarby.user.model.UserAccount;
import agh.oot.librarby.user.dto.CreateUserDto;
import agh.oot.librarby.user.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserAccountRepository userAccountRepository;

    public UserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserDto getUserAccount(Long userAccountId) {
        UserAccount user = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public UserDto updateUserAccount(Long userAccountId, UserDto userDto) {
        UserAccount user = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setUsername(userDto.username());
        user.setEmail(userDto.email());

        UserAccount updatedUser = userAccountRepository.save(user);

        return new UserDto(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail()
        );
    }

    public void deleteUserAccount(Long userAccountId) {
        UserAccount user = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userAccountRepository.delete(user);
    }

    public void createUserAccount(CreateUserDto userDto) {
        UserAccount newUser = new UserAccount(userDto.username(), userDto.email(), userDto.password(), userDto.role());
        userAccountRepository.save(newUser);
    }
}
