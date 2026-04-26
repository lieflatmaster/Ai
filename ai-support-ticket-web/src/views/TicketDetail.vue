<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTicketStore } from '@/stores/ticket'
import { STATUS_COLOR } from '@/types'

const route = useRoute()
const router = useRouter()
const ticketStore = useTicketStore()

const ticketId = Number(route.params.id)
const showCloseModal = ref(false)
const closeReason = ref('')

onMounted(() => {
  ticketStore.fetchTicket(ticketId)
})

const formatDate = (dateStr: string | null) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const handleCloseTicket = async () => {
  const success = await ticketStore.closeTicket(ticketId, closeReason.value)
  if (success) {
    showCloseModal.value = false
    closeReason.value = ''
  }
}

const handleEscalateTicket = async () => {
  await ticketStore.escalateTicket(ticketId)
}

const goBack = () => {
  router.push('/tickets')
}
</script>

<template>
  <div class="px-4 py-6 sm:px-0">
    <div class="mb-6">
      <button
        @click="goBack"
        class="flex items-center text-gray-600 hover:text-gray-900"
      >
        <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
        </svg>
        返回列表
      </button>
    </div>

    <div v-if="ticketStore.loading" class="text-center py-12 text-gray-500">
      加载中...
    </div>

    <div v-else-if="ticketStore.error" class="text-center py-12 text-red-500">
      {{ ticketStore.error }}
    </div>

    <div v-else-if="ticketStore.currentTicket" class="bg-white rounded-lg shadow-md">
      <div class="p-6 border-b border-gray-200">
        <div class="flex justify-between items-start">
          <div>
            <div class="flex items-center space-x-3">
              <span class="text-sm text-gray-500">#{{ ticketStore.currentTicket.ticketId }}</span>
              <span
                class="px-2 py-1 text-xs font-medium rounded-full"
                :class="STATUS_COLOR[ticketStore.currentTicket.status]"
              >
                {{ ticketStore.currentTicket.statusDesc }}
              </span>
              <span class="px-2 py-1 text-xs font-medium rounded-full bg-gray-100 text-gray-600">
                {{ ticketStore.currentTicket.categoryDesc }}
              </span>
            </div>
            <h2 class="mt-3 text-2xl font-bold text-gray-900">
              {{ ticketStore.currentTicket.title }}
            </h2>
          </div>
          <div class="flex space-x-2" v-if="ticketStore.currentTicket.status !== 'CLOSED'">
            <button
              v-if="ticketStore.currentTicket.status === 'PROCESSING'"
              @click="handleEscalateTicket"
              :disabled="ticketStore.loading"
              class="px-4 py-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 disabled:opacity-50 transition-colors"
            >
              催办
            </button>
            <button
              @click="showCloseModal = true"
              :disabled="ticketStore.loading"
              class="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 disabled:opacity-50 transition-colors"
            >
              关闭工单
            </button>
          </div>
        </div>
      </div>

      <div class="p-6 space-y-6">
        <div>
          <h3 class="text-sm font-medium text-gray-500 mb-2">问题描述</h3>
          <p class="text-gray-900 whitespace-pre-wrap">
            {{ ticketStore.currentTicket.description || '暂无描述' }}
          </p>
        </div>

        <div class="grid grid-cols-2 gap-6">
          <div>
            <h3 class="text-sm font-medium text-gray-500 mb-2">关联订单</h3>
            <p class="text-gray-900">{{ ticketStore.currentTicket.orderId || '-' }}</p>
          </div>
          <div>
            <h3 class="text-sm font-medium text-gray-500 mb-2">优先级</h3>
            <p class="text-gray-900">{{ ticketStore.currentTicket.priority }}</p>
          </div>
          <div>
            <h3 class="text-sm font-medium text-gray-500 mb-2">催办次数</h3>
            <p class="text-gray-900">{{ ticketStore.currentTicket.escalateCount }}</p>
          </div>
          <div>
            <h3 class="text-sm font-medium text-gray-500 mb-2">创建时间</h3>
            <p class="text-gray-900">{{ formatDate(ticketStore.currentTicket.createdAt) }}</p>
          </div>
          <div>
            <h3 class="text-sm font-medium text-gray-500 mb-2">更新时间</h3>
            <p class="text-gray-900">{{ formatDate(ticketStore.currentTicket.updatedAt) }}</p>
          </div>
          <div>
            <h3 class="text-sm font-medium text-gray-500 mb-2">关闭时间</h3>
            <p class="text-gray-900">{{ formatDate(ticketStore.currentTicket.closedAt) }}</p>
          </div>
        </div>
      </div>
    </div>

    <div
      v-if="showCloseModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg p-6 w-full max-w-md">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">关闭工单</h3>
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">关闭原因（可选）</label>
          <textarea
            v-model="closeReason"
            rows="3"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            placeholder="请输入关闭原因..."
          ></textarea>
        </div>
        <div class="flex justify-end space-x-3">
          <button
            @click="showCloseModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="handleCloseTicket"
            :disabled="ticketStore.loading"
            class="px-4 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600 disabled:opacity-50"
          >
            确认关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
