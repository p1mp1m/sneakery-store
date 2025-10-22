import { createApp } from 'vue'
import { createPinia } from 'pinia' // 👈 1. Import Pinia

import App from './App.vue'
import router from './routers/index.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// Import custom CSS
import './assets/styles/main.css'
import './assets/styles/components.css'
import './assets/styles/admin-panel.css'
import './assets/styles/admin-design-system.css'
import './assets/styles/admin-dashboard-shared.css'

const app = createApp(App)
const pinia = createPinia() // 👈 2. Tạo một instance của Pinia

app.use(router)
app.use(ElementPlus)
app.use(pinia) // 👈 3. Sử dụng Pinia

app.mount('#app')