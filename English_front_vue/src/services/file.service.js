import api from './api'

export const fileService = {
  parseWordPdf({ file, book }) {
    const formData = new FormData()
    if (book) {
      formData.append('book', new Blob([JSON.stringify(book)], { type: 'application/json' }))
    }
    formData.append('file', file)
    return api.post('/api/files/parseWords', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
