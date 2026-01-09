package com.austain.domain.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDashboardDTO {
    private ProfileSummary summary;
    private StudyOverview overview;
    private ProgressTrends progress;
    private ChecklistSnapshot checklist;
    private DeviceSecurity device;
    private List<Achievement> achievements;
    private Settings settings;
    private List<QuickAction> quickActions;
    private List<Notification> notifications;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfileSummary {
        private Long userId;
        private String username;
        private String nickname;
        private String avatar;
        private String email;
        private LocalDate joinDate;
        private LocalDateTime lastLoginTime;
        private String learningGoal;
        private Integer dailyTargetMinutes;
        private Boolean reminderEnabled;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudyOverview {
        private long totalStudySeconds;
        private int todayStudySeconds;
        private int errorWordCount;
        private int errorSentenceCount;
        private int masteredWordCount;
        private int dictationCompleted;
        private int streakDays;
        private int checklistPending;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProgressTrends {
        private int weeklyPlanProgress;
        private int weeklyStudySeconds;
        private List<DailyTrendPoint> trends;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyTrendPoint {
        private String date;
        private int seconds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChecklistSnapshot {
        private List<ChecklistItem> pending;
        private List<TaskItem> todayTasks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChecklistItem {
        private Integer id;
        private String title;
        private Integer type;
        private boolean alreadyReviewed;
        private LocalDate dueDate;
        private Integer toDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskItem {
        private String title;
        private String description;
        private String category;
        private boolean completed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceSecurity {
        private String currentDevice;
        private LocalDateTime lastLoginTime;
        private String location;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Achievement {
        private String title;
        private String description;
        private boolean unlocked;
        private int currentValue;
        private int targetValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Settings {
        private String username;
        private String email;
        private Boolean reminderEnabled;
        private Integer dailyTargetMinutes;
        private String learningGoal;
        private String nickname;
        private String avatar;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuickAction {
        private String label;
        private String icon;
        private String route;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PasswordUpdate {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Notification {
        private String title;
        private String content;
        private String type;
        private LocalDateTime time;
    }
}
