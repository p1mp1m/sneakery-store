/**
 * Export data to Excel/CSV helper functions
 */

/**
 * Convert JSON data to CSV format
 */
export function jsonToCsv(data, columns = null) {
  if (!data || data.length === 0) {
    return ''
  }

  // Use provided columns or extract from first object
  const headers = columns || Object.keys(data[0])
  
  // Create CSV header row
  const csvHeader = headers.join(',')
  
  // Create CSV data rows
  const csvRows = data.map(row => {
    return headers.map(header => {
      const value = row[header]
      // Escape quotes and wrap in quotes if contains comma
      if (value === null || value === undefined) return ''
      const stringValue = String(value)
      if (stringValue.includes(',') || stringValue.includes('"') || stringValue.includes('\n')) {
        return `"${stringValue.replace(/"/g, '""')}"`
      }
      return stringValue
    }).join(',')
  })
  
  return [csvHeader, ...csvRows].join('\n')
}

/**
 * Download data as CSV file
 */
export function downloadCsv(data, filename = 'export.csv', columns = null) {
  const csv = jsonToCsv(data, columns)
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  
  if (link.download !== undefined) {
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', filename)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
  }
}

/**
 * Download data as JSON file
 */
export function downloadJson(data, filename = 'export.json') {
  const json = JSON.stringify(data, null, 2)
  const blob = new Blob([json], { type: 'application/json' })
  const link = document.createElement('a')
  
  if (link.download !== undefined) {
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', filename)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
  }
}

/**
 * Format currency for export
 */
export function formatCurrencyForExport(value) {
  return new Intl.NumberFormat('vi-VN').format(value)
}

/**
 * Format date for export
 */
export function formatDateForExport(date) {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('vi-VN') + ' ' + d.toLocaleTimeString('vi-VN')
}

/**
 * Prepare products data for export
 */
export function prepareProductsForExport(products) {
  return products.map(product => ({
    'ID': product.id,
    'Tên sản phẩm': product.name,
    'Slug': product.slug,
    'Thương hiệu': product.brandName || '',
    'Danh mục': product.categoryName || '',
    'Giá': formatCurrencyForExport(product.price),
    'Tồn kho': product.stockQuantity,
    'Trạng thái': product.isActive ? 'Hoạt động' : 'Ngừng bán',
    'Ngày tạo': formatDateForExport(product.createdAt)
  }))
}

/**
 * Prepare orders data for export
 */
export function prepareOrdersForExport(orders) {
  return orders.map(order => ({
    'Mã đơn': order.id,
    'Khách hàng': order.customerName,
    'Email': order.customerEmail,
    'Số điện thoại': order.customerPhone || '',
    'Tổng tiền': formatCurrencyForExport(order.totalAmount),
    'Trạng thái': order.status,
    'Phương thức thanh toán': order.paymentMethod || '',
    'Địa chỉ giao hàng': order.shippingAddress || '',
    'Ngày đặt': formatDateForExport(order.createdAt)
  }))
}

/**
 * Prepare users data for export
 */
export function prepareUsersForExport(users) {
  return users.map(user => ({
    'ID': user.id,
    'Họ tên': user.fullName,
    'Email': user.email,
    'Số điện thoại': user.phoneNumber || '',
    'Vai trò': user.role,
    'Trạng thái': user.isActive ? 'Hoạt động' : 'Bị khóa',
    'Ngày đăng ký': formatDateForExport(user.createdAt),
    'Lần đăng nhập cuối': formatDateForExport(user.lastLogin)
  }))
}

