export const CATEGORIES = ['电子设备', '证件卡片', '钥匙', '书籍文具', '衣物配饰', '其他']

export const TYPE_LABELS = { lost: '寻物', found: '招领', all: '全部' }

export const MOD_STATUS = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已拒绝', type: 'danger' }
}

export const POST_STATUS = {
  open: { label: '进行中', type: 'success' },
  pending_claim: { label: '认领中', type: 'warning' },
  closed: { label: '已关闭', type: 'info' }
}

export function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').slice(0, 16)
}

export function placeholderImage(category) {
  const map = {
    '电子设备': 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=300&fit=crop',
    '证件卡片': 'https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=400&h=300&fit=crop',
    '钥匙': 'https://images.unsplash.com/photo-1582139329536-e7284fece509?w=400&h=300&fit=crop',
    '书籍文具': 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=300&fit=crop',
    '衣物配饰': 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=400&h=300&fit=crop'
  }
  return map[category] || 'https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=400&h=300&fit=crop'
}

export const QUICK_LOGIN_ACCOUNTS = [
  { label: '管理员', username: 'admin', password: 'admin123' },
  { label: '校园用户', username: 'demo', password: 'demo123' },
  { label: '小明', username: 'xiaoming', password: 'demo123' },
  { label: '丽丽', username: 'lili', password: 'demo123' }
]
