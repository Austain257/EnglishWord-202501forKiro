package com.austain.service;

import com.austain.domain.dto.profile.ProfileDashboardDTO;
import com.austain.domain.dto.profile.ProfileDashboardDTO.Settings;
import com.austain.domain.dto.profile.ProfileDashboardDTO.PasswordUpdate;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    ProfileDashboardDTO getProfile(Long userId);

    Settings updateSettings(Long userId, Settings settings);

    void updatePassword(Long userId, PasswordUpdate passwordUpdate);

    /**
     * 上传头像到OSS并返回可访问URL
     */
    String uploadAvatar(Long userId, MultipartFile file);
}
