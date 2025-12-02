<template>
  <div
    class="relative"
    :style="{
      width: sizePx,
      height: sizePx,
      perspective: '900px',
    }"
  >
    <!-- Orbit 1 -->
    <div
      class="absolute rounded-full"
      :class="glowClass"
      :style="{
        width: sizePx,
        height: sizePx,
        borderBottom: `3px solid ${finalColor}`,
        animation: `orbit-one ${speed} linear infinite`,
      }"
    ></div>

    <!-- Orbit 2 -->
    <div
      class="absolute rounded-full"
      :class="glowClass"
      :style="{
        width: sizePx,
        height: sizePx,
        borderRight: `3px solid ${finalColor}`,
        animation: `orbit-two ${speed} linear infinite`,
      }"
    ></div>

    <!-- Orbit 3 -->
    <div
      class="absolute rounded-full"
      :class="glowClass"
      :style="{
        width: sizePx,
        height: sizePx,
        borderTop: `3px solid ${finalColor}`,
        animation: `orbit-three ${speed} linear infinite`,
      }"
    ></div>
  </div>
</template>

<script setup>
import { computed } from "vue";

/**
 * Props custom:
 * - size: px
 * - color: override (optional)
 * - speed: animation speed
 * - glow: boolean (premium effect)
 */
const props = defineProps({
  size: { type: Number, default: 55 },
  color: { type: String, default: "" }, // để auto chọn theo theme
  speed: { type: String, default: "1200ms" },
  glow: { type: Boolean, default: true },
});

/**
 * Màu theo theme Sneakery
 * Light mode  → #8b5cf6 (purple-500)
 * Dark mode   → #a78bfa (purple-400)
 */
const finalColor = computed(() => {
  if (props.color) return props.color;

  const isDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
  return isDark ? "#a78bfa" : "#8b5cf6";
});

const sizePx = computed(() => `${props.size}px`);

const glowClass = computed(() =>
  props.glow
    ? "shadow-[0_0_12px_rgba(139,92,246,0.5)] dark:shadow-[0_0_14px_rgba(167,139,250,0.7)]"
    : ""
);
</script>

<style>
/* Không scoped → để animation không bị hash */
@keyframes orbit-one {
  0% {
    transform: rotateX(35deg) rotateY(-45deg) rotateZ(0deg);
  }
  100% {
    transform: rotateX(35deg) rotateY(-45deg) rotateZ(360deg);
  }
}

@keyframes orbit-two {
  0% {
    transform: rotateX(50deg) rotateY(10deg) rotateZ(0deg);
  }
  100% {
    transform: rotateX(50deg) rotateY(10deg) rotateZ(360deg);
  }
}

@keyframes orbit-three {
  0% {
    transform: rotateX(35deg) rotateY(55deg) rotateZ(0deg);
  }
  100% {
    transform: rotateX(35deg) rotateY(55deg) rotateZ(360deg);
  }
}
</style>
