package agh.oot.librarby.user.mapper;

import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.model.Admin;
import agh.oot.librarby.user.model.UserAccount;
import agh.oot.librarby.user.model.UserProfile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AdminDtoMapper implements UserDtoMapper {
    @Override
    public boolean supports(UserProfile userProfile) {
        return userProfile instanceof Admin;
    }

    @Override
    public UserResponse toDto(UserProfile userProfile) {
        Admin profile = (Admin) userProfile;
        UserAccount account = profile.getUserAccount();
        return new UserResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getRole(),
                profile.getFirstName(),
                profile.getLastName(),
                Map.of()     // roleSpecificData
        );
    }
}
