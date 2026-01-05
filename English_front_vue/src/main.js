import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import { lazyLoad, lazyLoadStyles } from './directives/lazyLoad'
import './assets/styles/main.css'

const app = createApp(App)

// 注册懒加载指令
app.directive('lazy', lazyLoad)

// 添加懒加载样式
const style = document.createElement('style')
style.textContent = lazyLoadStyles
document.head.appendChild(style)

app.use(createPinia())
app.use(router)

app.mount('#app')
