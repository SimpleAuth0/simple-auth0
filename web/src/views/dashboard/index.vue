<template>
  <div class="dashboard-root">

    <!-- 顶部欢迎语 -->
    <div class="welcome-card">
      <h1 class="welcome-title">欢迎回来，{{ name }} 👋</h1>
      <p class="welcome-sub">SimpleAuth0 统一身份认证中心</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div
        v-for="item in stats"
        :key="item.title"
        class="stat-card"
        @click="onStatClick(item)"
      >
        <div class="stat-icon" :style="{ background: item.color }">
          <i :class="item.icon"/>
        </div>
        <div class="stat-info">
          <div class="stat-title">{{ item.title }}</div>
          <div class="stat-value">{{ item.value }}</div>
        </div>
      </div>
    </div>

    <!-- ========================= -->
    <!--       功能模块区           -->
    <!-- ========================= -->
    <div class="features-row">

      <!-- 应用上线管理 -->
      <el-card class="feature-card">
        <div class="feature-title">应用上线和管理</div>
        <div class="feature-sub">选择应用类型</div>

        <div class="app-grid">
          <div
            v-for="app in appList"
            :key="app.title"
            class="app-item"
            @click="goto(app.path)"
          >
            <img :src="app.icon" class="app-icon">
            <div class="app-title">{{ app.title }}</div>
          </div>
        </div>

        <div class="feature-btn" @click="goto('/app')">
          查看所有应用 →
        </div>
      </el-card>

      <!-- 管理用户 -->
      <el-card class="feature-card">
        <div class="feature-title">管理用户</div>
        <div class="feature-sub">管理应用程序的用户</div>

        <img
          class="feature-illustration"
          :src="require('@/assets/img/people.svg')"
        >

        <div class="feature-btn" @click="goto('/userManagement/user')">
          查看用户 →
        </div>
      </el-card>

      <!-- 社交登录 -->
      <el-card class="feature-card">
        <div class="feature-title">增强应用登录功能</div>
        <div class="feature-sub">允许用户选择身份提供商登录</div>

        <img
          class="feature-illustration"
          :src="require('@/assets/img/space-exploration.svg')"
        >

        <div class="feature-btn" @click="goto('/identity/social')">
          建立社交登录 →
        </div>
      </el-card>

    </div>

  </div>
</template>

<script>
import {mapGetters} from 'vuex'

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
          color: 'linear-gradient(135deg,#60a5fa,#3b82f6)',
          path: '/users/online'
        },
        {
          title: '应用总量',
          value: 42,
          icon: 'el-icon-menu',
          color: 'linear-gradient(135deg,#a78bfa,#7c3aed)',
          path: '/apps'
        },
        {
          title: '接口调用量',
          value: '24,581',
          icon: 'el-icon-data-line',
          color: 'linear-gradient(135deg,#34d399,#059669)',
          path: '/statistics/api'
        },
        {
          title: '今日登录',
          value: 642,
          icon: 'el-icon-s-check',
          color: 'linear-gradient(135deg,#f87171,#dc2626)',
          path: '/statistics/login'
        }
      ],

      appList: [
        {
          title: '单页应用程序',
          icon: require('@/assets/img/spa.svg'),
          path: '/app/create'
        },
        {
          title: '传统 Web 应用程序',
          icon: require('@/assets/img/web.svg'),
          path: '/app/create'
        },
        {
          title: '移动应用',
          icon: require('@/assets/img/mobile.svg'),
          path: '/app/create'
        },
        {
          title: '基于标准的应用程序',
          icon: require('@/assets/img/m2m.svg'),
          path: '/app/create'
        }
      ]
    }
  },

  mounted() {

  },

  methods: {
    goto(path) {
      this.$router.push(path)
    },

    onStatClick(item) {
      if (item.path) this.$router.push(item.path)
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

/*************** 顶部欢迎 ***************/
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
    color: #111827;
  }

  .welcome-sub {
    margin-top: 10px;
    font-size: 15px;
    color: #6b7280;
  }
}

/*************** 统计卡片 ***************/
.stats-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  margin-bottom: 30px;
}

.stat-card {
  flex: 1;
  min-width: 260px;
  background: #ffffff;
  border-radius: 14px;
  padding: 22px 26px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
  display: flex;
  cursor: pointer;
  transition: 0.2s;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  }

  .stat-icon {
    width: 58px;
    height: 58px;
    border-radius: 12px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: white;

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

/*************** 功能模块区 ***************/
.features-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.feature-card {
  border-radius: 14px;
  padding: 24px;
  border: 1px solid #e5e7eb;

  .feature-title {
    font-size: 18px;
    font-weight: 600;
    color: #111827;
  }

  .feature-sub {
    margin-top: 6px;
    margin-bottom: 20px;
    font-size: 14px;
    color: #6b7280;
  }

  .app-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 14px;
    margin-bottom: 18px;
  }

  .app-item {
    text-align: center;
    padding: 14px;
    background: #fff;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    cursor: pointer;
    transition: 0.2s;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
    }

    .app-icon {
      width: 36px;
      height: 36px;
      margin-bottom: 10px;
    }

    .app-title {
      font-size: 14px;
      color: #111827;
    }
  }

  .feature-illustration {
    width: 100%;
    max-height: 130px;
    object-fit: contain;
    margin-bottom: 14px;
  }

  .feature-btn {
    background: #f3f4f6;
    padding: 12px 14px;
    border-radius: 10px;
    text-align: center;
    cursor: pointer;
    color: #374151;
    font-size: 14px;
  }
}

</style>
