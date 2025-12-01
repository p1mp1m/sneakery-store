/**
 * PDF Invoice Generator
 * Simple HTML to PDF conversion for order invoices
 */

/**
 * Generate invoice HTML
 */
function generateInvoiceHtml(order) {
  const formatCurrency = (value) => {
    if (!value && value !== 0) return '0 ₫'
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
  }

  const formatDate = (date) => {
    if (!date) return 'N/A'
    const d = new Date(date)
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const month = d.getMonth() + 1
    const year = d.getFullYear()
    const monthNames = ['tháng 1', 'tháng 2', 'tháng 3', 'tháng 4', 'tháng 5', 'tháng 6', 
                        'tháng 7', 'tháng 8', 'tháng 9', 'tháng 10', 'tháng 11', 'tháng 12']
    return `lúc ${hours}:${minutes} ${day} ${monthNames[month - 1]}, ${year}`
  }

  const getStatusLabel = (status) => {
    if (!status) return 'N/A'
    const statusMap = {
      'pending': 'Chờ xử lý',
      'processing': 'Đang xử lý',
      'shipped': 'Đã gửi hàng',
      'delivered': 'Hoàn thành',
      'completed': 'Hoàn thành',
      'cancelled': 'Đã hủy',
      'confirmed': 'Đã xác nhận',
      'packed': 'Đã đóng gói',
      'refunded': 'Đã hoàn tiền'
    }
    return statusMap[status.toLowerCase()] || status
  }

  const getPaymentMethodLabel = (method) => {
    if (!method) return 'N/A'
    const methodMap = {
      'COD': 'Thanh toán khi nhận hàng',
      'BANK_TRANSFER': 'Chuyển khoản ngân hàng',
      'CREDIT_CARD': 'Thẻ tín dụng',
      'EWALLET': 'Ví điện tử'
    }
    return methodMap[method] || method
  }

  // Format địa chỉ giao hàng
  const formatShippingAddress = (address) => {
    if (!address) return 'Không có thông tin'
    
    const parts = []
    if (address.recipientName) parts.push(`<strong>${address.recipientName}</strong>`)
    if (address.phone) parts.push(address.phone)
    if (address.line1) parts.push(address.line1)
    if (address.line2) parts.push(address.line2)
    
    const locationParts = []
    if (address.ward) locationParts.push(address.ward)
    if (address.district) locationParts.push(address.district)
    if (address.city) locationParts.push(address.city)
    if (locationParts.length > 0) parts.push(locationParts.join(', '))
    
    if (address.postalCode) parts.push(`Mã bưu điện: ${address.postalCode}`)
    
    return parts.length > 0 ? parts.join('<br>') : 'Không có thông tin'
  }

  // Lấy order items từ orderDetails hoặc items
  const orderItems = order.orderDetails || order.items || []

  // Lấy thông tin thanh toán
  const paymentMethod = order.payment?.paymentMethod || order.paymentMethod
  const paymentStatus = order.payment?.status

  return `
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Hóa đơn #${order.id}</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 40px; color: #333; background: #f5f5f5; }
    .invoice { max-width: 800px; margin: 0 auto; background: white; border: 1px solid #ddd; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
    .header { display: flex; justify-content: space-between; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 2px solid #667eea; }
    .logo-section { }
    .logo { font-size: 36px; font-weight: bold; color: #667eea; display: flex; align-items: center; gap: 8px; }
    .logo-icon { width: 40px; height: 40px; background: #667eea; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; color: white; font-size: 24px; }
    .logo-subtitle { color: #666; margin-top: 4px; font-size: 14px; }
    .invoice-details { text-align: right; }
    .invoice-number { font-size: 28px; font-weight: bold; color: #2563eb; margin-bottom: 8px; }
    .invoice-date { color: #666; font-size: 14px; margin-bottom: 10px; }
    .status-badge { display: inline-block; padding: 6px 16px; border-radius: 20px; font-size: 12px; font-weight: 600; margin-top: 8px; }
    .status-pending { background: #fff3cd; color: #856404; }
    .status-processing { background: #cfe2ff; color: #084298; }
    .status-shipped { background: #d1e7dd; color: #0f5132; }
    .status-completed { background: #d1e7dd; color: #0f5132; }
    .status-delivered { background: #d1e7dd; color: #0f5132; }
    .status-cancelled { background: #f8d7da; color: #842029; }
    .status-confirmed { background: #e0d4f7; color: #5b21b6; }
    .status-packed { background: #e0d4f7; color: #5b21b6; }
    .status-refunded { background: #ffe4cc; color: #cc6600; }
    
    .divider { height: 2px; background: #2563eb; margin: 30px 0; }
    
    .info-section { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; margin-bottom: 30px; }
    .info-block { background: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #667eea; }
    .info-title { font-size: 16px; font-weight: 600; color: #667eea; margin-bottom: 12px; display: flex; align-items: center; gap: 8px; }
    .info-content { font-size: 14px; line-height: 1.8; color: #333; }
    .info-content strong { color: #000; }
    
    .section { margin-bottom: 30px; }
    .section-title { font-size: 18px; font-weight: 600; margin-bottom: 15px; color: #2563eb; }
    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    thead { background: #2563eb; color: white; }
    th { padding: 14px; text-align: left; font-weight: 600; font-size: 13px; }
    th.text-right,
    td.text-right {
      text-align: right;
    }
    th.text-center,
    td.text-center {
      text-align: center;
    }
    td { padding: 12px 14px; border-bottom: 1px solid #e5e7eb; }
    tbody tr:hover { background: #f9fafb; }
    .total-row { background: #f8f9fa; font-weight: bold; font-size: 16px; }
    .total-row td { border-top: 2px solid #667eea; padding-top: 18px; padding-bottom: 18px; }
    .product-name { font-weight: 600; color: #111; }
    .product-brand { font-size: 12px; color: #666; margin-top: 2px; }
    .product-variant { font-size: 12px; color: #666; margin-top: 4px; }
    
    .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #ddd; text-align: center; color: #666; font-size: 13px; line-height: 1.8; }
    .footer p { margin: 4px 0; }
    
    @media print {
      body { padding: 0; background: white; }
      .invoice { border: none; box-shadow: none; padding: 20px; }
    }
  </style>
</head>
<body>
  <div class="invoice">
    <div class="header">
      <div class="logo-section">
        <div class="logo">
          <span class="logo-icon">🟣</span>
          Sneakery Store
        </div>
        <div class="logo-subtitle">Premium Sneakers & Footwear</div>
      </div>
      <div class="invoice-details">
        <div class="invoice-number">HÓA ĐƠN #${order.id}</div>
        <div class="invoice-date">Ngày: ${formatDate(order.createdAt)}</div>
        <div>
          <span class="status-badge status-${(order.status || '').toLowerCase()}">${getStatusLabel(order.status)}</span>
        </div>
      </div>
    </div>

    <div class="divider"></div>

    <div class="info-section">
      <div class="info-block">
        <div class="info-title">👤 THÔNG TIN KHÁCH HÀNG</div>
        <div class="info-content">
          <div><strong>${order.customerName || 'N/A'}</strong></div>
          <div style="margin-top: 8px;">${order.customerEmail || 'N/A'}</div>
          ${order.customerPhone ? `<div style="margin-top: 4px;">📞 ${order.customerPhone}</div>` : ''}
        </div>
      </div>
      <div class="info-block">
        <div class="info-title">🚚 ĐỊA CHỈ GIAO HÀNG</div>
        <div class="info-content">
          ${formatShippingAddress(order.addressShipping)}
        </div>
      </div>
    </div>

    <div class="section">
      <div class="section-title">📦 Chi tiết đơn hàng</div>
      <table>
        <thead>
          <tr>
            <th>Sản phẩm</th>
            <th class="text-center">SKU</th>
            <th class="text-center">Số lượng</th>
            <th class="text-right">Đơn giá</th>
            <th class="text-right">Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          ${orderItems.length > 0 ? orderItems.map(item => {
  const productName = item.productName || 'Sản phẩm'
  const brandName = item.brandName || ''
  const size = item.size ? `Size: ${item.size}` : ''
  const color = item.color ? `Màu: ${item.color}` : ''
  const variantInfo = [size, color].filter(Boolean).join(' | ')
  const sku = item.sku || "N/A"
  const quantity = item.quantity || 1
  const unitPrice = item.unitPrice || 0
  const totalPrice = item.totalPrice || (unitPrice * quantity)
  // const vat = totalPrice * 0.1

  return `
  <tr>
    <td>
      <div class="product-name">${productName}</div>
      ${brandName ? `<div class="product-brand">${brandName}</div>` : ''}
      ${variantInfo ? `<div class="product-variant">${variantInfo}</div>` : ''}
    </td>
    <td class="text-center">${sku}</td>
    <td class="text-center">${quantity}</td>
    <td class="text-right">${formatCurrency(unitPrice)}</td>
    <td class="text-right">${formatCurrency(totalPrice)}</td>
  </tr>
  `
}).join('') : `
<tr>
  <td colspan="5" style="text-align:center; padding:20px; color:#666;">
    Không có sản phẩm
  </td>
</tr>
`}

<!-- SUMMARY SECTION -->
<tr>
  <td colspan="4" style="text-align:right; padding-top:20px;">Tạm tính (Subtotal):</td>
  <td class="text-right">${formatCurrency(order.subtotal || 0)}</td>
</tr>

${
  order.discountAmount > 0
    ? `
<tr>
  <td colspan="4" style="text-align:right; color:#dc2626;">Giảm giá Coupon:</td>
  <td class="text-right" style="color:#dc2626;">- ${formatCurrency(order.discountAmount)}</td>
</tr>
`
    : ""
}

${
  order.pointsUsed > 0
    ? `
<tr>
  <td colspan="4" style="text-align:right; color:#0ea5e9;">Điểm đã dùng (${order.pointsUsed} điểm):</td>
  <td class="text-right" style="color:#0ea5e9;">- ${formatCurrency(order.pointsDiscount || 0)}</td>
</tr>
`
    : ""
}

<tr>
  <td colspan="4" style="text-align:right;">Phí vận chuyển:</td>
  <td class="text-right">${formatCurrency(order.shippingFee || 0)}</td>
</tr>

<tr>
  <td colspan="4" style="text-align:right;">Thuế VAT:</td>
  <td class="text-right">${formatCurrency(order.taxAmount || 0)}</td>
</tr>

<tr class="total-row">
  <td colspan="4" style="text-align:right; font-size:16px; padding-top:15px;">
    <strong>Tổng cộng</strong>
  </td>
  <td style="text-align:right; font-size:18px; color:#2563eb;">
    <strong>${formatCurrency(order.totalAmount || 0)}</strong>
  </td>
</tr>
        </tbody>
      </table>
    </div>

    ${paymentMethod ? `
<div class="section">
  <div class="info-block" style="margin-top: 0;">
    <div class="info-title">💳 Thông tin thanh toán</div>
    <div class="info-content">
      <div><strong>Phương thức:</strong> ${getPaymentMethodLabel(paymentMethod)}</div>

      <!-- SỐ TIỀN THANH TOÁN (ĐÃ THÊM) -->
      <div style="margin-top: 8px;">
        <strong>Số tiền thanh toán:</strong> ${formatCurrency(order.payment?.amount || order.totalAmount || 0)}
      </div>

      ${paymentStatus ? `
      <div style="margin-top: 8px;">
        <strong>Trạng thái:</strong> ${paymentStatus}
      </div>` : ''}

      ${order.payment?.paidAt ? `
      <div style="margin-top: 8px;">
        <strong>Ngày thanh toán:</strong> ${formatDate(order.payment.paidAt)}
      </div>` : ''}
    </div>
  </div>
</div>
` : ''}

    <div class="footer">
      <p style="font-size: 15px; font-weight: 600; color: #333; margin-bottom: 8px;">Cảm ơn quý khách đã mua hàng tại Sneakery Store!</p>
      <p>Hotline: 1900-xxxx | Email: support@sneakery.com</p>
      <p>www.sneakery.com</p>
    </div>
  </div>
</body>
</html>
  `
}

/**
 * Print invoice
 */
export function printInvoice(order) {
  const html = generateInvoiceHtml(order)
  const printWindow = window.open('', '_blank')
  
  if (printWindow) {
    printWindow.document.write(html)
    printWindow.document.close()
    
    // Wait for content to load then print
    printWindow.onload = () => {
      setTimeout(() => {
        printWindow.print()
      }, 250)
    }
  } else {
    console.error('Could not open print window. Popup blocked?')
  }
}

/**
 * Download invoice as HTML (for later PDF conversion)
 */
export function downloadInvoiceHtml(order) {
  const html = generateInvoiceHtml(order)
  const blob = new Blob([html], { type: 'text/html' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  
  link.setAttribute('href', url)
  link.setAttribute('download', `invoice-${order.id}.html`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

