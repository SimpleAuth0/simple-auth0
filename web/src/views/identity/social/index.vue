<template>
  <div class="social-identity-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">社交身份源</h1>
          <p class="page-desc">配置社交登录方式，允许用户使用微信、钉钉等第三方账号登录</p>
        </div>
        <div class="header-actions">
          <el-button icon="el-icon-refresh" @click="refreshData">刷新</el-button>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon enabled">
            <i class="el-icon-check" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ enabledCount }}</div>
            <div class="stat-label">已启用</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon users">
            <i class="el-icon-user" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalUsers }}</div>
            <div class="stat-label">绑定用户</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon logins">
            <i class="el-icon-data-line" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ todayLogins }}</div>
            <div class="stat-label">今日登录</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon rate">
            <i class="el-icon-trend-charts" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ socialLoginRate }}%</div>
            <div class="stat-label">社交登录占比</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 身份源卡片列表 -->
    <div class="providers-section">
      <div class="section-header">
        <span class="section-title">已配置身份源</span>
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="grid">
            <i class="el-icon-menu" />
          </el-radio-button>
          <el-radio-button label="list">
            <i class="el-icon-s-unfold" />
          </el-radio-button>
        </el-radio-group>
      </div>

      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="providers-grid">
        <div
          v-for="provider in providers"
          :key="provider.key"
          class="provider-card"
          :class="{ 'is-enabled': provider.enabled, 'is-error': provider.enabled && !provider.connected }"
        >
          <div class="card-header">
            <div class="provider-icon" :style="{ background: provider.gradient || provider.color }">
              <i :class="provider.icon" />
            </div>
            <div class="provider-status">
              <el-switch
                v-model="provider.enabled"
                active-text="启用"
                @change="handleToggle(provider)"
              />
            </div>
          </div>

          <div class="card-body">
            <h3 class="provider-name">{{ provider.name }}</h3>
            <p class="provider-type">{{ provider.type }}</p>

            <div v-if="provider.enabled" class="connection-status">
              <span class="status-dot" :class="provider.connected ? 'connected' : 'error'" />
              <span class="status-text">{{ provider.connected ? '连接正常' : '连接异常' }}</span>
            </div>

            <div v-if="provider.enabled" class="provider-stats">
              <div class="stat-item">
                <span class="stat-num">{{ provider.totalUsers }}</span>
                <span class="stat-label">绑定用户</span>
              </div>
              <div class="stat-item">
                <span class="stat-num">{{ provider.todayLogins }}</span>
                <span class="stat-label">今日登录</span>
              </div>
            </div>
          </div>

          <div class="card-footer">
            <el-button
              v-if="provider.enabled"
              type="text"
              icon="el-icon-setting"
              @click="configureProvider(provider)"
            >
              配置
            </el-button>
            <el-button
              v-if="provider.enabled"
              type="text"
              icon="el-icon-user"
              @click="viewUsers(provider)"
            >
              用户
            </el-button>
            <el-popconfirm
              title="确定删除该身份源吗？"
              @confirm="deleteProvider(provider)"
            >
              <el-button
                slot="reference"
                type="text"
                class="danger"
                icon="el-icon-delete"
              >
                删除
              </el-button>
            </el-popconfirm>
          </div>
        </div>

        <!-- 添加卡片 -->
        <div class="provider-card add-card" @click="addSocialProvider">
          <div class="add-content">
            <div class="add-icon">
              <i class="el-icon-plus" />
            </div>
            <div class="add-text">添加身份源</div>
            <div class="add-desc">支持微信、钉钉、GitHub等</div>
          </div>
        </div>
      </div>

      <!-- 列表视图 -->
      <el-card v-else class="list-view" shadow="never">
        <el-table :data="providers" style="width: 100%">
          <el-table-column label="身份源" min-width="200">
            <template slot-scope="{ row }">
              <div class="provider-info">
                <div class="provider-icon-small" :style="{ background: row.color }">
                  <i :class="row.icon" />
                </div>
                <div class="provider-details">
                  <div class="provider-name">{{ row.name }}</div>
                  <div class="provider-type">{{ row.type }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="状态" width="120">
            <template slot-scope="{ row }">
              <el-switch v-model="row.enabled" @change="handleToggle(row)" />
            </template>
          </el-table-column>

          <el-table-column label="连接状态" width="120">
            <template slot-scope="{ row }">
              <div v-if="row.enabled" class="status-badge" :class="row.connected ? 'connected' : 'error'">
                <span class="status-dot-small" />
                {{ row.connected ? '正常' : '异常' }}
              </div>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>

          <el-table-column label="绑定用户" width="100">
            <template slot-scope="{ row }">
              <span v-if="row.enabled">{{ row.totalUsers }}</span>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>

          <el-table-column label="今日登录" width="100">
            <template slot-scope="{ row }">
              <span v-if="row.enabled">{{ row.todayLogins }}</span>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="{ row }">
              <el-button
                v-if="row.enabled"
                type="text"
                icon="el-icon-setting"
                @click="configureProvider(row)"
              >
                配置
              </el-button>
              <el-button
                v-if="row.enabled"
                type="text"
                icon="el-icon-user"
                @click="viewUsers(row)"
              >
                用户
              </el-button>
              <el-popconfirm
                title="确定删除该身份源吗？"
                @confirm="deleteProvider(row)"
              >
                <el-button slot="reference" type="text" class="danger" icon="el-icon-delete">
                  删除
                </el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 配置对话框 -->
    <el-dialog
      :title="currentProvider.name + ' 配置'"
      :visible.sync="configDialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="configForm"
        :model="providerConfig"
        label-width="140px"
        :rules="configRules"
        class="config-form"
      >
        <el-divider content-position="left">基础配置</el-divider>

        <el-form-item label="Client ID" prop="clientId">
          <el-input v-model="providerConfig.clientId" placeholder="输入应用ID" />
        </el-form-item>

        <el-form-item label="Client Secret" prop="clientSecret">
          <el-input
            v-model="providerConfig.clientSecret"
            type="password"
            placeholder="输入应用密钥"
            show-password
          />
        </el-form-item>

        <el-form-item label="授权范围">
          <el-select v-model="providerConfig.scope" multiple style="width: 100%">
            <el-option label="基本信息" value="profile" />
            <el-option label="邮箱" value="email" />
            <el-option label="手机号" value="phone" />
          </el-select>
        </el-form-item>

        <el-form-item label="回调地址">
          <el-input v-model="providerConfig.redirectUri" readonly>
            <el-button slot="append" icon="el-icon-document-copy" @click="copyRedirectUri">
              复制
            </el-button>
          </el-input>
          <div class="form-hint">请在您的IdP中将此地址配置为允许的回调地址</div>
        </el-form-item>

        <el-divider content-position="left">字段映射</el-divider>

        <div class="field-mapping">
          <div v-for="(value, key) in providerConfig.mapping" :key="key" class="mapping-item">
            <span class="mapping-label">{{ fieldLabels[key] }}</span>
            <el-input v-model="providerConfig.mapping[key]" size="small" placeholder="第三方字段名" />
          </div>
        </div>

        <el-divider content-position="left">登录设置</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="自动注册账号">
              <el-switch v-model="providerConfig.autoRegister" />
              <div class="form-hint">首次使用社交登录时自动创建本地账号</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="绑定已有账号">
              <el-switch v-model="providerConfig.allowBind" />
              <div class="form-hint">允许用户将社交账号绑定到已有账号</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item v-if="providerConfig.autoRegister" label="默认用户组">
          <el-select v-model="providerConfig.defaultGroup" style="width: 100%">
            <el-option label="普通用户" value="user" />
            <el-option label="访客" value="guest" />
          </el-select>
        </el-form-item>
      </el-form>

      <div slot="footer">
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" icon="el-icon-connection" @click="testConnection">
          测试连接
        </el-button>
        <el-button type="primary" icon="el-icon-check" @click="saveConfig">保存</el-button>
      </div>
    </el-dialog>

    <!-- 添加身份源对话框 -->
    <el-dialog title="添加社交身份源" :visible.sync="addDialogVisible" width="600px">
      <div class="provider-selector">
        <div
          v-for="provider in availableProviders"
          :key="provider.key"
          class="provider-option"
          @click="selectProvider(provider)"
        >
          <div class="option-icon" :style="{ background: provider.gradient || provider.color }">
            <i :class="provider.icon" />
          </div>
          <div class="option-name">{{ provider.name }}</div>
          <div class="option-type">{{ provider.type }}</div>
        </div>
      </div>
    </el-dialog>

    <!-- 用户列表对话框 -->
    <el-dialog
      :title="currentProvider.name + ' 绑定用户'"
      :visible.sync="usersDialogVisible"
      width="800px"
    >
      <el-table :data="boundUsers" size="small">
        <el-table-column prop="username" label="本地用户名">
          <template slot-scope="{ row }">
            <div class="user-cell">
              <el-avatar :size="28" :src="row.avatar">{{ row.username.charAt(0) }}</el-avatar>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="socialId" label="社交账号ID" />
        <el-table-column prop="socialName" label="社交账号名称" />
        <el-table-column prop="bindTime" label="绑定时间" />
        <el-table-column label="操作" width="100">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" class="danger" @click="unbindUser(row)">
              解绑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="boundUsersTotal"
          :page-size="10"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'SocialIdentity',
  data() {
    return {
      viewMode: 'grid',
      providers: [
        {
          key: 'wechat',
          name: '微信',
          type: 'OAuth2',
          icon: 'el-icon-chat-dot-round',
          color: '#07c160',
          gradient: 'linear-gradient(135deg, #07c160 0%, #10b981 100%)',
          enabled: true,
          connected: true,
          todayLogins: 128,
          totalUsers: 523
        },
        {
          key: 'weibo',
          name: '微博',
          type: 'OAuth2',
          icon: 'el-icon-sunny',
          color: '#e6162d',
          gradient: 'linear-gradient(135deg, #e6162d 0%, #ef4444 100%)',
          enabled: true,
          connected: true,
          todayLogins: 45,
          totalUsers: 189
        },
        {
          key: 'github',
          name: 'GitHub',
          type: 'OAuth2',
          icon: 'el-icon-cpu',
          color: '#24292e',
          gradient: 'linear-gradient(135deg, #24292e 0%, #374151 100%)',
          enabled: false,
          connected: false,
          todayLogins: 0,
          totalUsers: 0
        }
      ],
      configDialogVisible: false,
      addDialogVisible: false,
      usersDialogVisible: false,
      currentProvider: {},
      providerConfig: {
        clientId: '',
        clientSecret: '',
        scope: ['profile', 'email'],
        redirectUri: '',
        mapping: {
          id: 'id',
          username: 'login',
          nickname: 'name',
          email: 'email',
          avatar: 'avatar_url'
        },
        autoRegister: true,
        allowBind: true,
        defaultGroup: 'user'
      },
      configRules: {
        clientId: [{ required: true, message: '请输入Client ID', trigger: 'blur' }],
        clientSecret: [{ required: true, message: '请输入Client Secret', trigger: 'blur' }]
      },
      fieldLabels: {
        id: '用户ID',
        username: '用户名',
        nickname: '昵称',
        email: '邮箱',
        avatar: '头像'
      },
      availableProviders: [
        { key: 'dingtalk', name: '钉钉', icon: 'el-icon-bell', color: '#3370ff', type: 'OAuth2', gradient: 'linear-gradient(135deg, #3370ff 0%, #3b82f6 100%)' },
        { key: 'feishu', name: '飞书', icon: 'el-icon-document', color: '#00d6b9', type: 'OAuth2', gradient: 'linear-gradient(135deg, #00d6b9 0%, #14b8a6 100%)' },
        { key: 'qq', name: 'QQ', icon: 'el-icon-chat-square', color: '#12b7f5', type: 'OAuth2', gradient: 'linear-gradient(135deg, #12b7f5 0%, #0ea5e9 100%)' },
        { key: 'google', name: 'Google', icon: 'el-icon-search', color: '#4285f4', type: 'OAuth2', gradient: 'linear-gradient(135deg, #4285f4 0%, #3b82f6 100%)' }
      ],
      boundUsers: [
        { username: 'zhangsan', socialId: '12345678', socialName: '张三', bindTime: '2023-11-01 10:00:00', avatar: '' },
        { username: 'lisi', socialId: '87654321', socialName: '李四', bindTime: '2023-11-05 14:30:00', avatar: '' }
      ],
      boundUsersTotal: 2,
      socialLoginRate: 35
    }
  },
  computed: {
    enabledCount() {
      return this.providers.filter(p => p.enabled).length
    },
    totalUsers() {
      return this.providers.reduce((sum, p) => sum + (p.totalUsers || 0), 0)
    },
    todayLogins() {
      return this.providers.reduce((sum, p) => sum + (p.todayLogins || 0), 0)
    }
  },
  methods: {
    refreshData() {
      this.$message.success('数据已刷新')
    },
    handleToggle(provider) {
      const status = provider.enabled ? '启用' : '禁用'
      this.$message.success(`${provider.name}已${status}`)
    },
    handleSettingsChange(val) {
      this.$message.success(`社交登录已${val ? '启用' : '禁用'}`)
    },
    saveSettings() {
      this.$message.success('设置已保存')
    },
    configureProvider(provider) {
      this.currentProvider = provider
      this.providerConfig = {
        clientId: provider.clientId || '',
        clientSecret: provider.clientSecret || '',
        scope: ['profile', 'email'],
        redirectUri: `${window.location.origin}/api/oauth/callback/${provider.key}`,
        mapping: {
          id: 'id',
          username: 'login',
          nickname: 'name',
          email: 'email',
          avatar: 'avatar_url'
        }
      }
      this.configDialogVisible = true
    },
    viewUsers(provider) {
      this.currentProvider = provider
      this.usersDialogVisible = true
    },
    deleteProvider(provider) {
      const index = this.providers.findIndex(p => p.key === provider.key)
      if (index > -1) {
        this.providers.splice(index, 1)
        this.$message.success('删除成功')
      }
    },
    addSocialProvider() {
      this.addDialogVisible = true
    },
    selectProvider(provider) {
      this.addDialogVisible = false
      const newProvider = {
        ...provider,
        enabled: false,
        connected: false,
        todayLogins: 0,
        totalUsers: 0
      }
      this.providers.push(newProvider)
      this.$message.success(`已添加 ${provider.name}，请进行配置`)
      this.configureProvider(newProvider)
    },
    copyRedirectUri() {
      this.$copyText(this.providerConfig.redirectUri).then(() => {
        this.$message.success('已复制到剪贴板')
      })
    },
    testConnection() {
      this.$message.success('连接测试成功')
    },
    saveConfig() {
      this.$refs.configForm.validate(valid => {
        if (valid) {
          this.configDialogVisible = false
          this.$message.success('配置已保存')
        }
      })
    },
    unbindUser(row) {
      this.$confirm(`确定解绑用户 ${row.username} 吗？`, '提示').then(() => {
        const index = this.boundUsers.findIndex(u => u.username === row.username)
        if (index > -1) {
          this.boundUsers.splice(index, 1)
          this.$message.success('解绑成功')
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.social-identity-page {
  padding: 24px;

  // 页面头部
  .page-header {
    margin-bottom: 24px;

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24px;

      .title-section {
        .page-title {
          font-size: 24px;
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

      .header-actions {
        display: flex;
        gap: 12px;
      }
    }

    // 统计卡片
    .stats-row {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;

      .stat-card {
        background: #fff;
        border-radius: 12px;
        padding: 20px;
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

          &.enabled {
            background: #e6f7e6;
            color: #52c41a;
          }

          &.users {
            background: #e6f4ff;
            color: #1890ff;
          }

          &.logins {
            background: #fff7e6;
            color: #faad14;
          }

          &.rate {
            background: #f6ffed;
            color: #73d13d;
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
  }

  // 身份源列表
  .providers-section {
    margin-bottom: 24px;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: #1a1a1a;
      }
    }

    .providers-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 16px;

      .provider-card {
        background: #fff;
        border-radius: 12px;
        border: 1px solid #e8e8e8;
        padding: 20px;
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        }

        &.is-enabled {
          border-color: #d9f7be;
        }

        &.is-error {
          border-color: #ffccc7;
        }

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 16px;

          .provider-icon {
            width: 56px;
            height: 56px;
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 28px;
          }
        }

        .card-body {
          .provider-name {
            font-size: 16px;
            font-weight: 600;
            color: #1a1a1a;
            margin: 0 0 4px 0;
          }

          .provider-type {
            font-size: 13px;
            color: #999;
            margin-bottom: 12px;
          }

          .connection-status {
            display: flex;
            align-items: center;
            font-size: 13px;
            margin-bottom: 16px;

            .status-dot {
              width: 8px;
              height: 8px;
              border-radius: 50%;
              margin-right: 8px;

              &.connected {
                background: #52c41a;
              }

              &.error {
                background: #ff4d4f;
              }
            }

            .status-text {
              color: #666;
            }
          }

          .provider-stats {
            display: flex;
            gap: 24px;
            padding: 12px;
            background: #f5f5f5;
            border-radius: 8px;

            .stat-item {
              display: flex;
              flex-direction: column;

              .stat-num {
                font-size: 18px;
                font-weight: 600;
                color: #1a1a1a;
              }

              .stat-label {
                font-size: 12px;
                color: #999;
                margin-top: 2px;
              }
            }
          }
        }

        .card-footer {
          display: flex;
          gap: 16px;
          margin-top: 16px;
          padding-top: 16px;
          border-top: 1px solid #f0f0f0;
        }

        &.add-card {
          border-style: dashed;
          background: #fafafa;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          min-height: 200px;

          &:hover {
            border-color: #1890ff;
            background: #e6f4ff;
          }

          .add-content {
            text-align: center;

            .add-icon {
              width: 56px;
              height: 56px;
              border-radius: 50%;
              background: #e6f4ff;
              color: #1890ff;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 24px;
              margin: 0 auto 12px;
            }

            .add-text {
              font-size: 15px;
              font-weight: 500;
              color: #1a1a1a;
              margin-bottom: 4px;
            }

            .add-desc {
              font-size: 13px;
              color: #999;
            }
          }
        }
      }
    }

    .list-view {
      border-radius: 12px;

      ::v-deep .el-card__body {
        padding: 0;
      }

      .provider-info {
        display: flex;
        align-items: center;

        .provider-icon-small {
          width: 40px;
          height: 40px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
          font-size: 18px;
          margin-right: 12px;
        }

        .provider-name {
          font-weight: 500;
          color: #1a1a1a;
        }

        .provider-type {
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
    }
  }

  // 配置表单
  .config-form {
    .field-mapping {
      .mapping-item {
        display: flex;
        align-items: center;
        margin-bottom: 12px;

        .mapping-label {
          width: 100px;
          color: #606266;
          flex-shrink: 0;
        }

        .el-input {
          flex: 1;
        }
      }
    }

    .form-hint {
      font-size: 12px;
      color: #999;
      margin-top: 4px;
    }
  }

  // 选择器
  .provider-selector {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;

    .provider-option {
      text-align: center;
      padding: 24px;
      border: 1px solid #e8e8e8;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: #1890ff;
        box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
      }

      .option-icon {
        width: 56px;
        height: 56px;
        border-radius: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 28px;
        margin: 0 auto 12px;
      }

      .option-name {
        font-size: 15px;
        font-weight: 500;
        color: #1a1a1a;
        margin-bottom: 4px;
      }

      .option-type {
        font-size: 12px;
        color: #999;
      }
    }
  }

  // 用户列表
  .user-cell {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .pagination {
    margin-top: 16px;
    text-align: right;
  }

  .danger {
    color: #ff4d4f;
  }

  .text-muted {
    color: #999;
  }
}

// 响应式
@media (max-width: 1200px) {
  .social-identity-page {
    .page-header {
      .stats-row {
        grid-template-columns: repeat(2, 1fr);
      }
    }

    .providers-section {
      .providers-grid {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
}

@media (max-width: 768px) {
  .social-identity-page {
    padding: 16px;

    .page-header {
      .stats-row {
        grid-template-columns: 1fr;
      }
    }

    .providers-section {
      .providers-grid {
        grid-template-columns: 1fr;
      }
    }

    .provider-selector {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}
</style>
