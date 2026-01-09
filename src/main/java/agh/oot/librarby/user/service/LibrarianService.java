package agh.oot.librarby.user.service;

import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.mapper.UserResponseMapper;
import agh.oot.librarby.user.model.Librarian;
import agh.oot.librarby.user.model.UserAccount;
import agh.oot.librarby.user.model.UserProfile;
import agh.oot.librarby.user.model.UserRole;
import agh.oot.librarby.user.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LibrarianService {
    private final UserAccountRepository userAccountRepository;

    private final UserResponseMapper userResponseMapper;

    public LibrarianService(UserAccountRepository userAccountRepository, UserResponseMapper userResponseMapper) {
        this.userAccountRepository = userAccountRepository;
        this.userResponseMapper = userResponseMapper;
    }

    @Transactional
    public UserResponse promoteUserToLibrarian(String username) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfile oldProfile = account.getUserProfile();
        String firstName = oldProfile.getFirstName();
        String lastName = oldProfile.getLastName();

        account.setUserProfile(null);
        userAccountRepository.saveAndFlush(account);

        Librarian librarian = new Librarian(firstName, lastName);
        librarian.setUserAccount(account);

        account.setUserProfile(librarian);
        account.setRole(UserRole.LIBRARIAN);

        UserAccount savedAccount = userAccountRepository.save(account);

        return userResponseMapper.toDto(savedAccount);
    }
}
