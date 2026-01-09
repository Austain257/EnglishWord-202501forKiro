package com.austain.srevice;

import com.austain.domain.dto.profile.ProfileDashboardDTO;
import com.austain.domain.dto.profile.ProfileDashboardDTO.Settings;
import com.austain.domain.dto.profile.ProfileDashboardDTO.PasswordUpdate;

public interface ProfileService {
    ProfileDashboardDTO getProfile(Long userId);

    Settings updateSettings(Long userId, Settings settings);

    void updatePassword(Long userId, PasswordUpdate passwordUpdate);
}
