import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { TicketDetail, TicketStatus } from '@/types'
import { ticketApi } from '@/api'

export const useTicketStore = defineStore('ticket', () => {
  const tickets = ref<TicketDetail[]>([])
  const currentTicket = ref<TicketDetail | null>(null)
  const total = ref(0)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchTickets = async (status?: TicketStatus, page: number = 1, size: number = 10) => {
    loading.value = true
    error.value = null
    try {
      const response = await ticketApi.list(status, page, size)
      tickets.value = response.data.list
      total.value = response.data.total
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取工单列表失败'
    } finally {
      loading.value = false
    }
  }

  const fetchTicket = async (id: number) => {
    loading.value = true
    error.value = null
    try {
      const response = await ticketApi.get(id)
      currentTicket.value = response.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取工单详情失败'
    } finally {
      loading.value = false
    }
  }

  const createTicket = async (data: { title: string; description: string; category: string; orderId?: string }) => {
    loading.value = true
    error.value = null
    try {
      const response = await ticketApi.create({
        title: data.title,
        description: data.description,
        category: data.category as TicketDetail['category'],
        orderId: data.orderId
      })
      return response.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '创建工单失败'
      return null
    } finally {
      loading.value = false
    }
  }

  const closeTicket = async (id: number, reason?: string) => {
    loading.value = true
    error.value = null
    try {
      await ticketApi.close(id, reason ? { reason } : undefined)
      if (currentTicket.value && currentTicket.value.ticketId === id) {
        currentTicket.value.status = 'CLOSED'
        currentTicket.value.statusDesc = '已关闭'
      }
      return true
    } catch (e) {
      error.value = e instanceof Error ? e.message : '关闭工单失败'
      return false
    } finally {
      loading.value = false
    }
  }

  const escalateTicket = async (id: number) => {
    loading.value = true
    error.value = null
    try {
      const response = await ticketApi.escalate(id)
      if (currentTicket.value && currentTicket.value.ticketId === id) {
        currentTicket.value = response.data
      }
      return response.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '催办工单失败'
      return null
    } finally {
      loading.value = false
    }
  }

  return {
    tickets,
    currentTicket,
    total,
    loading,
    error,
    fetchTickets,
    fetchTicket,
    createTicket,
    closeTicket,
    escalateTicket
  }
})
