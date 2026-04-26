<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useChatStore } from '@/stores/chat'

const chatStore = useChatStore()
const inputMessage = ref('')
const messagesContainer = ref<HTMLElement | null>(null)

const sendMessage = async () => {
  if (!inputMessage.value.trim() || chatStore.loading) return
  
  const message = inputMessage.value.trim()
  inputMessage.value = ''
  
  await chatStore.sendMessage(message)
  
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const clearChat = () => {
  chatStore.clearMessages()
}
</script>

<template>
  <div class="px-4 py-6 sm:px-0">
    <div class="bg-white rounded-lg shadow-md h-[calc(100vh-200px)] flex flex-col">
      <div class="px-4 py-3 border-b border-gray-200 flex justify-between items-center">
        <h2 class="text-lg font-semibold text-gray-900">AI智能对话</h2>
        <button
          @click="clearChat"
          class="text-sm text-gray-500 hover:text-gray-700"
        >
          清空对话
        </button>
      </div>
      
      <div 
        ref="messagesContainer"
        class="flex-1 overflow-y-auto p-4 space-y-4"
      >
        <div v-if="chatStore.messages.length === 0" class="text-center py-12 text-gray-500">
          <svg class="w-16 h-16 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
          </svg>
          <p>开始与AI对话吧！</p>
          <p class="text-sm mt-2">例如：帮我创建一个物流问题的工单</p>
        </div>
        
        <div
          v-for="message in chatStore.messages"
          :key="message.id"
          class="flex"
          :class="message.role === 'user' ? 'justify-end' : 'justify-start'"
        >
          <div
            class="max-w-[80%] rounded-lg px-4 py-2"
            :class="message.role === 'user' 
              ? 'bg-primary-500 text-white' 
              : 'bg-gray-100 text-gray-900'"
          >
            <p class="whitespace-pre-wrap">{{ message.content }}</p>
            <span v-if="message.loading" class="inline-block animate-pulse">▊</span>
          </div>
        </div>
      </div>
      
      <div class="p-4 border-t border-gray-200">
        <form @submit.prevent="sendMessage" class="flex space-x-4">
          <input
            v-model="inputMessage"
            type="text"
            placeholder="输入消息..."
            :disabled="chatStore.loading"
            class="flex-1 rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent disabled:bg-gray-100"
          />
          <button
            type="submit"
            :disabled="!inputMessage.trim() || chatStore.loading"
            class="px-6 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {{ chatStore.loading ? '发送中...' : '发送' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>
