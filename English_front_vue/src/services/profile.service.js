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
  }
}
