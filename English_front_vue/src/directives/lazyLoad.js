// 图片懒加载指令
export const lazyLoad = {
  mounted(el, binding) {
    // 创建 Intersection Observer
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          // 元素进入视口
          const img = entry.target
          const src = binding.value
          
          if (src) {
            // 创建新的图片对象来预加载
            const newImg = new Image()
            
            newImg.onload = () => {
              // 图片加载成功后设置src
              img.src = src
              img.classList.remove('lazy-loading')
              img.classList.add('lazy-loaded')
            }
            
            newImg.onerror = () => {
              // 图片加载失败时的处理
              img.classList.remove('lazy-loading')
              img.classList.add('lazy-error')
              console.warn('图片加载失败:', src)
            }
            
            // 开始加载图片
            newImg.src = src
          }
          
          // 停止观察该元素
          observer.unobserve(img)
        }
      })
    }, {
      // 配置选项
      rootMargin: '50px', // 提前50px开始加载
      threshold: 0.1 // 10%可见时触发
    })
    
    // 开始观察元素
    observer.observe(el)
    
    // 添加加载中的样式
    el.classList.add('lazy-loading')
    
    // 将observer存储到元素上，以便后续清理
    el._lazyObserver = observer
  },
  
  unmounted(el) {
    // 清理observer
    if (el._lazyObserver) {
      el._lazyObserver.disconnect()
      delete el._lazyObserver
    }
  }
}

// 懒加载样式
export const lazyLoadStyles = `
.lazy-loading {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

.lazy-loaded {
  animation: fadeIn 0.3s ease-in-out;
}

.lazy-error {
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.lazy-error::before {
  content: "图片加载失败";
  font-size: 12px;
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
`