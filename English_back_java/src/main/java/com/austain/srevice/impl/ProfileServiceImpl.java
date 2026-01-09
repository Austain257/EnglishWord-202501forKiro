package com.austain.srevice.impl;

import com.austain.domain.dto.profile.ProfileDashboardDTO;
import com.austain.domain.dto.profile.ProfileDashboardDTO;
import com.austain.domain.dto.profile.ProfileDashboardDTO.ProgressTrends;
import com.austain.domain.dto.profile.ProfileDashboardDTO.ChecklistSnapshot;
import com.austain.domain.dto.profile.ProfileDashboardDTO.ChecklistItem;
import com.austain.domain.dto.profile.ProfileDashboardDTO.DailyTrendPoint;
import com.austain.domain.dto.profile.ProfileDashboardDTO.DeviceSecurity;
import com.austain.domain.dto.profile.ProfileDashboardDTO.Notification;
import com.austain.domain.dto.profile.ProfileDashboardDTO.PasswordUpdate;
import com.austain.domain.dto.profile.ProfileDashboardDTO.ProfileSummary;
import com.austain.domain.dto.profile.ProfileDashboardDTO.QuickAction;
import com.austain.domain.dto.profile.ProfileDashboardDTO.Settings;
import com.austain.domain.dto.profile.ProfileDashboardDTO.StudyOverview;
import com.austain.domain.dto.profile.ProfileDashboardDTO.Achievement;
import com.austain.domain.dto.profile.ProfileDashboardDTO.TaskItem;
import com.austain.domain.dto.RecordResult;
import com.austain.domain.dto.StudyStatVO;
import com.austain.domain.po.User;
import com.austain.domain.po.UserStudyDaily;
import com.austain.mapper.EnglishMapper;
import com.austain.mapper.StudyRecordMapper;
import com.austain.mapper.StudyStatMapper;
import com.austain.mapper.UserMapper;
import com.austain.mapper.WordStudyRecordMapper;
import com.austain.srevice.ProfileService;
import com.austain.srevice.StudySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EnglishMapper englishMapper;

    @Autowired
    private StudySessionService studySessionService;

    @Autowired
    private StudyStatMapper studyStatMapper;

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    @Autowired
    private WordStudyRecordMapper wordStudyRecordMapper;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd");
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ProfileDashboardDTO getProfile(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        ProfileSummary summary = buildSummary(user);
        StudyOverview overview = buildOverview(userId);
        ProgressTrends progressTrends = buildProgress(userId, summary.getDailyTargetMinutes());
        ChecklistSnapshot checklist = buildChecklist(userId);
        DeviceSecurity device = buildDevice(user);
        List<Achievement> achievements = buildAchievements(overview);
        Settings settings = Settings.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .nickname(user.getNickname())
                .learningGoal(user.getLearningGoal())
                .dailyTargetMinutes(summary.getDailyTargetMinutes())
                .reminderEnabled(summary.getReminderEnabled())
                .build();

        List<QuickAction> actions = List.of(
                new QuickAction("错词学习", "BookOpenIcon", "/word/error-book"),
                new QuickAction("错句学习", "ChatBubbleIcon", "/sentence/error-book"),
                new QuickAction("听写训练", "MicrophoneIcon", "/word/dictation"),
                new QuickAction("学习清单", "ClipboardIcon", "/checklist")
        );

        List<Notification> notifications = buildNotifications(summary, overview);

        return ProfileDashboardDTO.builder()
                .summary(summary)
                .overview(overview)
                .progress(progressTrends)
                .checklist(checklist)
                .device(device)
                .achievements(achievements)
                .settings(settings)
                .quickActions(actions)
                .notifications(notifications)
                .build();
    }

    @Override
    public Settings updateSettings(Long userId, Settings settings) {
        if (settings == null) {
            throw new RuntimeException("设置内容不能为空");
        }

        User currentUser = userMapper.findById(userId);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }

        User update = new User();
        update.setId(userId);

        // 用户名
        if (!StringUtils.hasText(settings.getUsername())) {
            throw new RuntimeException("用户名不能为空");
        }
        String username = settings.getUsername().trim();
        if (!username.equals(currentUser.getUsername())) {
            if (userMapper.countByUsernameExcluding(username, userId) > 0) {
                throw new RuntimeException("用户名已存在");
            }
            update.setUsername(username);
            currentUser.setUsername(username);
        }

        // 邮箱（可选）
        if (settings.getEmail() != null) {
            String email = settings.getEmail().trim();
            if (!email.equals(currentUser.getEmail())) {
                if (StringUtils.hasText(email)) {
                    if (userMapper.countByEmailExcluding(email, userId) > 0) {
                        throw new RuntimeException("邮箱已被使用");
                    }
                    update.setEmail(email);
                    currentUser.setEmail(email);
                } else {
                    update.setEmail("");
                    currentUser.setEmail("");
                }
            }
        }

        if (settings.getNickname() != null) {
            update.setNickname(settings.getNickname());
            currentUser.setNickname(settings.getNickname());
        }
        if (settings.getAvatar() != null) {
            update.setAvatar(settings.getAvatar());
            currentUser.setAvatar(settings.getAvatar());
        }
        if (settings.getLearningGoal() != null) {
            update.setLearningGoal(settings.getLearningGoal());
            currentUser.setLearningGoal(settings.getLearningGoal());
        }
        if (settings.getDailyTargetMinutes() != null) {
            int dailyTarget = Math.max(10, settings.getDailyTargetMinutes());
            update.setDailyTarget(dailyTarget);
            currentUser.setDailyTarget(dailyTarget);
        }
        if (settings.getReminderEnabled() != null) {
            int reminder = settings.getReminderEnabled() ? 1 : 0;
            update.setReminderEnabled(reminder);
            currentUser.setReminderEnabled(reminder);
        }

        userMapper.updateProfile(update);

        return Settings.builder()
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .nickname(currentUser.getNickname())
                .avatar(currentUser.getAvatar())
                .learningGoal(currentUser.getLearningGoal())
                .dailyTargetMinutes(currentUser.getDailyTarget())
                .reminderEnabled(currentUser.getReminderEnabled() == null || currentUser.getReminderEnabled() == 1)
                .build();
    }

    @Override
    public void updatePassword(Long userId, PasswordUpdate passwordUpdate) {
        if (passwordUpdate == null
                || !StringUtils.hasText(passwordUpdate.getCurrentPassword())
                || !StringUtils.hasText(passwordUpdate.getNewPassword())
                || !StringUtils.hasText(passwordUpdate.getConfirmPassword())) {
            throw new RuntimeException("密码参数不能为空");
        }
        if (!passwordUpdate.getNewPassword().equals(passwordUpdate.getConfirmPassword())) {
            throw new RuntimeException("两次输入的新密码不一致");
        }
        if (passwordUpdate.getNewPassword().length() < 6) {
            throw new RuntimeException("新密码长度不能少于6位");
        }
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(passwordUpdate.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        String encoded = passwordEncoder.encode(passwordUpdate.getNewPassword());
        userMapper.updatePassword(userId, encoded);
    }

    private ProfileSummary buildSummary(User user) {
        Integer dailyTarget = user.getDailyTarget();
        if (dailyTarget == null || dailyTarget <= 0) {
            dailyTarget = 60;
        }
        Boolean reminder = user.getReminderEnabled() == null || user.getReminderEnabled() == 1;

        return ProfileSummary.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .joinDate(user.getCreateTime() != null ? user.getCreateTime().toLocalDate() : LocalDate.now())
                .lastLoginTime(user.getLastLoginTime())
                .learningGoal(user.getLearningGoal())
                .dailyTargetMinutes(dailyTarget)
                .reminderEnabled(reminder)
                .build();
    }

    private StudyOverview buildOverview(Long userId) {
        StudyStatVO statVO = studySessionService.getTodayStats(userId);
        List<RecordResult> checklist = studyRecordMapper.getListAll(userId.intValue(), 1);
        int errorWordCount = englishMapper.countErrorWords(userId.intValue());
        int errorSentenceCount = englishMapper.countErrorSentences(userId.intValue());
        int masteredWordCount = englishMapper.countMasteredWords(userId.intValue());
        int dictationCompleted = wordStudyRecordMapper.countCompletedSessions(userId);
        int streakDays = calculateStreakDays(userId);

        return StudyOverview.builder()
                .totalStudySeconds(statVO.getTotalSec() == null ? 0 : statVO.getTotalSec())
                .todayStudySeconds(statVO.getDayTotalSec() == null ? 0 : statVO.getDayTotalSec())
                .errorWordCount(errorWordCount)
                .errorSentenceCount(errorSentenceCount)
                .masteredWordCount(masteredWordCount)
                .dictationCompleted(dictationCompleted)
                .streakDays(streakDays)
                .checklistPending(checklist == null ? 0 : checklist.size())
                .build();
    }

    private ProgressTrends buildProgress(Long userId, Integer dailyTargetMinutes) {
        int targetSeconds = (dailyTargetMinutes == null ? 60 : dailyTargetMinutes) * 60;
        int weeklyPlan = targetSeconds * 7;

        List<UserStudyDaily> recentStats = studyStatMapper.findRecentStats(userId, 7);
        if (recentStats == null) recentStats = List.of();

        List<DailyTrendPoint> points = recentStats.stream()
                .sorted(Comparator.comparing(UserStudyDaily::getStatDate))
                .map(item -> DailyTrendPoint.builder()
                        .date(item.getStatDate().format(DATE_FORMAT))
                        .seconds(item.getTotalSec() == null ? 0 : item.getTotalSec())
                        .build())
                .collect(Collectors.toList());

        int weeklyTotal = points.stream().mapToInt(DailyTrendPoint::getSeconds).sum();
        int weeklyProgress = weeklyPlan == 0 ? 0 : Math.min(100, weeklyTotal * 100 / weeklyPlan);

        return ProgressTrends.builder()
                .weeklyPlanProgress(weeklyProgress)
                .weeklyStudySeconds(weeklyTotal)
                .trends(points)
                .build();
    }

    private ChecklistSnapshot buildChecklist(Long userId) {
        List<RecordResult> list = studyRecordMapper.getListAll(userId.intValue(), 1);
        List<ChecklistItem> items = list == null ? List.of() : list.stream()
                .limit(5)
                .map(item -> ChecklistItem.builder()
                        .id(item.getId())
                        .title(item.getLearningRecord())
                        .type(item.getType())
                        .alreadyReviewed(item.getAlreadyReviewed() == 1)
                        .dueDate(item.getDueDate())
                        .toDate(item.getSelected())
                        .build())
                .collect(Collectors.toList());

        List<TaskItem> todayTasks = List.of(
                TaskItem.builder().title("错词巩固 12 词").description("来自错词本").category("word").completed(false).build(),
                TaskItem.builder().title("句子听写 3 条").description("来自句子听写").category("sentence").completed(true).build()
        );

        return ChecklistSnapshot.builder()
                .pending(items)
                .todayTasks(todayTasks)
                .build();
    }

    private DeviceSecurity buildDevice(User user) {
        return DeviceSecurity.builder()
                .currentDevice("Web · Chrome")
                .lastLoginTime(user.getLastLoginTime())
                .location("上海 · 浦东新区")
                .build();
    }

    private List<Achievement> buildAchievements(StudyOverview overview) {
        return List.of(
                Achievement.builder()
                        .title("坚持 30 天")
                        .description("连续打卡 30 天")
                        .currentValue(overview.getStreakDays())
                        .targetValue(30)
                        .unlocked(overview.getStreakDays() >= 30)
                        .build(),
                Achievement.builder()
                        .title("掌握 500 词")
                        .description("已掌握单词数达到 500")
                        .currentValue(overview.getMasteredWordCount())
                        .targetValue(500)
                        .unlocked(overview.getMasteredWordCount() >= 500)
                        .build()
        );
    }

    private int calculateStreakDays(Long userId) {
        List<UserStudyDaily> recentStats = studyStatMapper.findRecentStats(userId, 30);
        if (recentStats == null || recentStats.isEmpty()) {
            return 0;
        }
        Map<LocalDate, Integer> dailyMap = new HashMap<>();
        for (UserStudyDaily stat : recentStats) {
            if (stat.getStatDate() != null) {
                dailyMap.put(stat.getStatDate(), stat.getTotalSec() == null ? 0 : stat.getTotalSec());
            }
        }
        int streak = 0;
        LocalDate cursor = LocalDate.now();
        while (true) {
            Integer sec = dailyMap.get(cursor);
            if (sec == null || sec <= 0) {
                break;
            }
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private List<Notification> buildNotifications(ProfileSummary summary, StudyOverview overview) {
        return List.of(
                Notification.builder()
                        .title("AI 学习建议")
                        .content("今日已学习 " + overview.getTodayStudySeconds() / 60 + " 分钟，建议继续巩固错词。")
                        .type("suggestion")
                        .time(LocalDateTime.now().minusHours(1))
                        .build(),
                Notification.builder()
                        .title("学习提醒")
                        .content(summary.getReminderEnabled() != null && summary.getReminderEnabled()
                                ? "每日提醒已开启，别忘了完成今日任务。"
                                : "开启学习提醒，帮助你坚持计划。")
                        .type("reminder")
                        .time(LocalDateTime.now().minusHours(3))
                        .build()
        );
    }
}
