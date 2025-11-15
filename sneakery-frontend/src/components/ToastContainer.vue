<template>
  <div
    class="fixed top-20 right-6 z-[10000] flex flex-col gap-3 w-full max-w-sm"
    role="region"
    aria-label="Thông báo"
  >
    <TransitionGroup
      name="toast"
      tag="div"
      enter-active-class="transition-all duration-300 ease-out"
      leave-active-class="transition-all duration-300 ease-in"
      enter-from-class="opacity-0 translate-x-20 scale-95"
      enter-to-class="opacity-100 translate-x-0 scale-100"
      leave-from-class="opacity-100 translate-x-0 scale-100"
      leave-to-class="opacity-0 translate-x-20 scale-95"
    >
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="relative flex items-start gap-3 px-4 py-3 rounded-2xl shadow-lg border border-white/20 backdrop-blur-md"
        :class="toastClass(toast.type)"
      >
        <!-- Icon -->
        <div class="flex-shrink-0 text-2xl">
          <span v-if="toast.type === 'success'">✅</span>
          <span v-else-if="toast.type === 'error'">❌</span>
          <span v-else-if="toast.type === 'warning'">⚠️</span>
          <span v-else-if="toast.type === 'info'">ℹ️</span>
        </div>

        <!-- Content -->
        <div class="flex-1 text-left">
          <p class="font-semibold text-white text-sm tracking-wide">
            {{ toast.title }}
          </p>
          <p class="text-xs text-white/90 mt-0.5 leading-snug">
            {{ toast.message }}
          </p>
        </div>

        <!-- Close Button -->
        <button
          v-if="toast.closable"
          @click="remove(toast.id)"
          class="absolute top-2 right-3 text-white/80 hover:text-white text-lg"
        >
          ×
        </button>

        <!-- Gradient border glow -->
        <div
          class="absolute inset-0 rounded-2xl opacity-30 blur-lg"
          :class="glowClass(toast.type)"
        ></div>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import toastService from "@/utils/toastService";

const toasts = ref([]);

const remove = (id) => {
  toastService.removeToast(id);
};

// 🔹 Tạo class nền và màu theo type
const toastClass = (type) => {
  switch (type) {
    case "success":
      return "bg-gradient-to-br from-emerald-600/90 to-teal-600/90 text-white";
    case "error":
      return "bg-gradient-to-br from-rose-600/90 to-red-600/90 text-white";
    case "warning":
      return "bg-gradient-to-br from-amber-500/90 to-orange-600/90 text-white";
    case "info":
      return "bg-gradient-to-br from-sky-600/90 to-blue-700/90 text-white";
    default:
      return "bg-gray-800/90 text-white";
  }
};

// 🔹 Hiệu ứng “aura glow” cho mỗi loại toast
const glowClass = (type) => {
  switch (type) {
    case "success":
      return "bg-emerald-400";
    case "error":
      return "bg-rose-500";
    case "warning":
      return "bg-amber-400";
    case "info":
      return "bg-sky-400";
    default:
      return "bg-gray-500";
  }
};

onMounted(() => {
  console.log("✅ ToastContainer mounted");
  const unsubscribe = toastService.subscribe((newToasts) => {
    // console.log("📩 ToastContainer nhận toasts:", newToasts);
    toasts.value = [...newToasts];
  });
  onUnmounted(() => unsubscribe());
});
</script>

<style scoped>
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(1rem) scale(0.95);
}
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

/* Ánh sáng lan tỏa nhẹ */
.backdrop-blur-md {
  backdrop-filter: blur(10px);
}
</style>
