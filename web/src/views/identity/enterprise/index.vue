<template>
  <div class="identity-source-page">
    <!-- 顶部标题区 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">企业身份源</h1>
        <p class="page-desc">连接和管理 LDAP、Active Directory、OIDC、SAML 等企业身份源，实现统一身份认证</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon connected">
          <i class="el-icon-link" />
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ connectedCount }}</div>
          <div class="stat-label">已连接</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon total-users">
          <i class="el-icon-user" />
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalUsers }}</div>
          <div class="stat-label">同步用户</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon last-sync">
          <i class="el-icon-refresh" />
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ lastSyncTime }}</div>
          <div class="stat-label">上次同步</div>
        </div>
      </div>
    </div>

    <!-- 筛选工具栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-input
          v-model="searchQuery"
          placeholder="搜索身份源名称"
          prefix-icon="el-icon-search"
          clearable
          style="width: 260px"
          size="medium"
        />
        <el-select v-model="filterType" placeholder="全部类型" clearable size="medium" style="width: 140px">
          <el-option label="LDAP" value="LDAP" />
          <el-option label="Active Directory" value="Active Directory" />
          <el-option label="OIDC" value="OIDC" />
          <el-option label="SAML" value="SAML" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="全部状态" clearable size="medium" style="width: 120px">
          <el-option label="已连接" value="connected" />
          <el-option label="未连接" value="disconnected" />
          <el-option label="连接失败" value="error" />
        </el-select>
      </div>
      <div class="filter-right">
        <el-radio-group v-model="viewMode" size="medium">
          <el-radio-button label="card">
            <i class="el-icon-menu" />
          </el-radio-button>
          <el-radio-button label="list">
            <i class="el-icon-s-unfold" />
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 卡片视图 -->
    <div v-if="viewMode === 'card'" class="card-grid">
      <div
        v-for="item in filteredList"
        :key="item.id"
        class="source-card"
        :class="{ 'is-connected': item.status === 'connected' }"
      >
        <!-- 卡片头部 -->
        <div class="card-header">
          <div class="source-type-icon" :class="getTypeClass(item.type)">
            <i :class="getTypeIcon(item.type)" />
          </div>
          <div class="header-actions">
            <el-dropdown trigger="click" placement="bottom-end">
              <el-button type="text" class="action-btn">
                <i class="el-icon-more" />
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="edit(item)">
                  <i class="el-icon-edit" /> 编辑配置
                </el-dropdown-item>
                <el-dropdown-item @click.native="testConnection(item)">
                  <i class="el-icon-connection" /> 测试连接
                </el-dropdown-item>
                <el-dropdown-item @click.native="sync(item)">
                  <i class="el-icon-refresh" /> 立即同步
                </el-dropdown-item>
                <el-dropdown-item divided @click.native="viewLogs(item)">
                  <i class="el-icon-document" /> 查看日志
                </el-dropdown-item>
                <el-dropdown-item class="delete-item" @click.native="remove(item)">
                  <i class="el-icon-delete" /> 删除连接
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>

        <!-- 卡片内容 -->
        <div class="card-body">
          <h3 class="source-name">{{ item.name }}</h3>
          <div class="source-type">{{ item.type }}</div>

          <div class="connection-status">
            <span class="status-dot" :class="item.status" />
            <span class="status-text">{{ getStatusText(item.status) }}</span>
            <span v-if="item.status === 'connected'" class="sync-info">
              · {{ formatTime(item.updateTime) }}
            </span>
          </div>

          <div class="sync-stats">
            <div class="stat-item">
              <div class="stat-number">{{ item.users || 0 }}</div>
              <div class="stat-desc">同步用户</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ item.groups || 0 }}</div>
              <div class="stat-desc">同步群组</div>
            </div>
          </div>
        </div>

        <!-- 卡片底部 -->
        <div class="card-footer">
          <el-button
            v-if="item.status === 'connected'"
            type="primary"
            plain
            size="small"
            @click="sync(item)"
          >
            <i class="el-icon-refresh" /> 同步
          </el-button>
          <el-button
            v-else
            type="warning"
            plain
            size="small"
            @click="reconnect(item)"
          >
            <i class="el-icon-link" /> 重新连接
          </el-button>
          <el-button type="text" size="small" @click="edit(item)">
            配置 <i class="el-icon-arrow-right" />
          </el-button>
        </div>
      </div>

      <!-- 添加新卡片 -->
      <div class="source-card add-card" @click="goCreate">
        <div class="add-content">
          <div class="add-icon">
            <i class="el-icon-plus" />
          </div>
          <div class="add-text">新建身份源连接</div>
          <div class="add-desc">支持 LDAP、AD、OIDC、SAML</div>
        </div>
      </div>
    </div>

    <!-- 列表视图 -->
    <el-card v-else class="list-view" shadow="never">
      <el-table v-loading="loading" :data="filteredList" style="width: 100%">
        <el-table-column label="身份源" min-width="250">
          <template slot-scope="{ row }">
            <div class="source-info">
              <div class="source-icon-small" :class="getTypeClass(row.type)">
                <i :class="getTypeIcon(row.type)" />
              </div>
              <div class="source-details">
                <div class="source-name">{{ row.name }}</div>
                <div class="source-type">{{ row.type }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="140">
          <template slot-scope="{ row }">
            <div class="status-badge" :class="row.status">
              <span class="status-dot-small" />
              <span>{{ getStatusText(row.status) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="同步统计" width="200">
          <template slot-scope="{ row }">
            <div class="sync-count">
              <span class="count-item">
                <i class="el-icon-user" /> {{ row.users || 0 }}
              </span>
              <span class="count-item">
                <i class="el-icon-s-custom" /> {{ row.groups || 0 }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="updateTime" label="上次同步" width="160">
          <template slot-scope="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" @click="edit(row)">编辑</el-button>
            <el-button type="text" @click="sync(row)">同步</el-button>
            <el-dropdown trigger="click" placement="bottom-end">
              <el-button type="text">更多 <i class="el-icon-arrow-down" /></el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="testConnection(row)">测试连接</el-dropdown-item>
                <el-dropdown-item @click.native="viewLogs(row)">查看日志</el-dropdown-item>
                <el-dropdown-item divided class="delete-item" @click.native="remove(row)">删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="filteredList.length"
          :page-size="10"
        />
      </div>
    </el-card>

    <!-- 空状态 -->
    <div v-if="filteredList.length === 0 && !loading" class="empty-state">
      <el-empty description="暂无企业身份源">
        <el-button type="primary" icon="el-icon-plus" @click="goCreate">
          新建连接
        </el-button>
      </el-empty>
    </div>
  </div>
</template>

<script>
export default {
  name: 'IdentitySourcePage',

  data() {
    return {
      loading: false,
      searchQuery: '',
      filterType: '',
      filterStatus: '',
      viewMode: 'card', // card | list

      list: [
        {
          id: 1,
          name: '公司 LDAP 服务器',
          type: 'LDAP',
          status: 'connected',
          users: 324,
          groups: 28,
          updateTime: '2025-02-12 14:22:30',
          description: '总部主LDAP服务器'
        },
        {
          id: 2,
          name: '总部 Active Directory',
          type: 'Active Directory',
          status: 'connected',
          users: 1124,
          groups: 156,
          updateTime: '2025-02-12 13:10:15',
          description: 'Windows域控制器'
        },
        {
          id: 3,
          name: 'Ping Identity OIDC',
          type: 'OIDC',
          status: 'error',
          users: 0,
          groups: 0,
          updateTime: '2025-02-01 09:15:00',
          description: '外部OIDC提供商'
        },
        {
          id: 4,
          name: '企业 SAML SSO',
          type: 'SAML',
          status: 'connected',
          users: 234,
          groups: 12,
          updateTime: '2025-02-10 09:30:45',
          description: '企业统一登录'
        },
        {
          id: 5,
          name: '分部 LDAP',
          type: 'LDAP',
          status: 'disconnected',
          users: 0,
          groups: 0,
          updateTime: '2025-01-15 16:20:00',
          description: '上海分部LDAP'
        }
      ]
    }
  },

  computed: {
    filteredList() {
      let result = this.list

      if (this.searchQuery) {
        result = result.filter(item =>
          item.name.toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      }

      if (this.filterType) {
        result = result.filter(item => item.type === this.filterType)
      }

      if (this.filterStatus) {
        result = result.filter(item => item.status === this.filterStatus)
      }

      return result
    },

    connectedCount() {
      return this.list.filter(item => item.status === 'connected').length
    },

    totalUsers() {
      return this.list.reduce((sum, item) => sum + (item.users || 0), 0)
    },

    lastSyncTime() {
      const connected = this.list.filter(item => item.status === 'connected')
      if (connected.length === 0) return '-'

      const latest = connected.sort((a, b) =>
        new Date(b.updateTime) - new Date(a.updateTime)
      )[0]
      return this.formatTime(latest.updateTime)
    }
  },

  methods: {
    getTypeClass(type) {
      const map = {
        'LDAP': 'ldap',
        'Active Directory': 'ad',
        'OIDC': 'oidc',
        'SAML': 'saml'
      }
      return map[type] || 'default'
    },

    getTypeIcon(type) {
      const map = {
        'LDAP': 'el-icon-s-cooperation',
        'Active Directory': 'el-icon-s-platform',
        'OIDC': 'el-icon-link',
        'SAML': 'el-icon-key'
      }
      return map[type] || 'el-icon-s-tools'
    },

    getStatusText(status) {
      const map = {
        'connected': '已连接',
        'disconnected': '未连接',
        'error': '连接失败'
      }
      return map[status] || status
    },

    formatTime(time) {
      if (!time) return '-'
      const date = new Date(time)
      const now = new Date()
      const diff = Math.floor((now - date) / 1000 / 60) // minutes

      if (diff < 1) return '刚刚'
      if (diff < 60) return `${diff}分钟前`
      if (diff < 1440) return `${Math.floor(diff / 60)}小时前`
      return `${Math.floor(diff / 1440)}天前`
    },

    goCreate() {
      this.$router.push('/identity/create')
    },

    edit(row) {
      this.$message.info(`编辑：${row.name}`)
    },

    sync(row) {
      this.$message.success(`已启动同步：${row.name}`)
      // 模拟同步完成
      setTimeout(() => {
        row.updateTime = new Date().toISOString()
        this.$message.success(`${row.name} 同步完成`)
      }, 2000)
    },

    reconnect(row) {
      this.$message.info(`正在重新连接：${row.name}`)
      setTimeout(() => {
        row.status = 'connected'
        this.$message.success(`${row.name} 连接成功`)
      }, 1500)
    },

    testConnection(row) {
      this.$message.info(`正在测试连接：${row.name}`)
      setTimeout(() => {
        if (row.status === 'connected') {
          this.$message.success('连接测试成功')
        } else {
          this.$message.error('连接测试失败，请检查配置')
        }
      }, 1000)
    },

    viewLogs(row) {
      this.$message.info(`查看 ${row.name} 的同步日志`)
    },

    remove(row) {
      this.$confirm(`确定删除 "${row.name}" 吗？删除后将无法恢复。`, '删除确认', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const index = this.list.findIndex(item => item.id === row.id)
        if (index > -1) {
          this.list.splice(index, 1)
          this.$message.success('已删除')
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.identity-source-page {
  padding: 24px 32px;
  background: #f5f7fa;
  min-height: 100vh;

  // 页面头部
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;

    .page-title {
      font-size: 28px;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0 0 8px 0;
    }

    .page-desc {
      font-size: 14px;
      color: #666;
      margin: 0;
    }
  }

  // 统计卡片
  .stats-row {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    .stat-card {
      background: #fff;
      border-radius: 12px;
      padding: 20px 24px;
      display: flex;
      align-items: center;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      border: 1px solid #e8e8e8;

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        font-size: 24px;

        &.connected {
          background: #e6f7e6;
          color: #52c41a;
        }

        &.total-users {
          background: #e6f4ff;
          color: #1890ff;
        }

        &.last-sync {
          background: #fff7e6;
          color: #faad14;
        }
      }

      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #1a1a1a;
        line-height: 1.2;
      }

      .stat-label {
        font-size: 13px;
        color: #666;
        margin-top: 4px;
      }
    }
  }

  // 筛选栏
  .filter-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 0 4px;

    .filter-left {
      display: flex;
      gap: 12px;
    }
  }

  // 卡片网格
  .card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 20px;

    .source-card {
      background: #fff;
      border-radius: 12px;
      border: 1px solid #e8e8e8;
      padding: 24px;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;

      &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        transform: translateY(-2px);
      }

      &.is-connected::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 3px;
        background: linear-gradient(90deg, #52c41a, #73d13d);
      }

      &.add-card {
        border-style: dashed;
        background: #fafafa;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 260px;

        &:hover {
          border-color: #1890ff;
          background: #e6f4ff;
        }

        .add-content {
          text-align: center;

          .add-icon {
            width: 64px;
            height: 64px;
            border-radius: 50%;
            background: #e6f4ff;
            color: #1890ff;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            margin: 0 auto 16px;
          }

          .add-text {
            font-size: 16px;
            font-weight: 500;
            color: #1a1a1a;
            margin-bottom: 8px;
          }

          .add-desc {
            font-size: 13px;
            color: #666;
          }
        }
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;

        .source-type-icon {
          width: 48px;
          height: 48px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;

          &.ldap {
            background: #fff2e8;
            color: #fa8c16;
          }

          &.ad {
            background: #e6f7ff;
            color: #1890ff;
          }

          &.oidc {
            background: #f6ffed;
            color: #52c41a;
          }

          &.saml {
            background: #f9f0ff;
            color: #722ed1;
          }

          &.default {
            background: #f5f5f5;
            color: #666;
          }
        }

        .action-btn {
          color: #999;
          font-size: 18px;
          padding: 4px;

          &:hover {
            color: #666;
          }
        }
      }

      .card-body {
        .source-name {
          font-size: 16px;
          font-weight: 600;
          color: #1a1a1a;
          margin: 0 0 4px 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .source-type {
          font-size: 13px;
          color: #666;
          margin-bottom: 12px;
        }

        .connection-status {
          display: flex;
          align-items: center;
          font-size: 13px;
          margin-bottom: 20px;

          .status-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            margin-right: 8px;

            &.connected {
              background: #52c41a;
              box-shadow: 0 0 0 3px rgba(82, 196, 26, 0.2);
            }

            &.disconnected {
              background: #bfbfbf;
            }

            &.error {
              background: #ff4d4f;
              box-shadow: 0 0 0 3px rgba(255, 77, 79, 0.2);
            }
          }

          .status-text {
            font-weight: 500;
            color: #1a1a1a;
          }

          .sync-info {
            color: #999;
            margin-left: 8px;
          }
        }

        .sync-stats {
          display: flex;
          gap: 24px;
          padding: 16px;
          background: #fafafa;
          border-radius: 8px;

          .stat-item {
            flex: 1;
            text-align: center;

            .stat-number {
              font-size: 20px;
              font-weight: 600;
              color: #1a1a1a;
            }

            .stat-desc {
              font-size: 12px;
              color: #999;
              margin-top: 4px;
            }
          }
        }
      }

      .card-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 20px;
        padding-top: 16px;
        border-top: 1px solid #f0f0f0;
      }
    }
  }

  // 列表视图
  .list-view {
    background: #fff;
    border-radius: 12px;
    border: 1px solid #e8e8e8;

    ::v-deep .el-card__body {
      padding: 0;
    }

    .source-info {
      display: flex;
      align-items: center;

      .source-icon-small {
        width: 40px;
        height: 40px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        margin-right: 12px;

        &.ldap {
          background: #fff2e8;
          color: #fa8c16;
        }

        &.ad {
          background: #e6f7ff;
          color: #1890ff;
        }

        &.oidc {
          background: #f6ffed;
          color: #52c41a;
        }

        &.saml {
          background: #f9f0ff;
          color: #722ed1;
        }
      }

      .source-name {
        font-weight: 500;
        color: #1a1a1a;
      }

      .source-type {
        font-size: 12px;
        color: #999;
      }
    }

    .status-badge {
      display: inline-flex;
      align-items: center;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 13px;
      font-weight: 500;

      &.connected {
        background: #e6f7e6;
        color: #52c41a;
      }

      &.disconnected {
        background: #f5f5f5;
        color: #999;
      }

      &.error {
        background: #fff2f0;
        color: #ff4d4f;
      }

      .status-dot-small {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        margin-right: 6px;
        background: currentColor;
      }
    }

    .sync-count {
      .count-item {
        margin-right: 16px;
        color: #666;

        i {
          margin-right: 4px;
          color: #999;
        }
      }
    }

    .table-footer {
      padding: 16px 24px;
      border-top: 1px solid #f0f0f0;
      display: flex;
      justify-content: flex-end;
    }
  }

  // 空状态
  .empty-state {
    padding: 80px 0;
    background: #fff;
    border-radius: 12px;
    border: 1px solid #e8e8e8;
  }
}

// 下拉菜单样式
.delete-item {
  color: #ff4d4f;

  &:hover {
    color: #ff7875;
  }
}

// 响应式
@media (max-width: 1200px) {
  .identity-source-page {
    .stats-row {
      grid-template-columns: repeat(3, 1fr);
    }
  }
}

@media (max-width: 768px) {
  .identity-source-page {
    padding: 16px;

    .stats-row {
      grid-template-columns: 1fr;
    }

    .card-grid {
      grid-template-columns: 1fr;
    }

    .filter-bar {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;

      .filter-left {
        flex-wrap: wrap;
      }
    }
  }
}
</style>
