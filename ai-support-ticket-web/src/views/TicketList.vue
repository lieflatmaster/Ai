<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useTicketStore } from '@/stores/ticket'
import { RouterLink } from 'vue-router'
import type { TicketStatus } from '@/types'
import { STATUS_COLOR } from '@/types'

const ticketStore = useTicketStore()
const selectedStatus = ref<TicketStatus | ''>('')
const currentPage = ref(1)
const pageSize = ref(10)

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'PENDING', label: '待处理' },
  { value: 'PROCESSING', label: '处理中' },
  { value: 'RESOLVED', label: '已解决' },
  { value: 'CLOSED', label: '已关闭' }
]

const fetchTickets = () => {
  ticketStore.fetchTickets(selectedStatus.value || undefined, currentPage.value, pageSize.value)
}

onMounted(() => {
  fetchTickets()
})

watch([selectedStatus, currentPage], () => {
  fetchTickets()
})

const totalPages = ref(1)
watch(() => ticketStore.total, (newTotal) => {
  totalPages.value = Math.ceil(newTotal / pageSize.value)
})

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<template>
  <div class="px-4 py-6 sm:px-0">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-900">工单列表</h2>
      <RouterLink
        to="/tickets/create"
        class="px-4 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600 transition-colors"
      >
        创建工单
      </RouterLink>
    </div>

    <div class="bg-white rounded-lg shadow-md">
      <div class="p-4 border-b border-gray-200">
        <div class="flex items-center space-x-4">
          <label class="text-sm font-medium text-gray-700">状态筛选：</label>
          <select
            v-model="selectedStatus"
            class="rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option v-for="option in statusOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </div>
      </div>

      <div v-if="ticketStore.loading" class="p-8 text-center text-gray-500">
        加载中...
      </div>

      <div v-else-if="ticketStore.tickets.length === 0" class="p-8 text-center text-gray-500">
        暂无工单数据
      </div>

      <div v-else class="divide-y divide-gray-200">
        <RouterLink
          v-for="ticket in ticketStore.tickets"
          :key="ticket.ticketId"
          :to="`/tickets/${ticket.ticketId}`"
          class="block p-4 hover:bg-gray-50 transition-colors"
        >
          <div class="flex justify-between items-start">
            <div class="flex-1">
              <div class="flex items-center space-x-3">
                <span class="text-sm text-gray-500">#{{ ticket.ticketId }}</span>
                <span
                  class="px-2 py-1 text-xs font-medium rounded-full"
                  :class="STATUS_COLOR[ticket.status]"
                >
                  {{ ticket.statusDesc }}
                </span>
                <span class="px-2 py-1 text-xs font-medium rounded-full bg-gray-100 text-gray-600">
                  {{ ticket.categoryDesc }}
                </span>
              </div>
              <h3 class="mt-2 text-lg font-medium text-gray-900">{{ ticket.title }}</h3>
              <p class="mt-1 text-sm text-gray-500 line-clamp-2">{{ ticket.description || '暂无描述' }}</p>
              <div class="mt-2 text-xs text-gray-400">
                创建时间：{{ formatDate(ticket.createdAt) }}
              </div>
            </div>
            <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </div>
        </RouterLink>
      </div>

      <div v-if="ticketStore.tickets.length > 0" class="p-4 border-t border-gray-200 flex justify-between items-center">
        <span class="text-sm text-gray-500">
          共 {{ ticketStore.total }} 条记录
        </span>
        <div class="flex space-x-2">
          <button
            @click="currentPage = Math.max(1, currentPage - 1)"
            :disabled="currentPage === 1"
            class="px-3 py-1 rounded border border-gray-300 text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
          >
            上一页
          </button>
          <span class="px-3 py-1 text-sm text-gray-600">
            {{ currentPage }} / {{ totalPages }}
          </span>
          <button
            @click="currentPage = Math.min(totalPages, currentPage + 1)"
            :disabled="currentPage >= totalPages"
            class="px-3 py-1 rounded border border-gray-300 text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
