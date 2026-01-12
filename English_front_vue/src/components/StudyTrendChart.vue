<template>
  <div ref="chartRef" class="w-full h-80"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  trendData: {
    type: Array,
    default: () => []
  },
  trendCategories: {
    type: Array,
    default: () => []
  }
})

const chartRef = ref(null)
let chartInstance = null

const initChart = () => {
  if (!chartRef.value) return
  
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

const updateChart = () => {
  if (!chartInstance) return

  // 确保有7天的数据，缺失的用0填充
  const categories = props.trendCategories.length ? props.trendCategories : generateDefaultCategories()
  const data = props.trendData.length ? props.trendData : new Array(7).fill(0)
  
  // 计算平均值作为折线数据
  const averageData = data.map((value, index) => {
    if (index === 0) return value
    const sum = data.slice(0, index + 1).reduce((acc, val) => acc + val, 0)
    return Math.round(sum / (index + 1))
  })

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      },
      formatter: function(params) {
        let result = `${params[0].axisValue}<br/>`
        params.forEach(param => {
          if (param.seriesName === '学习时长') {
            result += `${param.marker}${param.seriesName}: ${param.value} 分钟<br/>`
          } else if (param.seriesName === '平均趋势') {
            result += `${param.marker}${param.seriesName}: ${param.value} 分钟<br/>`
          }
        })
        return result
      }
    },
    legend: {
      data: ['学习时长', '平均趋势'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: categories,
        axisPointer: {
          type: 'shadow'
        },
        axisLabel: {
          color: '#64748b'
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '学习时长',
        min: 0,
        axisLabel: {
          formatter: '{value} 分钟',
          color: '#64748b'
        },
        nameTextStyle: {
          color: '#64748b'
        },
        splitLine: {
          lineStyle: {
            color: '#f1f5f9'
          }
        }
      }
    ],
    series: [
      {
        name: '学习时长',
        type: 'bar',
        barWidth: '60%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#60a5fa' },
            { offset: 1, color: '#6366f1' }
          ]),
          borderRadius: [8, 8, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#3b82f6' },
              { offset: 1, color: '#4f46e5' }
            ])
          }
        },
        data: data
      },
      {
        name: '平均趋势',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          color: '#f59e0b',
          width: 3
        },
        itemStyle: {
          color: '#f59e0b',
          borderColor: '#fff',
          borderWidth: 2
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(245, 158, 11, 0.1)' },
            { offset: 1, color: 'rgba(245, 158, 11, 0.01)' }
          ])
        },
        data: averageData
      }
    ]
  }

  chartInstance.setOption(option)
}

const generateDefaultCategories = () => {
  const categories = []
  const today = new Date()
  for (let i = 6; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(today.getDate() - i)
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    categories.push(`${month}-${day}`)
  }
  return categories
}

const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(async () => {
  await nextTick()
  initChart()
  window.addEventListener('resize', resizeChart)
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  window.removeEventListener('resize', resizeChart)
})

watch([() => props.trendData, () => props.trendCategories], () => {
  updateChart()
}, { deep: true })
</script>
