<template>
  <div
    class="flex items-center gap-3 p-3 bg-white dark:bg-gray-800 rounded-xl border border-gray-300 dark:border-gray-700 shadow-sm hover:shadow-md transition-all duration-150"
  >
    <!-- Icon -->
    <div
      class="w-14 h-14 rounded-lg bg-yellow-200 flex items-center justify-center overflow-hidden flex-shrink-0"
    >
      <img
        src="/coupon-icon.png"
        alt="coupon"
        class="w-full h-full object-contain"
      />
    </div>

    <!-- Nội dung -->
    <div class="flex-1">
      <!-- Giảm giá -->
      <p
        class="font-bold text-[15px] text-gray-900 dark:text-gray-100 leading-tight"
      >
        <span class="text-red-600 text-[17px] font-extrabold">
          {{ discountText }}
        </span>
      </p>

      <!-- Điều kiện -->
      <p
        class="text-[12px] text-gray-600 dark:text-gray-400 leading-tight mt-[2px]"
      >
        Đơn hàng từ {{ formatMoney(coupon.minOrderAmount) }} trở lên
      </p>

      <!-- Mã -->
      <p class="text-[12px] mt-[2px]">
        <span class="font-semibold">Mã:</span>
        <span
          class="font-bold text-yellow-800 bg-yellow-200 px-1 py-[1px] rounded"
        >
          {{ coupon.code }}
        </span>
      </p>

      <!-- HSD -->
      <p class="text-[11px] text-gray-500 leading-tight mt-[2px]">
        HSD: {{ formatDate(coupon.startAt) }} – {{ formatDate(coupon.endAt) }}
      </p>

      <!-- Lượt dùng -->
      <p v-if="coupon.maxUses" class="text-[11px] text-gray-400 leading-tight">
        Đã dùng: {{ coupon.usesCount || 0 }}/{{ coupon.maxUses }} lượt
      </p>
    </div>

    <!-- Buttons -->
    <div class="flex flex-col items-end gap-2">
      <button
        @click="copyCode"
        class="px-3 py-1 text-[12px] bg-yellow-400 rounded-full font-semibold text-gray-900 hover:bg-yellow-500 transition"
      >
        Sao chép
      </button>

      <button
        @click="useCoupon"
        class="px-3 py-1 text-[12px] rounded-full bg-purple-600 text-white hover:bg-purple-700 transition"
      >
        Dùng mã
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import toastService from "@/utils/toastService";

const props = defineProps({
  coupon: { type: Object, required: true },
});

const emit = defineEmits(["use"]);

const formatMoney = (n) => Number(n).toLocaleString("vi-VN") + "₫";

const formatDate = (d) =>
  new Date(d).toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });

const discountText = computed(() => {
  if (props.coupon.discountType === "percent") {
    const max = props.coupon.maxDiscountAmount
      ? ` (tối đa ${formatMoney(props.coupon.maxDiscountAmount)})`
      : "";
    return `Giảm ${props.coupon.value}%${max}`;
  }

  if (props.coupon.discountType === "fixed") {
    return `Giảm ${formatMoney(props.coupon.value)}`;
  }

  return "Giảm giá";
});

const copyCode = async () => {
  await navigator.clipboard.writeText(props.coupon.code);
  toastService.success("Thành công", "Đã sao chép mã vào clipboard");
};

const useCoupon = () => emit("use", props.coupon);
</script>

<style scoped>
p,
span,
button {
  user-select: none;
}
</style>
