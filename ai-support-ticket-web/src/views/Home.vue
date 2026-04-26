<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <div class="px-4 py-6 sm:px-0">
    <div class="text-center py-12">
      <h1 class="text-4xl font-bold text-gray-900 mb-4">
        欢迎使用智能客服工单系统
      </h1>
      <p class="text-lg text-gray-600 mb-8 max-w-2xl mx-auto">
        本系统集成了AI智能对话功能，您可以通过自然语言与系统交互，
        AI会自动识别您的意图并执行相应的操作，如创建工单、查询工单状态等。
      </p>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-4xl mx-auto mt-12">
        <RouterLink
          to="/chat"
          class="block p-6 bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow border border-gray-200"
        >
          <div class="text-primary-600 mb-4">
            <svg class="w-12 h-12 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">AI智能对话</h3>
          <p class="text-gray-600">
            通过自然语言与AI对话，自动创建工单、查询状态、催办工单等
          </p>
        </RouterLink>

        <RouterLink
          to="/tickets"
          class="block p-6 bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow border border-gray-200"
        >
          <div class="text-primary-600 mb-4">
            <svg class="w-12 h-12 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"></path>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">工单管理</h3>
          <p class="text-gray-600">
            查看和管理您的工单，包括创建、查询、关闭和催办等操作
          </p>
        </RouterLink>
      </div>

      <div class="mt-12 p-6 bg-blue-50 rounded-lg max-w-4xl mx-auto">
        <h3 class="text-lg font-semibold text-blue-900 mb-4">使用示例</h3>
        <div class="text-left text-blue-800 space-y-2">
          <p>• "帮我创建一个物流问题的工单，订单号是12345"</p>
          <p>• "查询我的工单列表"</p>
          <p>• "帮我查一下工单10086的状态"</p>
          <p>• "帮我关闭工单10086"</p>
          <p>• "催一下工单10086"</p>
        </div>
      </div>
    </div>
  </div>
</template>
