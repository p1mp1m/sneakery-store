<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Enhanced Page Header with Glassmorphism -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">settings</i>
            Cài đặt hệ thống
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Quản lý cấu hình cửa hàng và hệ thống</p>
        </div>
        
        <div class="flex flex-wrap items-center gap-2">
          <button @click="showSearch = !showSearch" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">search</i>
            Tìm kiếm
          </button>
          <button @click="exportSettings" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">file_download</i>
            Xuất cấu hình
          </button>
          <button @click="importSettings" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">file_upload</i>
            Nhập cấu hình
          </button>
          <button @click="resetToDefaults" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-red-500 to-red-600 text-white rounded-lg hover:from-red-600 hover:to-red-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">restore</i>
            Khôi phục mặc định
          </button>
        </div>
      </div>

      <!-- Search Bar -->
      <transition
        enter-active-class="transition-all duration-300 ease-out"
        enter-from-class="opacity-0 transform -translate-y-2"
        enter-to-class="opacity-100 transform translate-y-0"
        leave-active-class="transition-all duration-200 ease-in"
        leave-from-class="opacity-100 transform translate-y-0"
        leave-to-class="opacity-0 transform -translate-y-2"
      >
        <div v-if="showSearch" class="mt-4">
          <div class="relative">
            <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg">search</i>
            <input 
              v-model="searchQuery" 
              type="text" 
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="Tìm kiếm cài đặt..."
              @input="handleSearch"
            />
            <button v-if="searchQuery" @click="clearSearch" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>
          
          <!-- Search Results -->
          <div v-if="searchResults.length > 0 && searchQuery" class="mt-2 bg-white dark:bg-gray-700 rounded-lg border border-gray-200 dark:border-gray-600 shadow-lg max-h-64 overflow-y-auto">
            <div 
              v-for="result in searchResults" 
              :key="result.id"
              @click="goToSetting(result)"
              class="flex items-center gap-3 p-3 hover:bg-gray-50 dark:hover:bg-gray-600 cursor-pointer transition-colors border-b border-gray-200 dark:border-gray-600 last:border-b-0"
            >
              <i class="material-icons text-gray-500 dark:text-gray-400">{{ result.icon }}</i>
              <div class="flex-1">
                <strong class="text-sm font-medium text-gray-900 dark:text-gray-100 block">{{ result.title }}</strong>
                <p class="text-xs text-gray-500 dark:text-gray-400 mt-0.5">{{ result.description }}</p>
              </div>
            </div>
          </div>
          
          <div v-else-if="searchQuery && searchResults.length === 0" class="mt-2 flex flex-col items-center justify-center p-8 bg-gray-50 dark:bg-gray-900/50 rounded-lg border border-gray-200 dark:border-gray-700">
            <i class="material-icons text-gray-400 dark:text-gray-500 text-3xl mb-2">search_off</i>
            <p class="text-sm text-gray-500 dark:text-gray-400">Không tìm thấy cài đặt nào</p>
          </div>
        </div>
      </transition>
    </div>

    <!-- Enhanced Settings Navigation Tabs -->
    <div class="flex flex-wrap gap-2 p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        @click="activeTab = tab.id"
        class="flex items-center gap-2 px-4 py-2 rounded-lg transition-all duration-200 text-sm font-medium"
        :class="activeTab === tab.id 
          ? 'bg-gradient-to-r from-purple-500 to-purple-600 text-white shadow-sm' 
          : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'"
      >
        <i class="material-icons text-base">{{ tab.icon }}</i>
        <span>{{ tab.label }}</span>
        <span v-if="tab.badge" class="px-2 py-0.5 text-xs rounded-full bg-white/20 dark:bg-white/10">{{ tab.badge }}</span>
      </button>
    </div>

    <!-- Store Settings -->
    <div v-if="activeTab === 'store'" class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="mb-6">
        <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 mb-1">Thông tin cửa hàng</h2>
        <p class="text-sm text-gray-500 dark:text-gray-400">Cấu hình thông tin cơ bản của cửa hàng</p>
      </div>

      <div class="space-y-4">
        <div class="flex flex-col gap-2">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Tên cửa hàng</label>
          <input
            v-model="storeSettings.name"
            type="text"
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            placeholder="Sneakery Store"
          />
        </div>

        <div class="flex flex-col gap-2">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Slogan</label>
          <input
            v-model="storeSettings.slogan"
            type="text"
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            placeholder="Your Perfect Sneakers Destination"
          />
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Email liên hệ</label>
            <input
              v-model="storeSettings.email"
              type="email"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="contact@sneakerystore.com"
            />
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số điện thoại</label>
            <input
              v-model="storeSettings.phone"
              type="tel"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="(+84) 123-456-789"
            />
          </div>
        </div>

        <div class="flex flex-col gap-2">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Địa chỉ</label>
          <textarea
            v-model="storeSettings.address"
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            rows="3"
            placeholder="Nhập địa chỉ cửa hàng..."
          ></textarea>
        </div>

        <div class="flex items-center justify-end pt-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="saveStoreSettings" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">save</i>
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>

    <!-- General Settings -->
    <div v-if="activeTab === 'general'" class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="mb-6">
        <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 mb-1">Cài đặt chung</h2>
        <p class="text-sm text-gray-500 dark:text-gray-400">Cấu hình chung cho hệ thống</p>
      </div>

      <div class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Đơn vị tiền tệ</label>
            <select 
              v-model="generalSettings.currency" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            >
              <option value="VND">VND - Việt Nam Đồng</option>
              <option value="USD">USD - US Dollar</option>
              <option value="EUR">EUR - Euro</option>
            </select>
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Múi giờ</label>
            <select 
              v-model="generalSettings.timezone" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            >
              <option value="Asia/Ho_Chi_Minh">Việt Nam (UTC+7)</option>
              <option value="America/New_York">New York (UTC-5)</option>
              <option value="Europe/London">London (UTC+0)</option>
            </select>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Ngôn ngữ mặc định</label>
            <select 
              v-model="generalSettings.language" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            >
              <option value="vi">Tiếng Việt</option>
              <option value="en">English</option>
            </select>
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số sản phẩm mỗi trang</label>
            <input
              v-model.number="generalSettings.productsPerPage"
              type="number"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              min="4"
              max="24"
            />
          </div>
        </div>

        <div class="flex items-center gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
          <input
            v-model="generalSettings.maintenanceMode"
            type="checkbox"
            id="maintenance"
            class="w-5 h-5 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
          />
          <label for="maintenance" class="text-sm text-gray-700 dark:text-gray-300 cursor-pointer">
            Bật chế độ bảo trì (Khách hàng sẽ không truy cập được website)
          </label>
        </div>

        <div class="flex items-center justify-end pt-4 border-t border-gray-200 dark:border-gray-700">
          <button 
            @click="saveGeneralSettings" 
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
          >
            <i class="material-icons text-base">save</i>
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>

    <!-- Email Settings -->
    <div v-if="activeTab === 'email'" class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="mb-6">
        <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 mb-1">Cài đặt Email</h2>
        <p class="text-sm text-gray-500 dark:text-gray-400">Cấu hình SMTP để gửi email tự động</p>
      </div>

      <div class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">SMTP Host</label>
            <input
              v-model="emailSettings.smtpHost"
              type="text"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="smtp.gmail.com"
            />
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">SMTP Port</label>
            <input
              v-model.number="emailSettings.smtpPort"
              type="number"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="587"
            />
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Email gửi đi</label>
            <input
              v-model="emailSettings.fromEmail"
              type="email"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="noreply@sneakerystore.com"
            />
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Tên hiển thị</label>
            <input
              v-model="emailSettings.fromName"
              type="text"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="Sneakery Store"
            />
          </div>
        </div>

        <div class="flex items-center gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
          <input
            v-model="emailSettings.enableNotifications"
            type="checkbox"
            id="email-notifications"
            class="w-5 h-5 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
          />
          <label for="email-notifications" class="text-sm text-gray-700 dark:text-gray-300 cursor-pointer">
            Gửi email thông báo cho admin khi có đơn hàng mới
          </label>
        </div>

        <div class="flex items-center justify-end gap-2 pt-4 border-t border-gray-200 dark:border-gray-700">
          <button 
            @click="saveEmailSettings" 
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
          >
            <i class="material-icons text-base">save</i>
            Lưu thay đổi
          </button>
          <button 
            @click="testEmail" 
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">email</i>
            Gửi email test
          </button>
        </div>
      </div>
    </div>

    <!-- Payment Settings -->
    <div v-if="activeTab === 'payment'" class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="mb-6">
        <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 mb-1">Cài đặt Thanh toán</h2>
        <p class="text-sm text-gray-500 dark:text-gray-400">Quản lý các phương thức thanh toán</p>
      </div>

      <div class="space-y-4">
        <!-- COD Payment -->
        <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg border border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h3 class="text-base font-semibold text-gray-900 dark:text-gray-100 mb-1">Thanh toán khi nhận hàng (COD)</h3>
              <p class="text-sm text-gray-500 dark:text-gray-400">Khách hàng thanh toán tiền mặt khi nhận hàng</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input 
                v-model="paymentSettings.cod.enabled" 
                type="checkbox" 
                class="sr-only peer"
              />
              <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-purple-300 dark:peer-focus:ring-purple-800 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-purple-600"></div>
            </label>
          </div>
        </div>

        <!-- Bank Transfer Payment -->
        <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg border border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h3 class="text-base font-semibold text-gray-900 dark:text-gray-100 mb-1">Chuyển khoản ngân hàng</h3>
              <p class="text-sm text-gray-500 dark:text-gray-400">Khách hàng chuyển khoản trước khi nhận hàng</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input 
                v-model="paymentSettings.bankTransfer.enabled" 
                type="checkbox" 
                class="sr-only peer"
              />
              <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-purple-300 dark:peer-focus:ring-purple-800 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-purple-600"></div>
            </label>
          </div>

          <div v-if="paymentSettings.bankTransfer.enabled" class="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700 space-y-4">
            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Tên ngân hàng</label>
              <input
                v-model="paymentSettings.bankTransfer.bankName"
                type="text"
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Vietcombank"
              />
            </div>
            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số tài khoản</label>
              <input
                v-model="paymentSettings.bankTransfer.accountNumber"
                type="text"
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="1234567890"
              />
            </div>
            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Chủ tài khoản</label>
              <input
                v-model="paymentSettings.bankTransfer.accountName"
                type="text"
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="SNEAKERY STORE CO., LTD"
              />
            </div>
          </div>
        </div>

        <!-- Online Payment -->
        <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg border border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h3 class="text-base font-semibold text-gray-900 dark:text-gray-100 mb-1">Thanh toán online (VNPay, MoMo...)</h3>
              <p class="text-sm text-gray-500 dark:text-gray-400">Tích hợp cổng thanh toán trực tuyến</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input 
                v-model="paymentSettings.online.enabled" 
                type="checkbox" 
                class="sr-only peer"
              />
              <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-purple-300 dark:peer-focus:ring-purple-800 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-purple-600"></div>
            </label>
          </div>

          <div v-if="paymentSettings.online.enabled" class="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
            <div class="flex items-center gap-2 p-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
              <i class="material-icons text-blue-600 dark:text-blue-400 text-base">info</i>
              <p class="text-sm text-blue-700 dark:text-blue-300">
                Tính năng này sẽ được phát triển trong phiên bản tiếp theo
              </p>
            </div>
          </div>
        </div>

        <div class="flex items-center justify-end pt-4 border-t border-gray-200 dark:border-gray-700">
          <button 
            @click="savePaymentSettings" 
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
          >
            <i class="material-icons text-base">save</i>
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>

    <!-- Theme Settings -->
    <div v-if="activeTab === 'theme'" class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="mb-6">
        <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 mb-1">Cài đặt Giao diện</h2>
        <p class="text-sm text-gray-500 dark:text-gray-400">Chọn chế độ giao diện sáng hoặc tối cho hệ thống</p>
      </div>

      <div class="space-y-6">
        <div class="p-6 bg-gray-50 dark:bg-gray-900/50 rounded-lg border border-gray-200 dark:border-gray-700">
          <div 
            class="p-6 rounded-lg border-2 transition-all"
            :class="isDark 
              ? 'bg-gray-800 border-gray-700' 
              : 'bg-white border-gray-200'"
          >
            <div class="flex items-center gap-2 mb-4">
              <div class="flex gap-1.5">
                <div class="w-3 h-3 rounded-full bg-red-500"></div>
                <div class="w-3 h-3 rounded-full bg-yellow-500"></div>
                <div class="w-3 h-3 rounded-full bg-green-500"></div>
              </div>
            </div>
            <div class="space-y-2">
              <div 
                class="text-base font-semibold"
                :class="isDark ? 'text-white' : 'text-gray-900'"
              >
                Xem trước
              </div>
              <div 
                class="text-sm"
                :class="isDark ? 'text-gray-400' : 'text-gray-600'"
              >
                Chế độ hiện tại: {{ isDark ? 'Tối' : 'Sáng' }}
              </div>
            </div>
          </div>
        </div>

        <div class="space-y-3">
          <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-3">Chế độ giao diện</label>
          <div class="space-y-2">
            <label 
              class="flex items-center gap-4 p-4 border-2 rounded-lg cursor-pointer transition-all"
              :class="theme === 'light' 
                ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20' 
                : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600'"
            >
              <input
                type="radio"
                value="light"
                :checked="theme === 'light'"
                @change="handleThemeChange('light')"
                name="theme"
                class="w-5 h-5 text-purple-600 border-gray-300 focus:ring-purple-500"
              />
              <div class="flex items-center gap-3 flex-1">
                <i class="material-icons text-gray-600 dark:text-gray-400">light_mode</i>
                <div>
                  <div class="text-sm font-medium text-gray-900 dark:text-gray-100">Sáng</div>
                  <div class="text-xs text-gray-500 dark:text-gray-400">Giao diện sáng với màu nền trắng</div>
                </div>
              </div>
            </label>

            <label 
              class="flex items-center gap-4 p-4 border-2 rounded-lg cursor-pointer transition-all"
              :class="theme === 'dark' 
                ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20' 
                : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600'"
            >
              <input
                type="radio"
                value="dark"
                :checked="theme === 'dark'"
                @change="handleThemeChange('dark')"
                name="theme"
                class="w-5 h-5 text-purple-600 border-gray-300 focus:ring-purple-500"
              />
              <div class="flex items-center gap-3 flex-1">
                <i class="material-icons text-gray-600 dark:text-gray-400">dark_mode</i>
                <div>
                  <div class="text-sm font-medium text-gray-900 dark:text-gray-100">Tối</div>
                  <div class="text-xs text-gray-500 dark:text-gray-400">Giao diện tối với màu nền đen</div>
                </div>
              </div>
            </label>

            <label 
              class="flex items-center gap-4 p-4 border-2 rounded-lg cursor-pointer transition-all"
              :class="theme === 'system' 
                ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20' 
                : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600'"
            >
              <input
                type="radio"
                value="system"
                :checked="theme === 'system'"
                @change="handleThemeChange('system')"
                name="theme"
                class="w-5 h-5 text-purple-600 border-gray-300 focus:ring-purple-500"
              />
              <div class="flex items-center gap-3 flex-1">
                <i class="material-icons text-gray-600 dark:text-gray-400">brightness_auto</i>
                <div>
                  <div class="text-sm font-medium text-gray-900 dark:text-gray-100">Theo hệ thống</div>
                  <div class="text-xs text-gray-500 dark:text-gray-400">Tự động theo cài đặt hệ thống</div>
                </div>
              </div>
            </label>
          </div>
        </div>

        <div class="flex items-center justify-end pt-4 border-t border-gray-200 dark:border-gray-700">
          <button 
            @click="saveThemeSettings" 
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
          >
            <i class="material-icons text-base">save</i>
            Lưu cài đặt
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { useTheme } from '@/composables/useTheme'
import notificationService from '@/utils/notificationService'
import confirmDialogService from '@/utils/confirmDialogService'
import { downloadJson } from '@/utils/exportHelpers'
import logger from '@/utils/logger'

