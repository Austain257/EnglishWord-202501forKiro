# API接口测试脚本
# 测试学习时长统计功能

$baseUrl = "http://localhost:8080"
$userId = 1
$bookId = 1

Write-Host "=== 学习时长统计API测试开始 ===" -ForegroundColor Green

# 测试1: 获取今日统计 (初始状态)
Write-Host "`n1. 测试获取今日统计 (初始状态)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/study/stat/today?userId=$userId" -Method GET
    Write-Host "状态码: $($response.StatusCode)"
    Write-Host "响应内容: $($response.Content)"
} catch {
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试2: 开始学习会话
Write-Host "`n2. 测试开始学习会话" -ForegroundColor Yellow
$startSessionBody = @{
    studyScene = "word_review_test"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/study/session/start?userId=$userId`&bookId=$bookId" -Method POST -Body $startSessionBody -ContentType "application/json"
    Write-Host "状态码: $($response.StatusCode)"
    Write-Host "响应内容: $($response.Content)"
    
    # 解析响应获取sessionId
    $responseObj = $response.Content | ConvertFrom-Json
    if ($responseObj.code -eq 1 -and $responseObj.data.sessionId) {
        $sessionId = $responseObj.data.sessionId
        Write-Host "获取到会话ID: $sessionId" -ForegroundColor Green
        
        # 测试3: 发送心跳
        Write-Host "`n3. 测试发送心跳" -ForegroundColor Yellow
        $heartbeatBody = @{
            sessionId = $sessionId
        } | ConvertTo-Json
        
        $response = Invoke-WebRequest -Uri "$baseUrl/api/study/session/heartbeat?userId=$userId" -Method POST -Body $heartbeatBody -ContentType "application/json"
        Write-Host "心跳状态码: $($response.StatusCode)"
        Write-Host "心跳响应: $($response.Content)"
        
        # 等待几秒钟模拟学习过程
        Write-Host "`n等待30秒模拟学习过程..." -ForegroundColor Cyan
        Start-Sleep -Seconds 30
        
        # 测试4: 结束学习会话
        Write-Host "`n4. 测试结束学习会话" -ForegroundColor Yellow
        $finishSessionBody = @{
            sessionId = $sessionId
            clientDurationSec = 30
            source = "test"
        } | ConvertTo-Json
        
        $response = Invoke-WebRequest -Uri "$baseUrl/api/study/session/finish?userId=$userId" -Method POST -Body $finishSessionBody -ContentType "application/json"
        Write-Host "结束会话状态码: $($response.StatusCode)"
        Write-Host "结束会话响应: $($response.Content)"
        
        # 测试5: 再次获取今日统计 (应该有数据)
        Write-Host "`n5. 测试获取今日统计 (结束会话后)" -ForegroundColor Yellow
        $response = Invoke-WebRequest -Uri "$baseUrl/api/study/stat/today?userId=$userId" -Method GET
        Write-Host "状态码: $($response.StatusCode)"
        Write-Host "响应内容: $($response.Content)"
        
    } else {
        Write-Host "开始会话失败，无法继续测试" -ForegroundColor Red
    }
    
} catch {
    Write-Host "开始会话错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== API测试完成 ===" -ForegroundColor Green
