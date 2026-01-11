package agh.oot.librarby.user.mapper;

import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.user.model.UserAccount;
import agh.oot.librarby.user.model.UserProfile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReaderDtoMapper implements UserDtoMapper {
    @Override
    public boolean supports(UserProfile userProfile) {
        return userProfile instanceof Reader;
    }

    @Override
    public UserResponse toDto(UserProfile userProfile) {
        Reader profile = (Reader) userProfile;
        UserAccount account = profile.getUserAccount();
        return new UserResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getRole(),
                profile.getFirstName(),
                profile.getLastName(),
                Map.of(
                        "dateOfBirth", profile.getDateOfBirth(),
                        "rentalLimit", profile.getRentalLimit()
                )
        );
    }
}