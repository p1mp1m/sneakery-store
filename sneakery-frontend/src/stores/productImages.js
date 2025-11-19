import { defineStore } from "pinia";
import { ref } from "vue";
import axios from "@/utils/axios";

export const useProductImageStore = defineStore("productImages", () => {
  const imagesByProduct = ref({}); // Map productId → list images
  const loading = ref(false);

  const loadAll = async () => {
    loading.value = true;
    const res = await axios.get("products/images");

    const map = {};
    res.data.forEach(item => {
      map[item.productId] = item.images; // List ảnh
    });

    imagesByProduct.value = map;
    loading.value = false;
  };

  return { imagesByProduct, loadAll };
});
