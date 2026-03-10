<template>
  <div class="social-page">

    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h2 class="page-title">社交身份源管理</h2>
        <p class="page-desc">配置社交登录方式，允许用户使用第三方账号登录</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="addSocialProvider">
        添加身份源
      </el-button>
    </div>

    <!-- 社交登录提供商卡片 -->
    <el-row :gutter="20">
      <el-col :span="8" v-for="provider in providers" :key="provider.key">
        <el-card class="provider-card" shadow="hover" :class="{ 'provider-enabled': provider.enabled }">
          <div class="provider-header">
            <div class="provider-info">
              <div class="provider-icon" :style="{ background: provider.color }">
                <i :class="provider.icon"></i>
              </div>
              <div>
                <div class="provider-name">{{ provider.name }}</div>
                <div class="provider-type">{{ provider.type }}</div>
              </div>
            </div>
            <el-switch v-model="provider.enabled" @change="handleToggle(provider)" />
          </div>

          <div class="provider-stats" v-if="provider.enabled">
            <div class="stat-item">
              <span class="stat-label">今日登录</span>
              <span class="stat-value">{{ provider.todayLogins }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">累计用户</span>
              <span class="stat-value">{{ provider.totalUsers }}</span>
            </div>
          </div>

          <div class="provider-status" v-if="provider.enabled">
            <el-tag :type="provider.connected ? 'success' : 'danger'" size="small">
              {{ provider.connected ? '连接正常' : '连接异常' }}
            </el-tag>
          </div>

          <div class="provider-actions">
            <el-button type="text" @click="configureProvider(provider)">配置</el-button>
            <el-button type="text" @click="viewUsers(provider)">用户列表</el-button>
            <el-button type="text" class="danger" @click="deleteProvider(provider)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 社交登录设置 -->
    <el-card class="settings-card" shadow="hover">
      <div slot="header">
        <span>社交登录设置</span>
      </div>

      <el-form :model="socialSettings" label-width="180px">
        <el-form-item label="社交登录开关">
          <el-switch v-model="socialSettings.enabled" @change="handleSettingsChange" />
        </el-form-item>

        <el-form-item label="登录页显示方式" v-if="socialSettings.enabled">
          <el-radio-group v-model="socialSettings.displayMode">
            <el-radio label="button">图标按钮</el-radio>
            <el-radio label="list">列表样式</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="自动注册账号" v-if="socialSettings.enabled">
          <el-switch v-model="socialSettings.autoRegister" />
          <div class="form-hint">首次使用社交登录时自动创建本地账号</div>
        </el-form-item>

        <el-form-item label="绑定已有账号" v-if="socialSettings.enabled">
          <el-switch v-model="socialSettings.allowBind" />
          <div class="form-hint">允许用户将社交账号绑定到已有账号</div>
        </el-form-item>

        <el-form-item label="默认用户组" v-if="socialSettings.enabled && socialSettings.autoRegister">
          <el-select v-model="socialSettings.defaultGroup" style="width: 100%">
            <el-option label="普通用户" value="user" />
            <el-option label="访客" value="guest" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="socialSettings.enabled">
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 配置对话框 -->
    <el-dialog :title="currentProvider.name + ' 配置'" :visible.sync="configDialogVisible" width="550px">
      <el-form :model="providerConfig" label-width="140px" :rules="configRules" ref="configForm">
        <el-form-item label="Client ID" prop="clientId">
          <el-input v-model="providerConfig.clientId" placeholder="输入应用ID" />
        </el-form-item>

        <el-form-item label="Client Secret" prop="clientSecret">
          <el-input v-model="providerConfig.clientSecret" type="password" placeholder="输入应用密钥" show-password />
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
            <el-button slot="append" icon="el-icon-document-copy" @click="copyRedirectUri">复制</el-button>
          </el-input>
        </el-form-item>

        <el-divider />

        <el-form-item label="字段映射">
          <div class="field-mapping">
            <div class="mapping-item">
              <span>用户ID</span>
              <el-input v-model="providerConfig.mapping.id" size="small" placeholder="第三方字段名" />
            </div>
            <div class="mapping-item">
              <span>用户名</span>
              <el-input v-model="providerConfig.mapping.username" size="small" placeholder="第三方字段名" />
            </div>
            <div class="mapping-item">
              <span>昵称</span>
              <el-input v-model="providerConfig.mapping.nickname" size="small" placeholder="第三方字段名" />
            </div>
            <div class="mapping-item">
              <span>邮箱</span>
              <el-input v-model="providerConfig.mapping.email" size="small" placeholder="第三方字段名" />
            </div>
            <div class="mapping-item">
              <span>头像</span>
              <el-input v-model="providerConfig.mapping.avatar" size="small" placeholder="第三方字段名" />
            </div>
          </div>
        </el-form-item>
      </el-form>

      <div slot="footer">
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="testConnection">测试连接</el-button>
        <el-button type="primary" @click="saveConfig">保存</el-button>
      </div>
    </el-dialog>

    <!-- 添加身份源对话框 -->
    <el-dialog title="添加社交身份源" :visible.sync="addDialogVisible" width="500px">
      <el-row :gutter="20">
        <el-col :span="8" v-for="provider in availableProviders" :key="provider.key">
          <div class="provider-option" @click="selectProvider(provider)">
            <div class="option-icon" :style="{ background: provider.color }">
              <i :class="provider.icon"></i>
            </div>
            <div class="option-name">{{ provider.name }}</div>
          </div>
        </el-col>
      </el-row>
    </el-dialog>

    <!-- 用户列表对话框 -->
    <el-dialog :title="currentProvider.name + ' 绑定用户'" :visible.sync="usersDialogVisible" width="700px">
      <el-table :data="boundUsers" size="small">
        <el-table-column prop="username" label="本地用户名" />
        <el-table-column prop="socialId" label="社交账号ID" />
        <el-table-column prop="socialName" label="社交账号名称" />
        <el-table-column prop="bindTime" label="绑定时间" />
        <el-table-column label="操作" width="100">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" class="danger" @click="unbindUser(row)">解绑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background layout="prev, pager, next" :total="boundUsersTotal" :page-size="10" />
      </div>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'SocialIdentity',
  data() {
    return {
      providers: [
        {
          key: 'wechat',
          name: '微信',
          type: 'OAuth2',
          icon: 'el-icon-chat-dot-round',
          color: '#07c160',
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
          enabled: false,
          connected: false,
          todayLogins: 0,
          totalUsers: 0
        }
      ],
      socialSettings: {
        enabled: true,
        displayMode: 'button',
        autoRegister: true,
        allowBind: true,
        defaultGroup: 'user'
      },
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
        }
      },
      configRules: {
        clientId: [{ required: true, message: '请输入Client ID', trigger: 'blur' }],
        clientSecret: [{ required: true, message: '请输入Client Secret', trigger: 'blur' }]
      },
      availableProviders: [
        { key: 'dingtalk', name: '钉钉', icon: 'el-icon-bell', color: '#3370ff' },
        { key: 'feishu', name: '飞书', icon: 'el-icon-document', color: '#3370ff' },
        { key: 'qq', name: 'QQ', icon: 'el-icon-chat-square', color: '#12b7f5' },
        { key: 'google', name: 'Google', icon: 'el-icon-search', color: '#4285f4' }
      ],
      boundUsers: [
        { username: 'zhangsan', socialId: '12345678', socialName: '张三', bindTime: '2023-11-01 10:00:00' },
        { username: 'lisi', socialId: '87654321', socialName: '李四', bindTime: '2023-11-05 14:30:00' }
      ],
      boundUsersTotal: 2
    }
  },
  methods: {
    handleToggle(provider) {
      const status = provider.enabled ? '启用' : '禁用'
      this.$message.success(`${provider.name}已${status}`)
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
      this.$confirm(`确定删除 ${provider.name} 身份源吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        const index = this.providers.findIndex(p => p.key === provider.key)
        if (index > -1) {
          this.providers.splice(index, 1)
          this.$message.success('删除成功')
        }
      })
    },
    addSocialProvider() {
      this.addDialogVisible = true
    },
    selectProvider(provider) {
      this.addDialogVisible = false
      const newProvider = {
        ...provider,
        type: 'OAuth2',
        enabled: false,
        connected: false,
        todayLogins: 0,
        totalUsers: 0
      }
      this.providers.push(newProvider)
      this.$message.success(`已添加 ${provider.name}，请进行配置`)
      this.configureProvider(newProvider)
    },
    handleSettingsChange(val) {
      this.$message.success(`社交登录已${val ? '启用' : '禁用'}`)
    },
    saveSettings() {
      this.$message.success('设置已保存')
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
.social-page {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .page-title {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 8px;
    }

    .page-desc {
      color: #909399;
    }
  }

  .provider-card {
    border-radius: 10px;
    margin-bottom: 20px;

    &.provider-enabled {
      border-color: #67c23a;
    }

    .provider-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      .provider-info {
        display: flex;
        align-items: center;

        .provider-icon {
          width: 48px;
          height: 48px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
          font-size: 24px;
          margin-right: 12px;
        }

        .provider-name {
          font-size: 16px;
          font-weight: 600;
        }

        .provider-type {
          font-size: 12px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }

    .provider-stats {
      display: flex;
      gap: 20px;
      padding: 15px 0;
      border-top: 1px solid #ebeef5;
      margin-bottom: 10px;

      .stat-item {
        display: flex;
        flex-direction: column;

        .stat-label {
          font-size: 12px;
          color: #909399;
        }

        .stat-value {
          font-size: 18px;
          font-weight: 600;
          color: #303133;
          margin-top: 4px;
        }
      }
    }

    .provider-status {
      margin-bottom: 10px;
    }

    .provider-actions {
      display: flex;
      gap: 15px;
      padding-top: 10px;
      border-top: 1px solid #ebeef5;
    }
  }

  .settings-card {
    border-radius: 10px;
    margin-top: 20px;

    .form-hint {
      font-size: 12px;
      color: #909399;
      margin-top: 5px;
    }
  }

  .provider-option {
    text-align: center;
    padding: 20px;
    border: 1px solid #ebeef5;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.3s;
    margin-bottom: 20px;

    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    }

    .option-icon {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 24px;
      margin: 0 auto 10px;
    }

    .option-name {
      font-size: 14px;
    }
  }

  .field-mapping {
    .mapping-item {
      display: flex;
      align-items: center;
      margin-bottom: 10px;

      span {
        width: 80px;
        color: #606266;
      }

      .el-input {
        flex: 1;
      }
    }
  }

  .pagination {
    margin-top: 15px;
    text-align: right;
  }

  .danger {
    color: #f56c6c;
  }
}
</style>
