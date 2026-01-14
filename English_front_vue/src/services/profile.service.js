import api from './api'

export const profileService = {
  getProfile() {
    return api.get('/api/profile/me')
  },

  updateSettings(payload) {
    return api.put('/api/profile/settings', payload)
  },

  updatePassword(payload) {
    return api.put('/api/profile/password', payload)
  },

  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/api/profile/avatar/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
