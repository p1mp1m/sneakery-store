// src/stores/admin/adminProductImages.js
import { defineStore } from "pinia";
import axios from "@/utils/axios";
import { ref } from "vue";

export const useAdminProductImageStore = defineStore("adminProductImages", () => {
  const loading = ref(false);
  const imagesByProduct = ref({});

    const loadAllAdminImages = async () => {
        console.log("🔥 STORE: loadAllAdminImages called!");
        loading.value = true;

        const res = await axios.get("/admin/products/images");

        console.log("🔥 STORE: API returned", res.data);

        const map = {};
        res.data.forEach(item => {
            map[item.productId] = item.images;
        });

        imagesByProduct.value = map;
        loading.value = false;
    };

  const getImages = (productId) => {
    return imagesByProduct.value[productId] || [];
  };

  return {
    loading,
    imagesByProduct,
    loadAllAdminImages,
    getImages
  };
});
