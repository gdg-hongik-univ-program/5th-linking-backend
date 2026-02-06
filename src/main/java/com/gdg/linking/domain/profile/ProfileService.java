package com.gdg.linking.domain.profile;

import com.gdg.linking.domain.profile.dto.ProfileResponse;

public interface ProfileService {

    ProfileResponse getUserProfile(Long userId);

    void addExperience(Long userId, int amount);

}
