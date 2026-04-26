export type TicketStatus = 'PENDING' | 'PROCESSING' | 'RESOLVED' | 'CLOSED'

export type TicketCategory = 'LOGISTICS' | 'REFUND' | 'ACCOUNT' | 'PAYMENT' | 'PRODUCT' | 'OTHER'

export interface TicketDetail {
  ticketId: number
  title: string
  description: string
  category: TicketCategory
  categoryDesc: string
  status: TicketStatus
  statusDesc: string
  priority: number
  orderId: string
  escalateCount: number
  createdAt: string
  updatedAt: string
  closedAt: string | null
}

export interface TicketListResponse {
  total: number
  list: TicketDetail[]
}

export interface CreateTicketRequest {
  title: string
  description: string
  category: TicketCategory
  orderId?: string
}

export interface UpdateTicketRequest {
  description: string
}

export interface CloseTicketRequest {
  reason?: string
}

export interface ChatRequest {
  message: string
}

export interface ChatResponse {
  content: string
}

export interface AiChunk {
  content: string
  done: boolean
  functionCall?: {
    functionName: string
    callId: string
    result: Record<string, unknown>
  }
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: string
  traceId: string
}

export const STATUS_DESC: Record<TicketStatus, string> = {
  PENDING: '待处理',
  PROCESSING: '处理中',
  RESOLVED: '已解决',
  CLOSED: '已关闭'
}

export const CATEGORY_DESC: Record<TicketCategory, string> = {
  LOGISTICS: '物流问题',
  REFUND: '退款问题',
  ACCOUNT: '账户问题',
  PAYMENT: '支付问题',
  PRODUCT: '商品问题',
  OTHER: '其他问题'
}

export const STATUS_COLOR: Record<TicketStatus, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  PROCESSING: 'bg-blue-100 text-blue-800',
  RESOLVED: 'bg-green-100 text-green-800',
  CLOSED: 'bg-gray-100 text-gray-800'
}
