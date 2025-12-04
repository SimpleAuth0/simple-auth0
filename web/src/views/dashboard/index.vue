<template>
  <div class="dashboard-root">

    <!-- 顶部欢迎语 -->
    <div class="welcome-card">
      <h1 class="welcome-title">欢迎回来，{{ name }} 👋</h1>
      <p class="welcome-sub">SimpleAuth0 统一身份认证中心</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">

      <div v-for="item in stats" :key="item.title" class="stat-card">
        <div class="stat-icon" :style="{ background: item.color }">
          <i :class="item.icon" />
        </div>
        <div class="stat-info">
          <div class="stat-title">{{ item.title }}</div>
          <div class="stat-value">{{ item.value }}</div>
        </div>
      </div>

    </div>

    <!-- 用户活跃折线图 -->
    <el-card class="chart-card" shadow="never">
      <div class="chart-header">
        <div class="chart-title">用户日活跃趋势</div>

        <el-radio-group v-model="range" size="small" @change="loadChartData">
          <el-radio-button label="7">近 7 天</el-radio-button>
          <el-radio-button label="30">近 30 天</el-radio-button>
          <el-radio-button label="90">近 90 天</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 渲染 ECharts 折线图 -->
      <div ref="chart" class="chart" />

    </el-card>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import echarts from 'echarts'

export default {
  name: 'Dashboard',

  computed: {
    ...mapGetters(['name'])
  },

  data() {
    return {
      range: '7',

      chart: null,

      stats: [
        {
          title: '在线用户',
          value: 128,
          icon: 'el-icon-user-solid',
          color: 'linear-gradient(135deg, #60a5fa, #3b82f6)'
        },
        {
          title: '应用总量',
          value: 42,
          icon: 'el-icon-menu',
          color: 'linear-gradient(135deg, #a78bfa, #7c3aed)'
        },
        {
          title: '接口调用量',
          value: '24,581',
          icon: 'el-icon-data-line',
          color: 'linear-gradient(135deg, #34d399, #059669)'
        },
        {
          title: '今日登录',
          value: 642,
          icon: 'el-icon-s-check',
          color: 'linear-gradient(135deg, #f87171, #dc2626)'
        }
      ]
    }
  },

  mounted() {
    this.chart = echarts.init(this.$refs.chart)
    this.loadChartData()
  },

  methods: {

    // 获取折线图数据（示例数据）
    loadChartData() {
      const days = Number(this.range)

      const now = new Date()
      const xData = []
      const yData = []

      for (let i = days - 1; i >= 0; i--) {
        const d = new Date(now)
        d.setDate(now.getDate() - i)

        xData.push(`${d.getMonth() + 1}-${d.getDate()}`)
        yData.push(Math.floor(Math.random() * 300 + 50)) // 模拟活跃人数
      }

      this.renderChart(xData, yData)
    },

    renderChart(xData, yData) {
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xData,
          boundaryGap: false,
          axisLine: { lineStyle: { color: '#9ca3af' }}
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#e5e7eb' }}
        },
        series: [
          {
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            data: yData,
            lineStyle: {
              width: 3,
              color: '#3b82f6'
            },
            itemStyle: {
              color: '#3b82f6'
            },
            areaStyle: {
              color: 'rgba(59,130,246,0.15)'
            }
          }
        ]
      }

      this.chart.setOption(option)
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-root {
  min-height: 100vh;
  padding: 30px;
  background: #ffffff;
  color: #1f2937;
}

/* 顶部欢迎卡片 */
.welcome-card {
  margin-bottom: 30px;
  padding: 28px 32px;
  border-radius: 14px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);

  .welcome-title {
    font-size: 28px;
    font-weight: 600;
    margin: 0;
    color: #111827;
  }
  .welcome-sub {
    margin-top: 10px;
    font-size: 15px;
    color: #6b7280;
  }
}

/* 统计卡片布局 */
.stats-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  margin-bottom: 30px;
}

.stat-card {
  flex: 1;
  min-width: 260px;
  display: flex;
  align-items: center;
  padding: 22px 26px;
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 6px rgba(0,0,0,0.04);
  transition: 0.2s;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  }

  .stat-icon {
    width: 58px;
    height: 58px;
    border-radius: 12px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #fff;

    i {
      font-size: 28px;
    }
  }

  .stat-info {
    margin-left: 18px;

    .stat-title {
      font-size: 15px;
      color: #6b7280;
    }

    .stat-value {
      margin-top: 6px;
      font-size: 28px;
      font-weight: 700;
      color: #111827;
    }
  }
}

/* 图表卡片 */
.chart-card {
  padding: 20px;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;

  .chart-title {
    font-size: 18px;
    font-weight: 600;
    color: #111827;
  }
}

.chart {
  width: 100%;
  height: 320px;
}

@media (max-width: 768px) {
  .stats-row {
    flex-direction: column;
  }
}
</style>
