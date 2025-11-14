/**
 * Import data validation utilities
 * Validates Excel/CSV import data before submission
 */

/**
 * Validate a single product import row
 * @param {Object} row - Product data row
 * @param {number} rowIndex - Row index (for error reporting)
 * @param {Object} options - Validation options
 * @returns {Object} Validation result { isValid: boolean, errors: string[] }
 */
export function validateProductRow(row, rowIndex, options = {}) {
  const errors = [];
  const { brands = [], categories = [] } = options;

  // Required fields
  if (!row.productName || !row.productName.trim()) {
    errors.push('Tên sản phẩm không được để trống');
  }

  if (!row.brandName || !row.brandName.trim()) {
    errors.push('Thương hiệu không được để trống');
  } else if (brands.length > 0) {
    // Check if brand exists
    const brandExists = brands.some(b => 
      b.name.toLowerCase() === row.brandName.toLowerCase() ||
      b.slug.toLowerCase() === row.brandName.toLowerCase()
    );
    if (!brandExists) {
      errors.push(`Thương hiệu "${row.brandName}" không tồn tại`);
    }
  }

  if (!row.sku || !row.sku.trim()) {
    errors.push('SKU không được để trống');
  }

  // Validate price
  if (!row.priceBase || row.priceBase <= 0) {
    errors.push('Giá gốc phải lớn hơn 0');
  }

  if (row.priceSale !== null && row.priceSale !== undefined) {
    if (row.priceSale < 0) {
      errors.push('Giá sale không được âm');
    }
    if (row.priceSale > row.priceBase) {
      errors.push('Giá sale không được lớn hơn giá gốc');
    }
  }

  // Validate stock
  if (row.stockQuantity === null || row.stockQuantity === undefined) {
    errors.push('Tồn kho không được để trống');
  } else if (row.stockQuantity < 0) {
    errors.push('Tồn kho không được âm');
  }

  // Validate size
  if (!row.size || !row.size.trim()) {
    errors.push('Size không được để trống');
  } else {
    const validSizes = ['35', '36', '37', '38', '39', '40', '41', '42', '43', '44', '45', '46'];
    if (!validSizes.includes(row.size.toString())) {
      errors.push(`Size "${row.size}" không hợp lệ. Size hợp lệ: ${validSizes.join(', ')}`);
    }
  }

  // Validate color
  if (!row.color || !row.color.trim()) {
    errors.push('Màu sắc không được để trống');
  }

  // Validate categories if provided
  if (row.categories && categories.length > 0) {
    const categoryNames = row.categories.split(',').map(c => c.trim());
    const invalidCategories = categoryNames.filter(catName => {
      return !categories.some(c => 
        c.name.toLowerCase() === catName.toLowerCase() ||
        c.slug.toLowerCase() === catName.toLowerCase()
      );
    });
    if (invalidCategories.length > 0) {
      errors.push(`Danh mục không tồn tại: ${invalidCategories.join(', ')}`);
    }
  }

  // Validate image URL format
  if (row.imageUrl && row.imageUrl.trim()) {
    try {
      new URL(row.imageUrl);
    } catch {
      // Not a valid URL, but might be a relative path - that's OK
      if (!row.imageUrl.startsWith('/') && !row.imageUrl.startsWith('./')) {
        errors.push('URL ảnh không hợp lệ');
      }
    }
  }

  return {
    isValid: errors.length === 0,
    errors,
    rowIndex
  };
}

/**
 * Validate all import rows
 * @param {Array} rows - Array of product data rows
 * @param {Object} options - Validation options
 * @returns {Object} Validation result { validRows: Array, invalidRows: Array, summary: Object }
 */
export function validateImportData(rows, options = {}) {
  const validRows = [];
  const invalidRows = [];

  rows.forEach((row, index) => {
    const validation = validateProductRow(row, index + 2, options); // +2 because Excel rows start at 1 and header is row 1
    if (validation.isValid) {
      validRows.push(row);
    } else {
      invalidRows.push({
        ...row,
        errors: validation.errors,
        rowNumber: validation.rowIndex
      });
    }
  });

  return {
    validRows,
    invalidRows,
    summary: {
      total: rows.length,
      valid: validRows.length,
      invalid: invalidRows.length,
      validPercentage: rows.length > 0 ? Math.round((validRows.length / rows.length) * 100) : 0
    }
  };
}

/**
 * Parse CSV file
 * @param {File} file - CSV file
 * @returns {Promise<Array>} Parsed data array
 */
export function parseCSV(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const text = e.target.result;
        const lines = text.split('\n').filter(line => line.trim());
        
        if (lines.length < 2) {
          reject(new Error('CSV file must have at least a header and one data row'));
          return;
        }

        // Parse header
        const headers = lines[0].split(',').map(h => h.trim().replace(/^"|"$/g, ''));
        
        // Parse data rows
        const data = [];
        for (let i = 1; i < lines.length; i++) {
          const values = lines[i].split(',').map(v => v.trim().replace(/^"|"$/g, ''));
          if (values.length !== headers.length) {
            console.warn(`Row ${i + 1} has ${values.length} columns, expected ${headers.length}`);
            continue;
          }
          
          const row = {};
          headers.forEach((header, index) => {
            row[header] = values[index] || '';
          });
          data.push(row);
        }
        
        resolve(data);
      } catch (error) {
        reject(error);
      }
    };
    reader.onerror = () => reject(new Error('Failed to read CSV file'));
    reader.readAsText(file);
  });
}

/**
 * Map CSV/Excel column names to internal format
 * @param {Object} row - Raw row data
 * @returns {Object} Mapped product data
 */
export function mapImportRow(row) {
  return {
    rowNumber: row.rowNumber || null,
    productName: row["Tên sản phẩm"] || row["Product Name"] || row["productName"] || "",
    productSlug: row["Slug"] || row["slug"] || "",
    brandName: row["Thương hiệu"] || row["Brand"] || row["brandName"] || "",
    description: row["Mô tả"] || row["Description"] || row["description"] || "",
    categories: row["Danh mục"] || row["Categories"] || row["categories"] || "",
    isActive: row["Trạng thái"] === "TRUE" || row["Status"] === "TRUE" || row["isActive"] === true || row["isActive"] === "true",
    sku: row["SKU"] || row["sku"] || "",
    size: row["Size"] || row["size"] || "",
    color: row["Màu sắc"] || row["Color"] || row["color"] || "",
    priceBase: Number(row["Giá gốc"] || row["Base Price"] || row["priceBase"] || 0),
    priceSale: row["Giá sale"] || row["Sale Price"] || row["priceSale"] ? Number(row["Giá sale"] || row["Sale Price"] || row["priceSale"]) : null,
    stockQuantity: Number(row["Tồn kho"] || row["Stock"] || row["stockQuantity"] || 0),
    imageUrl: row["URL ảnh"] || row["Image URL"] || row["imageUrl"] || "",
  };
}

