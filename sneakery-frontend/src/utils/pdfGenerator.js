/**
 * PDF Invoice Generator
 * Simple HTML to PDF conversion for order invoices
 */

/**
 * Generate invoice HTML
 */
function generateInvoiceHtml(order) {
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
  }

  const formatDate = (date) => {
    return new Date(date).toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  return `
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Hóa đơn #${order.id}</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 40px; color: #333; }
    .invoice { max-width: 800px; margin: 0 auto; border: 1px solid #ddd; padding: 40px; }
    .header { display: flex; justify-content: space-between; margin-bottom: 40px; border-bottom: 3px solid #667eea; padding-bottom: 20px; }
    .logo { font-size: 32px; font-weight: bold; color: #667eea; }
    .invoice-details { text-align: right; }
    .invoice-number { font-size: 24px; font-weight: bold; color: #667eea; margin-bottom: 8px; }
    .invoice-date { color: #666; }
    .section { margin-bottom: 30px; }
    .section-title { font-size: 18px; font-weight: 600; margin-bottom: 15px; color: #667eea; }
    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
    .info-block { background: #f8f9fa; padding: 15px; border-radius: 8px; }
    .info-label { font-size: 12px; color: #666; margin-bottom: 5px; text-transform: uppercase; }
    .info-value { font-size: 14px; font-weight: 600; }
    table { width: 100%; border-collapse: collapse; }
    thead { background: #667eea; color: white; }
    th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
    th { font-weight: 600; }
    tbody tr:hover { background: #f8f9fa; }
    .total-row { background: #f8f9fa; font-weight: bold; font-size: 16px; }
    .total-row td { border-top: 2px solid #667eea; padding-top: 15px; }
    .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #ddd; text-align: center; color: #666; font-size: 12px; }
    .status-badge { display: inline-block; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600; }
    .status-pending { background: #fff3cd; color: #856404; }
    .status-processing { background: #cfe2ff; color: #084298; }
    .status-shipped { background: #d1e7dd; color: #0f5132; }
    .status-completed { background: #d1e7dd; color: #0f5132; }
    .status-cancelled { background: #f8d7da; color: #842029; }
    @media print {
      body { padding: 0; }
      .invoice { border: none; }
    }
  </style>
</head>
<body>
  <div class="invoice">
    <div class="header">
      <div>
        <div class="logo">🟣 Sneakery Store</div>
        <div style="color: #666; margin-top: 8px;">Premium Sneakers & Footwear</div>
      </div>
      <div class="invoice-details">
        <div class="invoice-number">HÓA ĐƠN #${order.id}</div>
        <div class="invoice-date">Ngày: ${formatDate(order.createdAt)}</div>
        <div style="margin-top: 10px;">
          <span class="status-badge status-${order.status?.toLowerCase()}">${order.status}</span>
        </div>
      </div>
    </div>

    <div class="info-grid section">
      <div class="info-block">
        <div class="info-label">Thông tin khách hàng</div>
        <div class="info-value">${order.customerName}</div>
        <div style="margin-top: 5px; font-size: 13px;">${order.customerEmail}</div>
        ${order.customerPhone ? `<div style="margin-top: 5px; font-size: 13px;">${order.customerPhone}</div>` : ''}
      </div>
      <div class="info-block">
        <div class="info-label">Địa chỉ giao hàng</div>
        <div class="info-value">${order.shippingAddress || 'Không có thông tin'}</div>
        ${order.paymentMethod ? `<div style="margin-top: 10px; font-size: 13px;"><strong>Thanh toán:</strong> ${order.paymentMethod}</div>` : ''}
      </div>
    </div>

    <div class="section">
      <div class="section-title">Chi tiết đơn hàng</div>
      <table>
        <thead>
          <tr>
            <th>Sản phẩm</th>
            <th style="text-align: center;">Số lượng</th>
            <th style="text-align: right;">Đơn giá</th>
            <th style="text-align: right;">Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          ${order.items?.map(item => `
            <tr>
              <td>
                <div style="font-weight: 600;">${item.productName || 'Sản phẩm'}</div>
                ${item.variantName ? `<div style="font-size: 12px; color: #666;">${item.variantName}</div>` : ''}
              </td>
              <td style="text-align: center;">${item.quantity || 1}</td>
              <td style="text-align: right;">${formatCurrency(item.price || 0)}</td>
              <td style="text-align: right;">${formatCurrency((item.price || 0) * (item.quantity || 1))}</td>
            </tr>
          `).join('') || '<tr><td colspan="4" style="text-align: center; color: #666;">Không có sản phẩm</td></tr>'}
          <tr class="total-row">
            <td colspan="3" style="text-align: right;">Tổng cộng:</td>
            <td style="text-align: right; color: #667eea;">${formatCurrency(order.totalAmount)}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="footer">
      <p>Cảm ơn quý khách đã mua hàng tại Sneakery Store!</p>
      <p style="margin-top: 8px;">Hotline: 1900-xxxx | Email: support@sneakery.com</p>
      <p style="margin-top: 8px;">www.sneakery.com</p>
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

