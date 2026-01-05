import api from './api'

export const authService = {
  // 用户登录
  async login(credentials) {
    return api.post('/api/auth/login', {
      username: credentials.username,
      password: credentials.password
    })
  },
  
  // 用户注册
  async register(userData) {
    return api.post('/api/auth/register', {
      username: userData.username,
      password: userData.password,
      email: userData.email,
      nickname: userData.nickname
    })
  },
  
  // 用户登出
  async logout() {
    return api.post('/api/auth/logout')
  },
  
  // 验证令牌
  async validateToken() {
    return api.get('/api/auth/validate')
  },
  
  // 刷新令牌
  async refreshToken(oldToken) {
    return api.post('/api/auth/refresh', {
      token: oldToken
    })
  }
}