const adminStore = useAdminStore()
const { theme, setTheme, currentTheme, isDark } = useTheme()

const activeTab = ref('store')
const showSearch = ref(false)
const searchQuery = ref('')
const searchResults = ref([])

const tabs = [
  { id: 'store', label: 'Cửa hàng', icon: 'store' },
  { id: 'general', label: 'Chung', icon: 'settings' },
  { id: 'email', label: 'Email', icon: 'email' },
  { id: 'payment', label: 'Thanh toán', icon: 'payment' },
  { id: 'theme', label: 'Giao diện', icon: 'palette' }
]

// Searchable settings index
const settingsIndex = [
  { id: 'store-name', title: 'Tên cửa hàng', description: 'Thông tin cửa hàng', tab: 'store', icon: 'store' },
  { id: 'store-email', title: 'Email liên hệ', description: 'Thông tin cửa hàng', tab: 'store', icon: 'email' },
  { id: 'store-phone', title: 'Số điện thoại', description: 'Thông tin cửa hàng', tab: 'store', icon: 'phone' },
  { id: 'general-currency', title: 'Đơn vị tiền tệ', description: 'Cài đặt chung', tab: 'general', icon: 'attach_money' },
  { id: 'general-language', title: 'Ngôn ngữ', description: 'Cài đặt chung', tab: 'general', icon: 'language' },
  { id: 'general-maintenance', title: 'Chế độ bảo trì', description: 'Cài đặt chung', tab: 'general', icon: 'build' },
  { id: 'email-smtp', title: 'SMTP Host', description: 'Cài đặt Email', tab: 'email', icon: 'mail_outline' },
  { id: 'email-notifications', title: 'Thông báo Email', description: 'Cài đặt Email', tab: 'email', icon: 'notifications' },
  { id: 'payment-cod', title: 'Thanh toán khi nhận hàng', description: 'Cài đặt Thanh toán', tab: 'payment', icon: 'payments' },
  { id: 'payment-bank', title: 'Chuyển khoản ngân hàng', description: 'Cài đặt Thanh toán', tab: 'payment', icon: 'account_balance' },
  { id: 'payment-online', title: 'Thanh toán online', description: 'Cài đặt Thanh toán', tab: 'payment', icon: 'credit_card' },
  { id: 'theme-mode', title: 'Chế độ giao diện', description: 'Cài đặt Giao diện', tab: 'theme', icon: 'palette' }
]

