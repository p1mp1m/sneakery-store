/**
 * ===============================================
 * 🔹 SKU GENERATOR UTILS
 * Chuẩn hóa mã SKU tự động cho sản phẩm & biến thể
 * Format mới: [Brand]-[Category]-[Model]-[Color]-[Size]
 * Ví dụ: SNK-SHO-AZ-BK-42
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
 * - Lấy từ brandName nếu có, nếu không thì extract từ product name
 * - Rút gọn thành 3-4 ký tự, viết hoa
 * → "Nike" → "NIK"
 * → "Adidas" → "ADI"
 * → "Sneakery" → "SNK"
 * → "Converse" → "CON"
 */
export const extractBrandCode = (brandName, productName) => {
  // Mapping các brand phổ biến
  const brandMap = {
    "nike": "NIK",
    "adidas": "ADI",
    "sneakery": "SNK",
    "converse": "CON",
    "vans": "VAN",
    "jordan": "JOR",
    "puma": "PUM",
    "reebok": "REB",
    "new balance": "NB",
    "asics": "ASC",
  };

  let brand = "";

  // Ưu tiên dùng brandName nếu có
  if (brandName) {
    brand = normalize(brandName).toLowerCase();
    if (brandMap[brand]) {
      return brandMap[brand];
    }
    // Nếu không có trong map, rút gọn thông minh
    return normalize(brandName)
      .replace(/[^A-Za-z0-9]/g, "")
      .substring(0, 3)
      .toUpperCase();
  }

  // Fallback: extract từ product name
  if (productName) {
    const firstPart = normalize(productName).split("-")[0] || "";
    const brandWord = firstPart.split(/\s+/)[0] || "";
    brand = brandWord.toLowerCase();
    if (brandMap[brand]) {
      return brandMap[brand];
    }
    return brandWord
      .replace(/[^A-Za-z]/g, "")
      .substring(0, 3)
      .toUpperCase();
  }

  return "UNK"; // Unknown
};

/**
 * CATEGORY CODE
 * - Lấy từ category name (ưu tiên category đầu tiên nếu có nhiều)
 * - Rút gọn thành 3 ký tự viết hoa
 * → "Shoes" → "SHO"
 * → "Giày thể thao" → "GTT"
 * → "Sneakers" → "SNK"
 * → "Running" → "RUN"
 */
export const extractCategoryCode = (categories) => {
  if (!categories || !Array.isArray(categories) || categories.length === 0) {
    return "GEN"; // General (mặc định nếu không có category)
  }

  // Mapping các category phổ biến
  // Ưu tiên tiếng Anh, fallback sang tiếng Việt
  const categoryMap = {
    "shoes": "SHO",        // Ưu tiên tiếng Anh
    "giay": "SHO",         // "Giày" → "SHO" (thống nhất dùng tiếng Anh)
    "sneakers": "SNK",
    "running": "RUN",
    "basketball": "BAS",
    "football": "FOT",
    "tennis": "TEN",
    "casual": "CAS",
    "sport": "SPO",
    "the thao": "SPO",     // "Thể thao" → "SPO"
    "giay the thao": "SNK", // "Giày thể thao" → "SNK" (sneakers)
    "giay chay bo": "RUN",  // "Giày chạy bộ" → "RUN"
    "giay bong ro": "BAS",  // "Giày bóng rổ" → "BAS"
    "giay da bong": "FOT",  // "Giày đá bóng" → "FOT"
  };

  // Lấy category đầu tiên
  const categoryName = categories[0]?.name || categories[0];
  if (!categoryName) {
    return "GEN";
  }

  const normalized = normalize(categoryName).toLowerCase();

  // Kiểm tra trong map trước
  if (categoryMap[normalized]) {
    return categoryMap[normalized];
  }

  // Kiểm tra partial match
  for (const [key, value] of Object.entries(categoryMap)) {
    if (normalized.includes(key) || key.includes(normalized)) {
      return value;
    }
  }

  const words = normalized.split(/\s+/).filter(Boolean);

  // Nếu chỉ có 1 từ → lấy 3 ký tự đầu
  if (words.length === 1) {
    return words[0]
      .replace(/[^A-Za-z0-9]/g, "")
      .substring(0, 3)
      .toUpperCase()
      .padEnd(3, "X");
  }

  // Nếu có nhiều từ → lấy chữ cái đầu của mỗi từ (tối đa 3 từ)
  const initials = words
    .slice(0, 3)
    .map((word) => word.charAt(0).toUpperCase())
    .join("");

  return initials.length >= 3 ? initials.substring(0, 3) : initials.padEnd(3, "X");
};

