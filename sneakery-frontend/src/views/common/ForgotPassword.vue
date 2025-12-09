<template>
  <div
    class="min-h-screen bg-gradient-to-br from-purple-600 via-purple-700 to-indigo-800 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 flex items-center justify-center p-4"
  >
    <div
      class="w-full max-w-6xl grid grid-cols-1 lg:grid-cols-2 gap-8 items-center"
    >
      <!-- LEFT: CARD QUÊN MẬT KHẨU -->
      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl p-8 md:p-12">
        <!-- logo -->
        <div class="flex justify-center mb-6">
          <img
            src="@/assets/images/logo.png"
            alt="Sneakery Store"
            class="h-16 mx-auto mb-4"
          />
        </div>


        <!-- title -->
        <h2
          class="text-2xl font-bold text-gray-900 dark:text-gray-100 text-center mb-2"
        >
          Quên mật khẩu
        </h2>
        <p class="text-sm text-gray-300 text-center mb-8">
          Nhập email của bạn để nhận liên kết đặt lại mật khẩu.
        </p>


        <!-- error server -->
        <div
          v-if="serverError"
          class="mb-6 p-4 bg-red-500/10 border border-red-500 rounded-lg flex gap-3 text-sm text-red-300"
        >
          <i class="material-icons mt-0.5">error</i>
          <span>{{ serverError }}</span>
        </div>


        <!-- form -->
        <form @submit.prevent="handleForgotPassword" class="space-y-5">
          <!-- email -->
          <div>
            <label class="block text-sm text-gray-300 mb-1">Email</label>
            <div class="relative">
              <i
                class="material-icons absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-base"
              >
                email
              </i>
              <input
                v-model="forgotForm.email"
                type="email"
                placeholder="Nhập email của bạn"
                class="w-full pl-11 pr-4 py-3 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 dark:focus:ring-purple-400 focus:border-transparent transition-all"
              />
            </div>
            <p v-if="errors.email" class="mt-1 text-xs text-red-400">
              {{ errors.email }}
            </p>
          </div>


          <!-- submit -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-purple-700 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-purple-800 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center justify-center gap-2"
          >
            <i v-if="loading" class="material-icons animate-spin text-base">
              refresh
            </i>
            <span>{{ loading ? "Đang gửi..." : "Gửi liên kết đặt lại" }}</span>
          </button>


          <!-- back to login -->
          <div class="text-center pt-2">
            <a
              href="#"
              class="text-xs text-gray-300 hover:text-purple-400"
              @click.prevent="router.push('/login')"
            >
              ← Quay lại đăng nhập
            </a>
          </div>
        </form>
      </div>


      <!-- RIGHT: WELCOME SECTION giống login 100% -->
      <div class="hidden lg:block text-white">
        <div class="space-y-6">
          <h2 class="text-4xl font-bold">Chào mừng trở lại Sneakery Store</h2>


          <p class="text-lg text-white/90">
            Mật khẩu mới của bạn sẽ giúp tài khoản được bảo mật tốt hơn.
          </p>


          <!-- Features -->
          <div class="space-y-4 mt-8">
            <div
              v-for="(text, i) in features"
              :key="i"
              class="flex items-center gap-3"
            >
              <div
                class="w-10 h-10 rounded-lg bg-white/20 backdrop-blur-sm flex items-center justify-center"
              >
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </div>


              <span class="text-lg">{{ text }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import notificationService from "@/utils/notificationService";
import axios from "axios";
import { API_ENDPOINTS, buildApiUrl } from "@/config/api";


const router = useRouter();
const loading = ref(false);
const serverError = ref("");
const errors = ref({});


const forgotForm = ref({
  email: "",
});


const features = [
  "Sản phẩm chính hãng 100%",
  "Giao hàng nhanh chóng",
  "Hỗ trợ 24/7",
];


// Validation
const validateForm = () => {
  errors.value = {};


  if (!forgotForm.value.email?.trim()) {
    errors.value.email = "Vui lòng nhập email";
    return false;
  }


  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(forgotForm.value.email)) {
    errors.value.email = "Email không hợp lệ";
    return false;
  }


  return true;
};


const handleForgotPassword = async () => {
  if (!validateForm()) return;


  loading.value = true;
  serverError.value = "";
  errors.value = {};


  try {
    const { data } = await axios.post(
      buildApiUrl(API_ENDPOINTS.AUTH.FORGOT_PASSWORD),
      { email: forgotForm.value.email }
    );
    notificationService.success(
      "Thành công",
      data?.message || "Liên kết đặt lại đã được gửi!"
    );
  } catch (error) {
    serverError.value =
      error.response?.data?.message || "Không thể gửi email, thử lại.";
  } finally {
    loading.value = false;
  }
};
</script>