// Store Settings
const storeSettings = ref({
  name: '',
  slogan: '',
  email: '',
  phone: '',
  address: ''
})

// General Settings
const generalSettings = ref({
  currency: 'VND',
  timezone: 'Asia/Ho_Chi_Minh',
  language: 'vi',
  productsPerPage: 12,
  maintenanceMode: false
})

// Email Settings
const emailSettings = ref({
  smtpHost: '',
  smtpPort: 587,
  fromEmail: '',
  fromName: '',
  enableNotifications: true
})

// Payment Settings
const paymentSettings = ref({
  cod: {
    enabled: true
  },
  bankTransfer: {
    enabled: true,
    bankName: 'Vietcombank',
    accountNumber: '1234567890',
    accountName: 'SNEAKERY STORE CO., LTD'
  },
  online: {
    enabled: false
  }
})

// Load settings from API (data thật từ database)
const loadSettings = async () => {
  try {
    // Load từ API thay vì localStorage/mock data
    const apiSettings = await adminStore.fetchSettings()
    
    if (apiSettings && Object.keys(apiSettings).length > 0) {
      // Map settings từ API vào các refs
      if (apiSettings.store) {
        storeSettings.value = {
          name: apiSettings.store.name || '',
          slogan: apiSettings.store.slogan || '',
          email: apiSettings.store.email || '',
          phone: apiSettings.store.phone || '',
          address: apiSettings.store.address || ''
        }
      }
      
      if (apiSettings.general) {
        generalSettings.value = {
          currency: apiSettings.general.currency || 'VND',
          timezone: apiSettings.general.timezone || 'Asia/Ho_Chi_Minh',
          language: apiSettings.general.language || 'vi',
          productsPerPage: apiSettings.general.productsPerPage || 12,
          maintenanceMode: apiSettings.general.maintenanceMode || false
        }
      }
      
      if (apiSettings.email) {
        emailSettings.value = {
          smtpHost: apiSettings.email.smtpHost || '',
          smtpPort: apiSettings.email.smtpPort || 587,
          fromEmail: apiSettings.email.fromEmail || '',
          fromName: apiSettings.email.fromName || '',
          enableNotifications: apiSettings.email.enableNotifications !== undefined ? apiSettings.email.enableNotifications : true
        }
      }
      
      if (apiSettings.payment) {
        paymentSettings.value = {
          cod: {
            enabled: apiSettings.payment.cod?.enabled !== undefined ? apiSettings.payment.cod.enabled : true
          },
          bankTransfer: {
            enabled: apiSettings.payment.bankTransfer?.enabled !== undefined ? apiSettings.payment.bankTransfer.enabled : true,
            bankName: apiSettings.payment.bankTransfer?.bankName || '',
            accountNumber: apiSettings.payment.bankTransfer?.accountNumber || '',
            accountName: apiSettings.payment.bankTransfer?.accountName || ''
          },
          online: {
            enabled: apiSettings.payment.online?.enabled !== undefined ? apiSettings.payment.online.enabled : false
          }
        }
      }
      
      logger.log('✅ Settings loaded from API:', apiSettings)
    } else {
      // Nếu không có data từ API, giữ giá trị mặc định (không dùng mock data)
      logger.log('⚠️ No settings found in database, using defaults')
    }
  } catch (error) {
    logger.error('Error loading settings:', error)
    // Không hiển thị error để tránh làm phiền user, chỉ log
    // Settings sẽ giữ giá trị mặc định
  }
}

