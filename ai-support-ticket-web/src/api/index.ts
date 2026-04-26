import axios, { type AxiosInstance, type AxiosError } from 'axios'
import type { 
  ApiResponse, 
  TicketDetail, 
  TicketListResponse, 
  CreateTicketRequest, 
  UpdateTicketRequest,
  CloseTicketRequest,
  ChatResponse
} from '@/types'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.request.use(
  (config) => {
    const userId = localStorage.getItem('userId') || '1'
    config.headers['X-User-Id'] = userId
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

apiClient.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error: AxiosError<ApiResponse<unknown>>) => {
    const message = error.response?.data?.message || error.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

export const ticketApi = {
  create: (data: CreateTicketRequest): Promise<ApiResponse<TicketDetail>> => {
    return apiClient.post('/api/tickets', data)
  },

  get: (id: number): Promise<ApiResponse<TicketDetail>> => {
    return apiClient.get(`/api/tickets/${id}`)
  },

  list: (status?: string, page: number = 1, size: number = 10): Promise<ApiResponse<TicketListResponse>> => {
    const params = new URLSearchParams()
    params.append('page', page.toString())
    params.append('size', size.toString())
    if (status) {
      params.append('status', status)
    }
    return apiClient.get(`/api/tickets?${params.toString()}`)
  },

  update: (id: number, data: UpdateTicketRequest): Promise<ApiResponse<void>> => {
    return apiClient.put(`/api/tickets/${id}`, data)
  },

  close: (id: number, data?: CloseTicketRequest): Promise<ApiResponse<void>> => {
    return apiClient.post(`/api/tickets/${id}/close`, data || {})
  },

  escalate: (id: number): Promise<ApiResponse<TicketDetail>> => {
    return apiClient.post(`/api/tickets/${id}/escalate`)
  }
}

export const chatApi = {
  send: (message: string): Promise<ApiResponse<ChatResponse>> => {
    return apiClient.post('/api/chat', { message })
  },

  stream: (message: string, onMessage: (chunk: { content: string; done: boolean }) => void, onError?: (error: Error) => void): () => void => {
    const userId = localStorage.getItem('userId') || '1'
    const controller = new AbortController()
    
    fetch(`${API_BASE_URL}/api/chat/stream`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
        'X-User-Id': userId
      },
      body: JSON.stringify({ message }),
      signal: controller.signal
    })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        
        const reader = response.body?.getReader()
        if (!reader) {
          throw new Error('No reader available')
        }

        const decoder = new TextDecoder()
        let buffer = ''

        while (true) {
          const { done, value } = await reader.read()
          if (done) break

          buffer += decoder.decode(value, { stream: true })
          const lines = buffer.split('\n')
          buffer = lines.pop() || ''

          for (const line of lines) {
            if (line.startsWith('data:')) {
              const data = line.slice(5).trim()
              if (data) {
                try {
                  const chunk = JSON.parse(data)
                  onMessage(chunk)
                  if (chunk.done) {
                    return
                  }
                } catch {
                  // ignore parse errors
                }
              }
            }
          }
        }
      })
      .catch((error) => {
        if (error.name !== 'AbortError' && onError) {
          onError(error)
        }
      })

    return () => controller.abort()
  }
}

export default apiClient
