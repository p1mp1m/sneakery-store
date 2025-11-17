<template>
  <div>
    <!-- Popup -->
    <div
      v-if="asPopup && visible"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
      @click.self="closePopup"
    >
      <div
        class="bg-white dark:bg-gray-900 w-[450px] rounded-xl shadow-xl p-4 max-h-[85vh] overflow-y-auto"
      >
        <div class="flex justify-between items-center mb-3">
          <h2 class="text-lg font-bold">Danh sách mã khuyến mãi</h2>
          <button @click="closePopup" class="text-xl">✕</button>
        </div>

        <CouponCard
          v-for="coupon in coupons"
          :key="coupon.id"
          :coupon="coupon"
          @use="selectCoupon"
        />
      </div>
    </div>

    <!-- Dạng cố định -->
    <div v-else>
      <CouponCard
        v-for="coupon in coupons"
        :key="coupon.id"
        :coupon="coupon"
        @use="selectCoupon"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "@/utils/axios";
import CouponCard from "./CouponCard.vue";

const props = defineProps({
  asPopup: { type: Boolean, default: false },
  visible: { type: Boolean, default: false },
});

const emit = defineEmits(["close", "select"]);

const coupons = ref([]);

const loadCoupons = async () => {
  try {
    const res = await axios.get("/coupons/active");
    coupons.value = res.data;
  } catch (e) {
    console.error("Lỗi load coupons", e);
  }
};

const selectCoupon = (coupon) => {
  emit("select", coupon); // gửi coupon ra CartPage
};
const closePopup = () => emit("close");

onMounted(() => loadCoupons());
</script>