// Save methods - lưu vào database qua API
const saveStoreSettings = async () => {
  try {
    await adminStore.updateSettings({ store: storeSettings.value })
    notificationService.success('Thành công','Đã lưu thông tin cửa hàng thành công!')
  } catch (error) {
    logger.error('Error saving store settings:', error)
    notificationService.apiError(error, 'Không thể lưu thông tin cửa hàng')
  }
}

const saveGeneralSettings = async () => {
  try {
    await adminStore.updateSettings({ general: generalSettings.value })
    notificationService.success('Thành công','Đã lưu cài đặt chung thành công!')
  } catch (error) {
    logger.error('Error saving general settings:', error)
    notificationService.apiError(error, 'Không thể lưu cài đặt chung')
  }
}

const saveEmailSettings = async () => {
  try {
    await adminStore.updateSettings({ email: emailSettings.value })
    notificationService.success('Thành công','Đã lưu cài đặt email thành công!')
  } catch (error) {
    logger.error('Error saving email settings:', error)
    notificationService.apiError(error, 'Không thể lưu cài đặt email')
  }
}

const savePaymentSettings = async () => {
  try {
    await adminStore.updateSettings({ payment: paymentSettings.value })
    notificationService.success('Thành công','Đã lưu cài đặt thanh toán thành công!')
  } catch (error) {
    logger.error('Error saving payment settings:', error)
    notificationService.apiError(error, 'Không thể lưu cài đặt thanh toán')
  }
}

