package agh.oot.librarby.user.model.service;

import agh.oot.librarby.user.auth.repository.UserAccountRepository;
import agh.oot.librarby.user.model.UserAccount;
import agh.oot.librarby.user.model.dto.UserDto;
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
}
