package agh.oot.librarby.user.mapper;

import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.model.UserProfile;

public interface UserDtoMapper {
    boolean supports(UserProfile userProfile);

    UserResponse toDto(UserProfile userProfile);
}
