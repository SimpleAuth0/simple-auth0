<template>
  <div class="app-detail-page">

    <!-- 顶部基础信息栏 -->
    <div class="app-header">
      <el-button
        type="text"
        icon="el-icon-arrow-left"
        class="back-btn"
        @click="$router.back()"
      >返回</el-button>

      <div class="app-meta">
        <div class="app-name">{{ app.name }}</div>
        <el-tag size="mini">{{ app.type }}</el-tag>
        <div class="app-id">Client ID：{{ app.clientId }}</div>
      </div>
    </div>

    <!-- 新建引导 Banner -->
    <el-alert
      v-if="isFromCreate && showGuide"
      type="success"
      show-icon
      closable
      class="guide-banner"
      title="应用已创建成功"
      description="请完成协议配置、API 授权和角色分配，以便应用可以正常使用。"
      @close="showGuide = false"
    />

    <!-- Tabs -->
    <el-tabs v-model="activeTab" class="detail-tabs">

      <!-- 概览 -->
      <el-tab-pane label="概览" name="overview">
        <div class="overview">

          <el-card class="overview-card">
            <div class="card-title">配置进度</div>

            <ul class="checklist">
              <li
                v-for="item in checklist"
                :key="item.key"
                :class="{ done: item.done }"
                @click="goTo(item.tab)"
              >
                <i
                  :class="item.done ? 'el-icon-check' : 'el-icon-warning-outline'"
                />
                {{ item.label }}
              </li>
            </ul>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 协议 -->
      <el-tab-pane label="协议" name="protocol">
        <el-card>协议配置区域（OIDC / OAuth2）</el-card>
      </el-tab-pane>

      <!-- API 授权 -->
      <el-tab-pane label="API 授权" name="api">
        <el-card>API 授权配置区域</el-card>
      </el-tab-pane>

      <!-- 角色 -->
      <el-tab-pane label="角色" name="role">
        <el-card>角色分配区域</el-card>
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script>
export default {
  name: 'AppDetail',

  data() {
    return {
      activeTab: 'overview',
      showGuide: true,

      // 模拟应用数据
      app: {
        name: '单页应用程序',
        type: 'SPA',
        clientId: 'spa_xxx_123',
        protocolConfigured: false,
        apiAuthorized: false,
        roleAssigned: false
      }
    }
  },

  computed: {
    isFromCreate() {
      return this.$route.query.from === 'create'
    },

    checklist() {
      return [
        {
          key: 'protocol',
          label: '配置协议',
          done: this.app.protocolConfigured,
          tab: 'protocol'
        },
        {
          key: 'api',
          label: '授权 API',
          done: this.app.apiAuthorized,
          tab: 'api'
        },
        {
          key: 'role',
          label: '分配角色',
          done: this.app.roleAssigned,
          tab: 'role'
        }
      ]
    }
  },

  created() {
    if (this.isFromCreate) {
      this.activeTab = 'protocol'
    }
  },

  methods: {
    goTo(tab) {
      this.activeTab = tab
    }
  }
}
</script>

<style scoped lang="scss">
.app-detail-page {
  padding: 24px 32px;
}

.app-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;

  .back-btn {
    margin-right: 16px;
  }

  .app-meta {
    .app-name {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 4px;
    }

    .app-id {
      font-size: 13px;
      color: #888;
    }
  }
}

.guide-banner {
  margin-bottom: 16px;
}

.detail-tabs {
  background: #fff;
}

.overview {
  padding: 8px 0;

  .overview-card {
    max-width: 480px;
  }

  .card-title {
    font-weight: 600;
    margin-bottom: 12px;
  }

  .checklist {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      display: flex;
      align-items: center;
      padding: 8px 0;
      cursor: pointer;
      color: #333;

      i {
        margin-right: 8px;
      }

      &.done {
        color: #67c23a;
      }
    }
  }
}
</style>
