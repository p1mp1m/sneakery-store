<template>
  <div
    v-if="visible"
    class="fixed inset-0 z-[9999] bg-black/50 backdrop-blur-sm flex items-center justify-center p-4"
    @click.self="handleClose"
  >
    <div
      class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto scrollbar-hide animate-in fade-in zoom-in duration-200"
      role="dialog"
      aria-modal="true"
      :aria-label="mode === 'edit' ? 'Chỉnh sửa địa chỉ' : 'Thêm địa chỉ mới'"
    >
      <!-- Header -->
      <div
        class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700 bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20"
      >
        <h3
          class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
        >
          <i
            class="material-icons text-purple-600 dark:text-purple-400"
          >{{ mode === "edit" ? "edit_location_alt" : "add_location" }}</i>
          {{ mode === "edit" ? "Chỉnh sửa địa chỉ" : "Thêm địa chỉ mới" }}
        </h3>

        <button
          @click="handleClose"
          class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
          aria-label="Đóng"
        >
          <i class="material-icons">close</i>
        </button>
      </div>

      <!-- Body -->
      <div class="p-6 space-y-4">
        <!-- Recipient -->
        <div>
          <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
            Họ tên người nhận *
          </label>
          <input
            v-model="form.recipientName"
            type="text"
            class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
            placeholder="Nguyễn Văn A"
          />
        </div>

        <!-- Phone -->
        <div>
          <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
            Số điện thoại *
          </label>
          <input
            v-model="form.phone"
            type="tel"
            class="w-full px-4 py-2 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 transition-all"
            :class="[
              form.phone && !validateVietnamesePhone(form.phone)
                ? 'border-red-300 dark:border-red-600 focus:ring-red-500 focus:border-red-500'
                : 'border-gray-200 dark:border-gray-600 focus:ring-purple-500 focus:border-purple-500',
            ]"
            placeholder="0912345678"
            @blur="form.phone = formatPhoneNumber(form.phone)"
          />
          <p
            v-if="form.phone && !validateVietnamesePhone(form.phone)"
            class="text-xs text-red-600 dark:text-red-400 mt-1 flex items-center gap-1"
          >
            <i class="material-icons text-xs">error</i>
            Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam.
          </p>
        </div>

        <!-- Email (optional) -->
        <!-- <div>
          <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
            Email (không bắt buộc)
          </label>
          <input
            v-model="form.email"
            type="email"
            class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
            placeholder="email@example.com"
          />
        </div> -->

        <!-- Line1 -->
        <div>
          <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
            Địa chỉ *
          </label>
          <input
            v-model="form.line1"
            type="text"
            class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
            placeholder="123 Đường ABC"
          />
          <p
            v-if="form.line1 && !validateAddress(form.line1)"
            class="text-xs text-red-600 dark:text-red-400 mt-1 flex items-center gap-1"
          >
            <i class="material-icons text-xs">error</i>
            Địa chỉ không hợp lệ. Vui lòng nhập tối thiểu 5 ký tự.
          </p>
        </div>

        <!-- Line2 -->
        <div>
          <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
            Địa chỉ bổ sung (không bắt buộc)
          </label>
          <input
            v-model="form.line2"
            type="text"
            class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
            placeholder="Căn hộ, tòa nhà..."
          />
        </div>

        <!-- City + District -->
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
              Tỉnh/Thành phố *
            </label>
            <select
              v-model="form.city"
              class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
            >
              <option value="">-- Chọn Tỉnh/Thành phố --</option>
              <option v-for="(p, code) in provinces" :key="code" :value="code">
                {{ p.name_with_type }}
              </option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
              Quận/Huyện *
            </label>
            <select
              v-model="form.district"
              :disabled="!filteredDistricts.length"
              class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 disabled:opacity-60"
            >
              <option value="">-- Chọn Quận/Huyện --</option>
              <option v-for="d in filteredDistricts" :key="d.code" :value="d.code">
                {{ d.name_with_type }}
              </option>
            </select>
          </div>
        </div>

        <!-- Ward -->
        <div>
          <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
            Phường/Xã *
          </label>
          <select
            v-model="form.ward"
            :disabled="!filteredWards.length"
            class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 disabled:opacity-60"
          >
            <option value="">-- Chọn Phường/Xã --</option>
            <option v-for="w in filteredWards" :key="w.code" :value="w.code">
              {{ w.name_with_type }}
            </option>
          </select>
        </div>

        <!-- Postal -->
        <!-- <div>
          <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
            Mã bưu chính
          </label>
          <input
            v-model="form.postalCode"
            type="text"
            class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
            placeholder="(tự điền theo Tỉnh/TP hoặc nhập tay)"
          />
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
            Hệ thống sẽ tự điền theo Tỉnh/Thành phố nếu có dữ liệu.
          </p>
        </div> -->
      </div>

      <!-- Footer -->
      <div class="flex gap-3 p-6 border-t border-gray-200 dark:border-gray-700">
        <button
          @click="handleSubmit"
          :disabled="submitting"
          class="flex-1 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all disabled:opacity-60 disabled:cursor-not-allowed"
        >
          <span v-if="!submitting">{{ mode === "edit" ? "Lưu thay đổi" : "Lưu địa chỉ" }}</span>
          <span v-else>Đang xử lý...</span>
        </button>

        <button
          @click="handleClose"
          :disabled="submitting"
          class="flex-1 px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
        >
          Hủy
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch, nextTick } from "vue";
import provinces from "@/data/province_old.json";
import districts from "@/data/district_old.json";
import wards from "@/data/ward_old.json";
import postalData from "@/data/postalCode.json";
import {
  validateVietnamesePhone,
  formatPhoneNumber,
  validateAddress,
} from "@/utils/validators";

