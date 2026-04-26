<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useTicketStore } from '@/stores/ticket'
import type { TicketCategory } from '@/types'
import { CATEGORY_DESC } from '@/types'

const router = useRouter()
const ticketStore = useTicketStore()

const form = ref({
  title: '',
  description: '',
  category: '' as TicketCategory | '',
  orderId: ''
})

const categoryOptions = Object.entries(CATEGORY_DESC).map(([value, label]) => ({
  value,
  label
}))

const submitForm = async () => {
  if (!form.value.title || !form.value.category) {
    return
  }

  const result = await ticketStore.createTicket({
    title: form.value.title,
    description: form.value.description,
    category: form.value.category,
    orderId: form.value.orderId || undefined
  })

  if (result) {
    router.push(`/tickets/${result.ticketId}`)
  }
}
</script>

<template>
  <div class="px-4 py-6 sm:px-0">
    <div class="max-w-2xl mx-auto">
      <h2 class="text-2xl font-bold text-gray-900 mb-6">创建工单</h2>

      <form @submit.prevent="submitForm" class="bg-white rounded-lg shadow-md p-6 space-y-6">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            标题 <span class="text-red-500">*</span>
          </label>
          <input
            v-model="form.title"
            type="text"
            required
            maxlength="200"
            class="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            placeholder="请输入工单标题"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            分类 <span class="text-red-500">*</span>
          </label>
          <select
            v-model="form.category"
            required
            class="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option value="" disabled>请选择分类</option>
            <option v-for="option in categoryOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">问题描述</label>
          <textarea
            v-model="form.description"
            rows="4"
            maxlength="2000"
            class="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            placeholder="请详细描述您遇到的问题..."
          ></textarea>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">关联订单号</label>
          <input
            v-model="form.orderId"
            type="text"
            maxlength="50"
            class="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            placeholder="如有相关订单，请填写订单号"
          />
        </div>

        <div v-if="ticketStore.error" class="p-4 bg-red-50 text-red-600 rounded-lg">
          {{ ticketStore.error }}
        </div>

        <div class="flex justify-end space-x-3">
          <router-link
            to="/tickets"
            class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </router-link>
          <button
            type="submit"
            :disabled="ticketStore.loading || !form.title || !form.category"
            class="px-6 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ ticketStore.loading ? '提交中...' : '提交' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
