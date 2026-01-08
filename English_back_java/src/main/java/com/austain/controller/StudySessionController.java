package com.austain.controller;

import com.austain.domain.dto.Result;
import com.austain.domain.dto.StudySessionDTO;
import com.austain.domain.dto.StudyStatVO;
import com.austain.srevice.StudySessionService;
import com.austain.srevice.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 学习会话控制器
 * 提供学习时长统计相关的API接口
 */
@RestController
@RequestMapping("/api/study")
public class StudySessionController {

    @Autowired
    private StudySessionService studySessionService;

    @Autowired
    private TokenService tokenService;

    /**
     * 开始学习会话
     * POST /api/study/session/start
     */
    @PostMapping("/session/start")
    public Result startSession(@RequestBody StudySessionDTO sessionDTO, HttpServletRequest request) {
        try {
            // 从请求头或token中获取用户ID（这里简化处理，实际项目中应该从JWT中解析）
            Long userId = getUserIdFromRequest(request);
            Long bookId = getBookIdFromRequest(request);
            
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            if (bookId == null) {
                return Result.error("未选择课本");
            }
            
            if (sessionDTO.getStudyScene() == null || sessionDTO.getStudyScene().trim().isEmpty()) {
                return Result.error("学习场景不能为空");
            }

            StudyStatVO result = studySessionService.startSession(userId, bookId, sessionDTO.getStudyScene());
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("开始学习会话失败: " + e.getMessage());
        }
    }

    /**
     * 发送心跳
     * POST /api/study/session/heartbeat
     */
    @PostMapping("/session/heartbeat")
    public Result heartbeat(@RequestBody StudySessionDTO sessionDTO, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            if (sessionDTO.getSessionId() == null) {
                return Result.error("会话ID不能为空");
            }

            boolean success = studySessionService.heartbeat(userId, sessionDTO.getSessionId());
            if (success) {
                return Result.success("心跳更新成功");
            } else {
                return Result.error("心跳更新失败，会话可能已过期");
            }
        } catch (Exception e) {
            return Result.error("心跳更新失败: " + e.getMessage());
        }
    }

    /**
     * 结束学习会话
     * POST /api/study/session/finish
     */
    @PostMapping("/session/finish")
    public Result finishSession(@RequestBody StudySessionDTO sessionDTO, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            if (sessionDTO.getSessionId() == null) {
                return Result.error("会话ID不能为空");
            }

            StudyStatVO result = studySessionService.finishSession(userId, sessionDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("结束学习会话失败: " + e.getMessage());
        }
    }

    /**
     * 暂停学习会话
     * POST /api/study/session/pause
     */
    @PostMapping("/session/pause")
    public Result pauseSession(@RequestBody StudySessionDTO sessionDTO, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            if (sessionDTO.getSessionId() == null) {
                return Result.error("会话ID不能为空");
            }

            boolean success = studySessionService.pauseSession(userId, sessionDTO.getSessionId());
            if (success) {
                return Result.success("会话已暂停");
            } else {
                return Result.error("暂停会话失败，会话可能不存在");
            }
        } catch (Exception e) {
            return Result.error("暂停会话失败: " + e.getMessage());
        }
    }

    /**
     * 恢复学习会话
     * POST /api/study/session/resume
     */
    @PostMapping("/session/resume")
    public Result resumeSession(@RequestBody StudySessionDTO sessionDTO, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            if (sessionDTO.getSessionId() == null) {
                return Result.error("会话ID不能为空");
            }

            boolean success = studySessionService.resumeSession(userId, sessionDTO.getSessionId());
            if (success) {
                return Result.success("会话已恢复");
            } else {
                return Result.error("恢复会话失败，会话可能不存在");
            }
        } catch (Exception e) {
            return Result.error("恢复会话失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日学习统计
     * GET /api/study/stat/today
     */
    @GetMapping("/stat/today")
    public Result getTodayStats(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }

            StudyStatVO stats = studySessionService.getTodayStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取学习统计失败: " + e.getMessage());
        }
    }

    /**
     * 从请求中获取用户ID（通过Token验证）
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 优先从Authorization请求头获取Token
        String authHeader = request.getHeader("Authorization");
        String token = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // 移除"Bearer "前缀
        } else if (authHeader != null) {
            token = authHeader; // 直接使用header值作为token
        }
        
        // 如果Authorization header没有token，尝试从X-Token header获取
        if (token == null || token.trim().isEmpty()) {
            token = request.getHeader("X-Token");
        }
        
        // 如果还是没有token，尝试从请求参数获取（用于测试）
        if (token == null || token.trim().isEmpty()) {
            token = request.getParameter("token");
        }
        
        // 通过TokenService验证token并获取用户ID
        if (token != null && !token.trim().isEmpty()) {
            Long userId = tokenService.validateTokenAndGetUserId(token);
            if (userId != null) {
                return userId;
            }
        }
        
        // 临时测试方式：直接从header或参数获取用户ID
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            userIdStr = request.getParameter("userId");
        }
        
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }

    /**
     * 从请求中获取课本ID
     * 这里简化处理，实际项目中可能从用户状态或请求中获取
     */
    private Long getBookIdFromRequest(HttpServletRequest request) {
        // 从请求头中获取课本ID
        String bookIdStr = request.getHeader("X-Book-Id");
        if (bookIdStr != null && !bookIdStr.trim().isEmpty()) {
            try {
                return Long.parseLong(bookIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        // 也可以从请求参数中获取（用于测试）
        String paramBookId = request.getParameter("bookId");
        if (paramBookId != null && !paramBookId.trim().isEmpty()) {
            try {
                return Long.parseLong(paramBookId);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        // 默认返回课本ID 1（用于测试）
        return 1L;
    }
}