/**
 * Lấy phần tên chính
 * - Nếu có dạng "Tên - Brand", chỉ lấy phần đầu
 * → "Adidas Ultraboost 22 - Adidas" → "Adidas Ultraboost 22"
 */
export const extractMainName = (fullName) => {
  const parts = fullName.split("-");
  return normalize(parts.length >= 1 ? parts[0] : fullName);
};

/**
 * MODEL CODE
 * - Lấy từ product name, bỏ qua brand name
 * - Ưu tiên lấy token chứa số (ví dụ "55", "22", "36DX")
 * - Nếu không có số → lấy 2 từ đầu tiên (sau khi loại brand)
 * - Giới hạn tối đa 6-8 ký tự
 * → "Nike React Element 55" → "REACT55"
 * → "Adidas Ultraboost 22" → "ULTRA22"
 * → "Sneakery Air Zoom" → "AIRZOOM"
 * → "Air Zoom" → "AIRZOM"
 */
export const extractModelCode = (productName, brandName) => {
  if (!productName) return "MODEL";

  const main = extractMainName(productName);
  const tokens = main.split(/\s+/).filter(Boolean);

  // Loại bỏ brand name nếu có
  const brandNormalized = brandName ? normalize(brandName).toLowerCase() : "";
  const filteredTokens = brandNormalized
    ? tokens.filter(token => {
      const tokenNorm = normalize(token).toLowerCase();
      return !brandNormalized.includes(tokenNorm) && !tokenNorm.includes(brandNormalized);
    })
    : tokens;

  if (filteredTokens.length === 0) {
    // Nếu không còn token nào, lấy toàn bộ và rút gọn
    return main
      .replace(/[^A-Za-z0-9]/g, "")
      .substring(0, 6)
      .toUpperCase();
  }

  const lettersOnly = (t) => t.replace(/[^A-Za-z]/g, "");
  const cleanAlnum = (t) => t.replace(/[^A-Za-z0-9]/g, "");

  // Tìm token chứa số
  const numTok = filteredTokens.find((t) => /\d/.test(t));

  if (numTok) {
    // Có số: lấy từ trước số + số (tối đa 6 ký tự)
    const numIndex = filteredTokens.indexOf(numTok);
    const beforeNum = numIndex > 0 ? filteredTokens[numIndex - 1] : filteredTokens[0];
    const base = lettersOnly(beforeNum || "").substring(0, 4).toUpperCase();
    const suffix = cleanAlnum(numTok).substring(0, 2).toUpperCase();
    return `${base}${suffix}`.substring(0, 6);
  }

  // Không có số: 
  // - Nếu có 2 từ ngắn (≤ 4 ký tự mỗi từ) → lấy chữ cái đầu: "Air Zoom" → "AZ"
  // - Nếu có từ dài → lấy 4 ký tự đầu của mỗi từ: "Ultraboost" → "ULTR"
  if (filteredTokens.length >= 2) {
    const first = filteredTokens[0];
    const second = filteredTokens[1];

    // Nếu cả 2 từ đều ngắn (≤ 4 ký tự), lấy chữ cái đầu
    if (first.length <= 4 && second.length <= 4) {
      return `${first.charAt(0)}${second.charAt(0)}`.toUpperCase();
    }
  }

  // Nếu không phải 2 từ ngắn, lấy 2-4 ký tự đầu của mỗi từ
  const base = filteredTokens[0]
    ? lettersOnly(filteredTokens[0]).substring(0, 4).toUpperCase()
    : "";
  const next = filteredTokens[1]
    ? lettersOnly(filteredTokens[1]).substring(0, 4).toUpperCase()
    : "";

  const result = `${base}${next}`.substring(0, 6);
  return result || "MODEL";
};