const props = defineProps({
  visible: { type: Boolean, default: false },

  /**
   * mode:
   * - "create": thêm mới
   * - "edit": chỉnh sửa
   */
  mode: { type: String, default: "create" },

  /**
   * initialAddress:
   * - Nếu mode="edit": truyền object address từ server (thường là NAME: city/district/ward)
   * - Nếu mode="create": có thể null
   */
  initialAddress: { type: Object, default: null },

  /**
   * submitting:
   * - Parent có thể control state trong lúc gọi API
   */
  submitting: { type: Boolean, default: false },
});

const emit = defineEmits(["update:visible", "close", "submit"]);

/**
 * Reverse lookup: NAME -> CODE (để edit form theo code)
 */
const findCityCodeByName = (name) => {
  if (!name) return "";
  return (
    Object.keys(provinces).find(
      (c) => provinces[c].name === name || provinces[c].name_with_type === name
    ) || ""
  );
};

const findDistrictCodeByName = (name) => {
  if (!name) return "";
  return (
    Object.keys(districts).find(
      (d) => districts[d].name === name || districts[d].name_with_type === name
    ) || ""
  );
};

const findWardCodeByName = (name) => {
  if (!name) return "";
  return (
    Object.keys(wards).find(
      (w) => wards[w].name === name || wards[w].name_with_type === name
    ) || ""
  );
};

/**
 * Form state dùng CODE cho city/district/ward
 */
const form = reactive({
  id: null,
  recipientName: "",
  phone: "",
//   email: "",
  line1: "",
  line2: "",
  city: "", // code
  district: "", // code
  ward: "", // code
  postalCode: "",
});

const filteredDistricts = ref([]);
const filteredWards = ref([]);
const isHydrating = ref(false);

const isEdit = computed(() => props.mode === "edit");

/**
 * Hydrate form khi mở modal / đổi initialAddress
 */
