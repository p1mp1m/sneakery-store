<template>
  <div 
    ref="containerRef"
    class="virtual-list-container"
    :style="{ height: containerHeight + 'px', overflow: 'auto' }"
    @scroll="handleScroll"
  >
    <div :style="{ height: totalHeight + 'px', position: 'relative' }">
      <div :style="{ transform: `translateY(${offsetY}px)` }">
        <div
          v-for="item in visibleItems"
          :key="getItemKey(item, item.index)"
          :style="{ height: itemHeight + 'px' }"
        >
          <slot :item="item.data" :index="item.index" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';

const props = defineProps({
  items: {
    type: Array,
    required: true
  },
  itemHeight: {
    type: Number,
    default: 60
  },
  containerHeight: {
    type: Number,
    default: 400
  },
  overscan: {
    type: Number,
    default: 3 // Number of items to render outside visible area
  },
  getItemKey: {
    type: Function,
    default: (item, index) => index
  }
});

const containerRef = ref(null);
const scrollTop = ref(0);

const totalHeight = computed(() => props.items.length * props.itemHeight);

const startIndex = computed(() => {
  const index = Math.floor(scrollTop.value / props.itemHeight);
  return Math.max(0, index - props.overscan);
});

const endIndex = computed(() => {
  const visibleCount = Math.ceil(props.containerHeight / props.itemHeight);
  const index = startIndex.value + visibleCount + props.overscan * 2;
  return Math.min(props.items.length, index);
});

const visibleItems = computed(() => {
  return props.items.slice(startIndex.value, endIndex.value).map((item, i) => ({
    data: item,
    index: startIndex.value + i
  }));
});

const offsetY = computed(() => startIndex.value * props.itemHeight);

const handleScroll = (event) => {
  scrollTop.value = event.target.scrollTop;
};

onMounted(() => {
  if (containerRef.value) {
    scrollTop.value = containerRef.value.scrollTop;
  }
});
</script>

<style scoped>
.virtual-list-container {
  position: relative;
}
</style>

