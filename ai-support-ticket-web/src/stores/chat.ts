import { defineStore } from 'pinia'
import { ref } from 'vue'
import { chatApi } from '@/api'

export interface ChatMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: Date
  loading?: boolean
}

export const useChatStore = defineStore('chat', () => {
  const messages = ref<ChatMessage[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const generateId = () => `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`

  const sendMessage = async (content: string) => {
    const userMessage: ChatMessage = {
      id: generateId(),
      role: 'user',
      content,
      timestamp: new Date()
    }
    messages.value.push(userMessage)

    const assistantMessage: ChatMessage = {
      id: generateId(),
      role: 'assistant',
      content: '',
      timestamp: new Date(),
      loading: true
    }
    messages.value.push(assistantMessage)
    loading.value = true
    error.value = null

    return new Promise<void>((resolve) => {
      const abort = chatApi.stream(
        content,
        (chunk) => {
          const lastMessage = messages.value[messages.value.length - 1]
          if (lastMessage.role === 'assistant') {
            lastMessage.content += chunk.content
            lastMessage.loading = false
          }
          if (chunk.done) {
            loading.value = false
            resolve()
          }
        },
        (err) => {
          const lastMessage = messages.value[messages.value.length - 1]
          if (lastMessage.role === 'assistant') {
            lastMessage.content = `抱歉，发生了错误：${err.message}`
            lastMessage.loading = false
          }
          error.value = err.message
          loading.value = false
          resolve()
        }
      )

      return abort
    })
  }

  const clearMessages = () => {
    messages.value = []
  }

  return {
    messages,
    loading,
    error,
    sendMessage,
    clearMessages
  }
})
