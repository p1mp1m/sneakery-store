/**
 * ===============================================
 * 🔹 SKU GENERATOR UTILS
 * Chuẩn hóa mã SKU tự động cho sản phẩm & biến thể
 * Logic gốc giữ nguyên từ VariantModal.vue của bạn
 * ===============================================
 */

/**
 * Chuẩn hóa chuỗi:
 * - Loại bỏ dấu tiếng Việt (normalize)
 * - Loại bỏ ký tự đặc biệt, giữ lại chữ, số, dấu gạch
 * - Loại bỏ khoảng trắng dư thừa
 */
export const normalize = (s) =>
  (s || "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^A-Za-z0-9\- ]/g, " ")
    .replace(/\s+/g, " ")
    .trim();

/**
 * BRAND CODE
 * - Lấy từ đầu tiên của phần trước dấu “-”
 * - Giữ tối đa 5 ký tự
 * → "Adidas Ultraboost 22 - Adidas" → "ADIDA"
 */
export const extractBrandCode = (fullName) => {
  const firstPart = normalize(fullName).split("-")[0] || "";
  const brandWord = firstPart.split(/\s+/)[0] || "";
  return brandWord
    .replace(/[^A-Za-z]/g, "")
    .substring(0, 5)
    .toUpperCase();
};

/**
 * Lấy phần tên chính
 * - Nếu có dạng “Tên - Brand”, chỉ lấy phần đầu
 * → "Adidas Ultraboost 22 - Adidas" → "Adidas Ultraboost 22"
 */
export const extractMainName = (fullName) => {
  const parts = fullName.split("-");
  return normalize(parts.length >= 1 ? parts[0] : fullName);
};

/**
 * MODEL CODE
 * - 5 ký tự đầu của từ model chính (từ thứ 2)
 * - + token đầu tiên chứa số (ví dụ “55”, “36DX”, “270”)
 * - Nếu không có số → nối thêm 1 từ tiếp theo
 * → "Nike React Element 55" → "REACT55"
 * → "Adidas Ultraboost 22" → "ULTRA22"
 */
export const extractModelCode = (fullName) => {
  const main = extractMainName(fullName);
  const tokens = main.split(/\s+/).filter(Boolean);
  if (tokens.length < 2) {
    return main
      .replace(/[^A-Za-z0-9]/g, "")
      .substring(0, 8)
      .toUpperCase();
  }

  const lettersOnly = (t) => t.replace(/[^A-Za-z]/g, "");
  const cleanAlnum = (t) => t.replace(/[^A-Za-z0-9]/g, "");

  const base = lettersOnly(tokens[1]).substring(0, 5).toUpperCase();
  const numTok = tokens.slice(2).find((t) => /\d/.test(t));

  if (numTok) {
    const suffix = cleanAlnum(numTok).substring(0, 4).toUpperCase();
    return `${base}${suffix}`;
  }

  const next = tokens[2]
    ? lettersOnly(tokens[2]).substring(0, 4).toUpperCase()
    : "";
  return `${base}${next}`;
};

/**
 * COLOR CODE
 * - 3 ký tự đầu (chữ hoặc số)
 * → “Black” → “BLA”
 * → “Đỏ” → “DO”
 */
export const shortenColor = (color) =>
  (color || "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^A-Za-z0-9]/g, "")
    .substring(0, 3)
    .toUpperCase();

/**
 * HÀM CHÍNH: sinh SKU hoàn chỉnh
 * → "ADIDA-ULTRA22-WHI-42"
 */
export const generateSku = (productName, color, size) => {
  if (!productName || !color || !size) return "";
  const brandPart = extractBrandCode(productName);
  const modelPart = extractModelCode(productName);
  const colorPart = shortenColor(color);
  const sizePart = String(size).trim();
  return `${brandPart}-${modelPart}-${colorPart}-${sizePart}`;
};
