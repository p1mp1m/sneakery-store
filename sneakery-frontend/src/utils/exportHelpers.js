/**
 * Export data to Excel/CSV helper functions
 */
import * as XLSX from 'xlsx'

/**
 * Convert JSON data to CSV format with proper encoding
 */
export function jsonToCsv(data, columns = null) {
  // Ensure data is an array
  if (!data) {
    return ''
  }
  
  // If data is not an array, wrap it
  const dataArray = Array.isArray(data) ? data : [data]
  
  if (dataArray.length === 0) {
    return ''
  }

  // Use provided columns or extract from first object
  const headers = columns || Object.keys(dataArray[0])
  
  // Helper function to escape and quote CSV values
  const escapeValue = (value) => {
    if (value === null || value === undefined) return ''
    const stringValue = String(value).trim()
    
    // Always wrap in quotes to preserve special characters and ensure proper encoding
    const escapedValue = stringValue.replace(/"/g, '""')
    return `"${escapedValue}"`
  }
  
  // Create CSV header row (always quoted for proper encoding)
  const csvHeader = headers.map(escapeValue).join(',')
  
  // Create CSV data rows
  const csvRows = dataArray.map(row => {
    return headers.map(header => {
      const value = row[header]
      return escapeValue(value)
    }).join(',')
  })
  
  return [csvHeader, ...csvRows].join('\n')
}

/**
 * Download data as CSV file with UTF-8 BOM for Vietnamese characters
 */
export function downloadCsv(data, filename = 'export.csv', columns = null) {
  // Ensure data is an array
  const dataArray = Array.isArray(data) ? data : (data ? [data] : [])
  
  if (dataArray.length === 0) {
    console.warn('No data to export')
    return
  }
  
  const csv = jsonToCsv(dataArray, columns)
  
  // Add UTF-8 BOM for proper encoding of Vietnamese characters
  const BOM = '\uFEFF'
  const csvWithBOM = BOM + csv
  
  const blob = new Blob([csvWithBOM], { type: 'text/csv;charset=utf-8;' })
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
  // Ensure data is an array
  const dataArray = Array.isArray(data) ? data : (data ? [data] : [])
  const json = JSON.stringify(dataArray, null, 2)
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

/**
 * Export data to Excel with beautiful styling
 * @param {Array} data - Array of objects to export
 * @param {String} filename - Output filename
 * @param {String} sheetName - Sheet name
 */
export function exportToExcelStyled(data, filename = 'export.xlsx', sheetName = 'Sheet1') {
  try {
    // Create workbook
    const workbook = XLSX.utils.book_new()
    
    // Convert data to worksheet
    const worksheet = XLSX.utils.json_to_sheet(data)
    
    // Set column widths for better readability
    const maxWidths = {}
    const keys = Object.keys(data[0] || {})
    
    // Calculate optimal column widths
    keys.forEach(key => {
      let maxLength = key.length
      data.forEach(row => {
        const cellValue = String(row[key] || '')
        if (cellValue.length > maxLength) {
          maxLength = cellValue.length
        }
      })
      maxWidths[key] = { wch: Math.min(Math.max(maxLength + 2, 10), 50) }
    })
    
    worksheet['!cols'] = keys.map(key => maxWidths[key])
    
    // Set header row range
    const range = XLSX.utils.decode_range(worksheet['!ref'])
    
    // Style the header row (row 1)
    for (let col = range.s.c; col <= range.e.c; col++) {
      const cellAddress = XLSX.utils.encode_cell({ r: 0, c: col })
      if (!worksheet[cellAddress]) continue
      
      worksheet[cellAddress].s = {
        fill: {
          fgColor: { rgb: "4472C4" } // Blue header
        },
        font: {
          bold: true,
          color: { rgb: "FFFFFF" },
          sz: 11
        },
        alignment: {
          horizontal: "center",
          vertical: "center",
          wrapText: true
        },
        border: {
          top: { style: "thin", color: { rgb: "000000" } },
          bottom: { style: "thin", color: { rgb: "000000" } },
          left: { style: "thin", color: { rgb: "000000" } },
          right: { style: "thin", color: { rgb: "000000" } }
        }
      }
    }
    
    // Style data rows
    for (let row = range.s.r + 1; row <= range.e.r; row++) {
      for (let col = range.s.c; col <= range.e.c; col++) {
        const cellAddress = XLSX.utils.encode_cell({ r: row, c: col })
        if (!worksheet[cellAddress]) continue
        
        const isEvenRow = row % 2 === 0
        worksheet[cellAddress].s = {
          fill: {
            fgColor: { rgb: isEvenRow ? "F2F2F2" : "FFFFFF" } // Alternating row colors
          },
          font: {
            color: { rgb: "000000" },
            sz: 10
          },
          alignment: {
            horizontal: "left",
            vertical: "center",
            wrapText: true
          },
          border: {
            top: { style: "thin", color: { rgb: "D9D9D9" } },
            bottom: { style: "thin", color: { rgb: "D9D9D9" } },
            left: { style: "thin", color: { rgb: "D9D9D9" } },
            right: { style: "thin", color: { rgb: "D9D9D9" } }
          }
        }
      }
    }
    
    // Add worksheet to workbook
    XLSX.utils.book_append_sheet(workbook, worksheet, sheetName)
    
    // Write file
    XLSX.writeFile(workbook, filename)
    
    return true
  } catch (error) {
    console.error('Error exporting to Excel:', error)
    throw error
  }
}

