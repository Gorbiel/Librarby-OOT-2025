package agh.oot.librarby.user.mapper;

import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.model.UserAccount;
import agh.oot.librarby.user.model.UserProfile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserResponseMapper {
    private final List<UserDtoMapper> mappers;

    public UserResponseMapper(List<UserDtoMapper> mappers) {
        this.mappers = mappers;
    }

    public UserResponse toDto(UserAccount userAccount) {
        UserProfile userProfile = userAccount.getUserProfile();
        return toDto(userProfile);
    }

    public UserResponse toDto(UserProfile userProfile) {
        return mappers.stream()
                .filter(m -> m.supports(userProfile))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No mapper for " + userProfile.getClass()))
                .toDto(userProfile);
    }
}