/**
 * COLOR CODE
 * - Ưu tiên mapping màu tiếng Việt phổ biến để không bị mất ký tự (Đ/đ)
 * - Fallback: chuẩn hoá + lấy 3 ký tự đầu
 *
 * Ví dụ:
 * - "Đen" -> "DEN"
 * - "Đỏ"  -> "DO"
 * - "Xanh dương" -> "XDU" (hoặc mapping "XDU"/"XAN" tuỳ bạn)
 */
export const shortenColor = (color) => {
  const raw = String(color ?? "").trim();
  if (!raw) return "";

  // Chuẩn hoá nhưng phải xử lý Đ/đ trước để không bị rơi mất
  const normalizeVi = (s) =>
    (s || "")
      .trim()
      // Quan trọng: Đ/đ không bị NFD tách dấu, nên phải thay trước
      .replace(/Đ/g, "D")
      .replace(/đ/g, "d")
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "") // bỏ dấu
      .replace(/\s+/g, " ")
      .trim();

  const norm = normalizeVi(raw).toLowerCase();

  // Mapping màu tiếng Việt/tiếng Anh phổ biến
  // Bạn có thể mở rộng thêm theo hệ thống của bạn
  const viMap = {
    // tiếng Việt
    "den": "DEN",
    "do": "DO",
    "trang": "TRANG",
    "vang": "VANG",
    "xanh": "XAN",
    "xanh duong": "XDU",
    "xanh la": "XLA",
    "xanh lam": "XLA",
    "hong": "HON",
    "tim": "TIM",
    "cam": "CAM",
    "nau": "NAU",
    "xam": "XAM",
    "ghi": "GHI",
    "bac": "BAC",
    "vang dong": "VDO", // tuỳ quy ước
    "be": "BE",
    "kem": "KEM",

    // tiếng Anh (giữ ổn định 3 ký tự)
    "black": "BLK",
    "white": "WHI",
    "red": "RED",
    "blue": "BLU",
    "green": "GRE",
    "yellow": "YEL",
    "purple": "PUR",
    "pink": "PIN",
    "orange": "ORA",
    "brown": "BRO",
    "gray": "GRA",
    "grey": "GRA",
    "gold": "GOL",
    "silver": "SIL",
  };

  // 1) Match exact
  if (viMap[norm]) return viMap[norm];

  // 2) Partial match (ví dụ: "Đen tuyền", "Đỏ đô", "Xanh dương nhạt")
  //    Ưu tiên key dài trước để tránh "xanh" ăn trước "xanh duong"
  const keys = Object.keys(viMap).sort((a, b) => b.length - a.length);
  for (const k of keys) {
    if (norm.includes(k)) return viMap[k];
  }

  // 3) Fallback: lấy 3 ký tự đầu của chuỗi đã chuẩn hoá (không mất Đ)
  const fallback = normalizeVi(raw)
    .replace(/[^A-Za-z0-9]/g, "")
    .toUpperCase();

  // Nếu fallback quá ngắn (vd "Đỏ" -> "DO" OK), thì trả nguyên
  if (fallback.length <= 3) return fallback;

  return fallback.substring(0, 3);
};

/**
 * HÀM CHÍNH: sinh SKU hoàn chỉnh
 * Format: [Brand]-[Category]-[Model]-[Color]-[Size]
 * 
 * @param {Object} product - Product object với các fields: name, brandName, categories
 * @param {String} color - Màu sắc
 * @param {String|Number} size - Kích thước
 * @returns {String} SKU hoàn chỉnh
 * 
 * Ví dụ:
 * - generateSku({ name: "Sneakery Air Zoom", brandName: "Sneakery", categories: [{ name: "Shoes" }] }, "Black", 42)
 *   → "SNK-SHO-AZ-BLK-42"
 */
export const generateSku = (product, color, size) => {
  if (!product || !color || size === null || size === undefined) {
    return "";
  }

  const productName = product.name || "";
  const brandName = product.brandName || "";
  const categories = product.categories || [];

  const brandPart = extractBrandCode(brandName, productName);
  const categoryPart = extractCategoryCode(categories);
  const modelPart = extractModelCode(productName, brandName);
  const colorPart = shortenColor(color);
  const sizePart = String(size).trim().replace(/\s+/g, "");

  return `${brandPart}-${categoryPart}-${modelPart}-${colorPart}-${sizePart}`;
};
