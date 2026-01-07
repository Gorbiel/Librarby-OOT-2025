package agh.oot.librarby.user.service;

import agh.oot.librarby.user.dto.MultipleUsersResponse;
import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.dto.UserUpdateRequest;
import agh.oot.librarby.user.mapper.UserResponseMapper;
import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.user.model.UserAccount;
import agh.oot.librarby.user.model.UserRole;
import agh.oot.librarby.user.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserAccountRepository userAccountRepository;

    private final UserResponseMapper userResponseMapper;

    public UserService(UserAccountRepository userAccountRepository, UserResponseMapper userResponseMapper) {
        this.userAccountRepository = userAccountRepository;
        this.userResponseMapper = userResponseMapper;
    }

    public UserResponse getUserAccount(Long userAccountId) {
        UserAccount user = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return userResponseMapper.toDto(user);
    }

    @Transactional
    public UserResponse updateUserAccount(Long userAccountId, UserUpdateRequest request) {
        UserAccount user = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (userAccountRepository.existsByUsername(request.username()) && !Objects.equals(request.username(), user.getUsername())) {
            throw new IllegalArgumentException("Username already used.");
        }

        if (userAccountRepository.existsByEmail(request.email()) && !Objects.equals(request.email(), user.getEmail())) {
            throw new IllegalArgumentException("Email already used.");
        }

        if (request.dateOfBirth() != null && user.getRole() != UserRole.READER) {
            throw new IllegalArgumentException("Date of birth cannot be changed if user is not the reader");
        }

        Optional.ofNullable(request.username())
                .ifPresent(user::setUsername);

        Optional.ofNullable(request.email())
                .ifPresent(user::setEmail);

        Optional.ofNullable(user.getUserProfile()).ifPresent(profile -> {

            Optional.ofNullable(request.firstName())
                    .ifPresent(profile::setFirstName);

            Optional.ofNullable(request.lastName())
                    .ifPresent(profile::setLastName);

            if (profile instanceof Reader reader) {
                Optional.ofNullable(request.dateOfBirth())
                        .ifPresent(reader::setDateOfBirth);
            }
        });

        UserAccount savedUser = userAccountRepository.save(user);

        return userResponseMapper.toDto(savedUser);
    }

    @Transactional
    public void deleteUserAccount(Long userAccountId) {
        UserAccount user = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userAccountRepository.delete(user);
    }

    public MultipleUsersResponse getAllUserAccounts() {
        var users = userAccountRepository.findAll();

        List<UserResponse> userResponses = users.stream()
                .map(userResponseMapper::toDto)
                .toList();

        return new MultipleUsersResponse(userResponses);
    }

}
