<template>
  <div
    class="min-h-screen bg-gradient-to-br from-purple-600 via-purple-700 to-indigo-800 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 flex items-center justify-center p-4"
  >
    <div
      class="w-full max-w-6xl grid grid-cols-1 lg:grid-cols-2 gap-8 items-center"
    >
      <!-- LEFT: CARD -->
      <div
        class="bg-white/5 backdrop-blur-xl dark:bg-gray-800/60 rounded-2xl shadow-2xl p-8 md:p-12"
      >
        <!-- Logo -->
        <div class="flex justify-center mb-6">
          <img
            src="@/assets/images/logo.png"
            class="h-16"
            alt="Sneakery Store"
          />
        </div>


        <!-- Title -->
        <h2 class="text-2xl font-bold text-gray-100 text-center mb-2">
          Đặt lại mật khẩu
        </h2>
        <p class="text-sm text-gray-300 text-center mb-8">
          Nhập mật khẩu mới và xác nhận bên dưới.
        </p>


        <!-- Error -->
        <div
          v-if="serverError"
          class="mb-6 p-4 bg-red-500/10 border border-red-500 text-red-300 rounded-lg flex gap-3 text-sm"
        >
          <i class="material-icons mt-0.5">error</i>
          {{ serverError }}
        </div>


        <!-- FORM -->
        <form @submit.prevent="handleReset" class="space-y-5">
          <!-- Password -->
          <div>
            <label class="block text-sm text-gray-300 mb-1">Mật khẩu mới</label>
            <div class="relative">
              <i
                class="material-icons absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-base"
                >lock</i
              >
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Nhập mật khẩu mới"
                class="w-full pl-11 pr-12 py-3 border border-gray-600 rounded-lg bg-gray-800/50 text-gray-100 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-200"
              >
                <i class="material-icons">{{
                  showPassword ? "visibility_off" : "visibility"
                }}</i>
              </button>
            </div>
            <p v-if="errors.password" class="text-xs text-red-400 mt-1">
              {{ errors.password }}
            </p>
          </div>


          <!-- Confirm -->
          <div>
            <label class="block text-sm text-gray-300 mb-1"
              >Nhập lại mật khẩu</label
            >
            <div class="relative">
              <i
                class="material-icons absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-base"
                >lock</i
              >
              <input
                v-model="form.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="Nhập lại mật khẩu"
                class="w-full pl-11 pr-12 py-3 border border-gray-600 rounded-lg bg-gray-800/50 text-gray-100 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
              />
              <button
                type="button"
                @click="showConfirmPassword = !showConfirmPassword"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-200"
              >
                <i class="material-icons">{{
                  showConfirmPassword ? "visibility_off" : "visibility"
                }}</i>
              </button>
            </div>
            <p v-if="errors.confirmPassword" class="text-xs text-red-400 mt-1">
              {{ errors.confirmPassword }}
            </p>
          </div>


          <!-- Submit -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-purple-700 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-purple-800 focus:ring-2 focus:ring-purple-500 disabled:opacity-50 flex items-center justify-center gap-2 transition-all"
          >
            <i v-if="loading" class="material-icons animate-spin">refresh</i>
            <span>{{ loading ? "Đang xử lý..." : "Cập nhật mật khẩu" }}</span>
          </button>


          <!-- Back -->
          <div class="text-center pt-2">
            <a
              class="text-xs text-gray-300 hover:text-purple-400 cursor-pointer"
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
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import notificationService from "@/utils/notificationService";
import { API_ENDPOINTS, buildApiUrl } from "@/config/api";


const router = useRouter();
const route = useRoute();


const token = route.query.token;
if (!token) router.push("/forgot-password");


const loading = ref(false);
const serverError = ref("");
const errors = ref({});
const showPassword = ref(false);
const showConfirmPassword = ref(false);


const form = ref({
  password: "",
  confirmPassword: "",
});


const features = [
  "Sản phẩm chính hãng 100%",
  "Giao hàng nhanh chóng",
  "Hỗ trợ 24/7",
];


const validate = () => {
  errors.value = {};
  if (!form.value.password) {
    errors.value.password = "Vui lòng nhập mật khẩu";
    return false;
  }
  if (form.value.password.length < 6) {
    errors.value.password = "Mật khẩu phải từ 6 ký tự";
    return false;
  }
  if (form.value.password !== form.value.confirmPassword) {
    errors.value.confirmPassword = "Mật khẩu không khớp";
    return false;
  }
  return true;
};


const handleReset = async () => {
  if (!validate()) return;
  loading.value = true;


  try {
    await axios.post(buildApiUrl(API_ENDPOINTS.AUTH.RESET_PASSWORD), {
      token,
      newPassword: form.value.password,
    });


    notificationService.success("Thành công", "Mật khẩu đã được cập nhật!");
    router.push("/login");
  } catch (err) {
    serverError.value = err.response?.data?.message || "Có lỗi xảy ra";
  } finally {
    loading.value = false;
  }
};
</script>