const testEmail = () => {
  notificationService.info('Thông tin','Tính năng gửi email test đang được phát triển...')
}

// Search functionality
const handleSearch = () => {
  if (!searchQuery.value) {
    searchResults.value = []
    return
  }
  
  const query = searchQuery.value.toLowerCase()
  searchResults.value = settingsIndex.filter(item => 
    item.title.toLowerCase().includes(query) ||
    item.description.toLowerCase().includes(query)
  )
}

const clearSearch = () => {
  searchQuery.value = ''
  searchResults.value = []
}

const goToSetting = (result) => {
  activeTab.value = result.tab
  searchQuery.value = ''
  searchResults.value = []
  showSearch.value = false
  notificationService.success('Thành công',`Đã chuyển đến: ${result.title}`)
}

// Export/Import Settings
const exportSettings = async () => {
  try {
    // Load settings từ API trước khi export
    const apiSettings = await adminStore.fetchSettings()
    const allSettings = {
      ...apiSettings,
      exportedAt: new Date().toISOString(),
      version: '1.0'
    }
    
    downloadJson(allSettings, `sneakery-settings-${new Date().toISOString().split('T')[0]}.json`)
    notificationService.success('Thành công','Đã xuất cấu hình thành công!')
  } catch (error) {
    logger.error('Error exporting settings:', error)
    notificationService.apiError(error, 'Không thể xuất cấu hình')
  }
}

