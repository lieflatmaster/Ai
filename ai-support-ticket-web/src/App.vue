<script setup lang="ts">
import { RouterLink, RouterView, useRoute } from 'vue-router'
import { computed, ref, onMounted } from 'vue'

const route = useRoute()

const currentPath = computed(() => route.path)

const userId = ref('1')

onMounted(() => {
  userId.value = localStorage.getItem('userId') || '1'
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <nav class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex">
            <div class="flex-shrink-0 flex items-center">
              <RouterLink to="/" class="text-xl font-bold text-primary-600">
                智能客服工单系统
              </RouterLink>
            </div>
            <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
              <RouterLink
                to="/"
                class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
                :class="currentPath === '/' 
                  ? 'border-primary-500 text-gray-900' 
                  : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'"
              >
                首页
              </RouterLink>
              <RouterLink
                to="/chat"
                class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
                :class="currentPath.startsWith('/chat') 
                  ? 'border-primary-500 text-gray-900' 
                  : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'"
              >
                AI对话
              </RouterLink>
              <RouterLink
                to="/tickets"
                class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
                :class="currentPath.startsWith('/tickets') 
                  ? 'border-primary-500 text-gray-900' 
                  : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'"
              >
                工单管理
              </RouterLink>
            </div>
          </div>
          <div class="flex items-center">
            <span class="text-sm text-gray-500">用户ID: {{ userId }}</span>
          </div>
        </div>
      </div>
    </nav>

    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <RouterView />
    </main>
  </div>
</template>