const hydrateFromInitial = async () => {
  isHydrating.value = true;

  const a = props.initialAddress;

  // reset base fields
  form.id = a?.id ?? null;
  form.recipientName = a?.recipientName ?? "";
  form.phone = a?.phone ?? "";
  form.line1 = a?.line1 ?? "";
  form.line2 = a?.line2 ?? "";
  form.postalCode = a?.postalCode ?? "";

  // NAME -> CODE
  const cityCode = a?.city ? findCityCodeByName(a.city) : "";
  const districtCode = a?.district ? findDistrictCodeByName(a.district) : "";
  const wardCode = a?.ward ? findWardCodeByName(a.ward) : "";

  // 1) set city trước
  form.city = cityCode || "";

  // build districts list theo city
  filteredDistricts.value = form.city
    ? Object.values(districts).filter((d) => d.parent_code == form.city)
    : [];

  await nextTick();

  // 2) set district sau khi districts đã sẵn
  form.district = districtCode || "";

  // build wards list theo district
  filteredWards.value = form.district
    ? Object.values(wards).filter((w) => w.parent_code == form.district)
    : [];

  await nextTick();

  // 3) set ward sau khi wards đã sẵn
  form.ward = wardCode || "";

  // postal auto-fill (tuỳ bạn)
  if (!form.postalCode && form.city) {
    const item = postalData[form.city];
    form.postalCode = item ? item.postcode : "";
  }

  await nextTick();
  isHydrating.value = false;
};


watch(
  () => props.visible,
  async (v) => {
    if (v) await hydrateFromInitial();
  },
  { immediate: true }
);

watch(
  () => props.initialAddress,
  async () => {
    if (props.visible) await hydrateFromInitial();
  },
  { deep: true }
);

/**
 * Cascade: city -> districts, reset district/ward
 */
watch(
  () => form.city,
  (cityCode) => {
    filteredDistricts.value = cityCode
      ? Object.values(districts).filter((d) => d.parent_code == cityCode)
      : [];

    if (!isHydrating.value) {
      form.district = "";
      form.ward = "";
      filteredWards.value = [];
    }

    // postalCode auto fill
    if (!cityCode) {
      if (!isHydrating.value) form.postalCode = "";
      return;
    }
    const item = postalData[cityCode];
    form.postalCode = item ? item.postcode : form.postalCode;
  }
);

/**
 * Cascade: district -> wards, reset ward
 */
watch(
  () => form.district,
  (districtCode) => {
    filteredWards.value = districtCode
      ? Object.values(wards).filter((w) => w.parent_code == districtCode)
      : [];

    if (!isHydrating.value) {
      form.ward = "";
    }
  }
);

/**
 * Validate trước khi submit
 */
const validateForm = () => {
  if (!form.recipientName?.trim()) {
    return "Vui lòng nhập họ tên người nhận.";
  }
  if (!form.phone?.trim()) {
    return "Vui lòng nhập số điện thoại.";
  }
  if (!validateVietnamesePhone(form.phone)) {
    return "Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0).";
  }
  if (!form.line1?.trim()) {
    return "Vui lòng nhập địa chỉ.";
  }
  if (!validateAddress(form.line1)) {
    return "Địa chỉ không hợp lệ. Vui lòng nhập địa chỉ đầy đủ (tối thiểu 5 ký tự).";
  }
  if (!form.city) {
    return "Vui lòng chọn Tỉnh/Thành phố.";
  }
  if (!form.district) {
    return "Vui lòng chọn Quận/Huyện.";
  }
  if (!form.ward) {
    return "Vui lòng chọn Phường/Xã.";
  }
  return null;
};

/**
 * Build payload CODE -> NAME giống logic bạn đang dùng
 */
const buildPayload = () => {
  return {
    // nếu edit thì giữ id cho parent tiện gọi update
    id: form.id ?? undefined,

    recipientName: form.recipientName.trim(),
    phone: form.phone.trim(),
    // email: form.email?.trim() || "",

    line1: form.line1.trim(),
    line2: form.line2?.trim() || "",

    // CODE -> NAME
    city: provinces[form.city]?.name || "",
    district: districts[form.district]?.name || "",
    ward: wards[form.ward]?.name || "",

    // giữ cả code nếu parent muốn dùng tính ship bằng code
    cityCode: form.city,
    districtCode: form.district,
    wardCode: form.ward,

    postalCode: form.postalCode || "",
  };
};

const handleSubmit = () => {
  const err = validateForm();
  if (err) {
    // parent đang dùng notificationService, ta chỉ emit error message để parent show
    emit("submit", { ok: false, error: err, mode: props.mode, payload: null });
    return;
  }

  const payload = buildPayload();
  emit("submit", { ok: true, error: null, mode: props.mode, payload });
};

const handleClose = () => {
  emit("update:visible", false);
  emit("close");
};
</script>