const importSettings = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.json'
  
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    const reader = new FileReader()
    reader.onload = (event) => {
      try {
        const data = JSON.parse(event.target.result)
        
        // Validate structure
        if (!data.store || !data.general || !data.email || !data.payment) {
          throw new Error('Invalid settings file structure')
        }
        
        confirmDialogService.confirm(
          'Bạn có chắc muốn nhập cấu hình này? Các cài đặt hiện tại sẽ bị ghi đè.',
          'Xác nhận nhập cấu hình',
          {
            confirmButtonText: 'Nhập',
            cancelButtonText: 'Hủy',
            type: 'warning'
          }
        ).then(async () => {
          // Update local state
          storeSettings.value = { ...storeSettings.value, ...data.store }
          generalSettings.value = { ...generalSettings.value, ...data.general }
          emailSettings.value = { ...emailSettings.value, ...data.email }
          paymentSettings.value = { ...paymentSettings.value, ...data.payment }
          
          // Save to database via API
          await adminStore.updateSettings({
            store: storeSettings.value,
            general: generalSettings.value,
            email: emailSettings.value,
            payment: paymentSettings.value
          })
          
          notificationService.success('Thành công','Đã nhập cấu hình thành công!')
        }).catch(() => {
          notificationService.info('Thông tin','Đã hủy nhập cấu hình')
        })
      } catch (error) {
        logger.error('Error importing settings:', error)
        notificationService.apiError(error, 'Lỗi khi nhập cấu hình')
      }
    }
    reader.readAsText(file)
  }
  
  input.click()
}

// Reset to defaults
const resetToDefaults = async () => {
  try {
    await confirmDialogService.confirm(
      'Bạn có chắc muốn khôi phục tất cả cài đặt về mặc định? Hành động này không thể hoàn tác.',
      'Xác nhận khôi phục',
      {
        confirmButtonText: 'Khôi phục',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    // Reset all settings to defaults (empty values, not mock data)
    const defaultSettings = {
      store: {
        name: '',
        slogan: '',
        email: '',
        phone: '',
        address: ''
      },
      general: {
        currency: 'VND',
        timezone: 'Asia/Ho_Chi_Minh',
        language: 'vi',
        productsPerPage: 12,
        maintenanceMode: false
      },
      email: {
        smtpHost: '',
        smtpPort: 587,
        fromEmail: '',
        fromName: '',
        enableNotifications: true
      },
      payment: {
        cod: { enabled: true },
        bankTransfer: {
          enabled: true,
          bankName: '',
          accountNumber: '',
          accountName: ''
        },
        online: { enabled: false }
      }
    }
    
    // Update local state
    storeSettings.value = defaultSettings.store
    generalSettings.value = defaultSettings.general
    emailSettings.value = defaultSettings.email
    paymentSettings.value = defaultSettings.payment
    
    // Save to database
    await adminStore.updateSettings(defaultSettings)
    
    notificationService.success('Thành công','Đã khôi phục cài đặt mặc định thành công!')
  } catch (error) {
    if (error !== 'cancel') {
      logger.error('Error resetting settings:', error)
      notificationService.apiError(error, 'Không thể khôi phục cài đặt mặc định')
    }
  }
}

// Theme settings
const handleThemeChange = (newTheme) => {
  setTheme(newTheme)
}

const saveThemeSettings = () => {
  // Theme is already saved when changed via handleThemeChange
  notificationService.success('Thành công','Đã lưu cài đặt giao diện thành công!')
}

onMounted(() => {
  loadSettings()
})
</script>



