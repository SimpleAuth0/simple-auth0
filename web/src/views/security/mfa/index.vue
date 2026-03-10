<template>
  <div class="mfa-page">

    <!-- 全局MFA开关 -->
    <el-card class="global-config-card" shadow="hover">
      <div class="global-header">
        <div>
          <div class="global-title">多因素认证 (MFA)</div>
          <div class="global-desc">为用户登录添加额外的安全验证层，保护账户安全</div>
        </div>
        <el-switch v-model="mfaEnabled" active-text="已启用" inactive-text="已禁用" @change="handleGlobalToggle" />
      </div>
    </el-card>

    <!-- MFA方式配置 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8" v-for="method in mfaMethods" :key="method.key">
        <el-card class="method-card" shadow="hover" :class="{ 'method-enabled': method.enabled }">
          <div class="method-header">
            <div class="method-icon" :style="{ background: method.color }">
              <i :class="method.icon" />
            </div>
            <el-switch v-model="method.enabled" @change="handleMethodToggle(method)" />
          </div>
          <div class="method-name">{{ method.name }}</div>
          <div class="method-desc">{{ method.description }}</div>
          <div class="method-users" v-if="method.enabled">
            <span class="users-count">{{ method.userCount }}</span> 位用户已绑定
          </div>
          <div class="method-actions" v-if="method.enabled">
            <el-button type="text" @click="configureMethod(method)">配置</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- MFA策略配置 -->
    <el-card class="policy-card" shadow="hover">
      <div slot="header">
        <span>MFA策略配置</span>
      </div>

      <el-form :model="mfaPolicy" label-width="180px" class="policy-form">
        <el-form-item label="MFA启用范围">
          <el-radio-group v-model="mfaPolicy.scope">
            <el-radio label="all">所有用户</el-radio>
            <el-radio label="admin">仅管理员</el-radio>
            <el-radio label="custom">自定义用户组</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="登录时MFA触发条件" v-if="mfaPolicy.scope !== 'all'">
          <el-checkbox-group v-model="mfaPolicy.conditions">
            <el-checkbox label="new_device">新设备登录</el-checkbox>
            <el-checkbox label="new_location">新地点登录</el-checkbox>
            <el-checkbox label="suspicious">可疑行为</el-checkbox>
            <el-checkbox label="high_value">高价值操作</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="记住设备时长">
          <el-select v-model="mfaPolicy.rememberDays">
            <el-option label="每次都需要" :value="0" />
            <el-option label="1天" :value="1" />
            <el-option label="7天" :value="7" />
            <el-option label="30天" :value="30" />
          </el-select>
          <span class="form-tip">在此时间内同设备登录无需再次验证</span>
        </el-form-item>

        <el-form-item label="验证失败锁定">
          <el-input-number v-model="mfaPolicy.maxFailures" :min="3" :max="10" />
          <span class="form-tip">次失败后锁定账户</span>
        </el-form-item>

        <el-form-item label="备用验证码数量">
          <el-input-number v-model="mfaPolicy.backupCodes" :min="5" :max="20" />
          <span class="form-tip">个一次性备用验证码</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="savePolicy">保存策略</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户MFA绑定情况 -->
    <el-card class="user-card" shadow="hover">
      <div slot="header">
        <span>用户MFA绑定情况</span>
        <el-input
          v-model="userSearch"
          placeholder="搜索用户"
          size="small"
          style="width: 200px; float: right;"
          suffix-icon="el-icon-search"
        />
      </div>

      <el-table :data="userMfaList" size="small">
        <el-table-column prop="username" label="用户名">
          <template slot-scope="{ row }">
            <div class="user-cell">
              <el-avatar :size="32" :src="row.avatar">{{ row.username.charAt(0) }}</el-avatar>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="mfaMethod" label="绑定方式">
          <template slot-scope="{ row }">
            <el-tag v-if="row.mfaMethod" :type="getMethodTag(row.mfaMethod)" size="small">
              {{ getMethodName(row.mfaMethod) }}
            </el-tag>
            <span v-else class="text-muted">未绑定</span>
          </template>
        </el-table-column>
        <el-table-column prop="bindTime" label="绑定时间" />
        <el-table-column prop="lastUsed" label="最后使用" />
        <el-table-column label="操作" width="150">
          <template slot-scope="{ row }">
            <el-button v-if="row.mfaMethod" size="mini" type="text" @click="resetMfa(row)">重置MFA</el-button>
            <el-button v-else size="mini" type="text" @click="enforceMfa(row)">强制启用</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="userTotal"
          :page-size="10"
        />
      </div>
    </el-card>

    <!-- 配置对话框 -->
    <el-dialog :title="currentMethod.name + '配置'" :visible.sync="configDialogVisible" width="500px">
      <el-form :model="methodConfig" label-width="120px">
        <el-form-item label="应用名称">
          <el-input v-model="methodConfig.issuer" placeholder="显示在验证器中的应用名称" />
        </el-form-item>
        <el-form-item label="验证码有效期">
          <el-input-number v-model="methodConfig.period" :min="30" :max="300" :step="30" />
          <span class="form-tip">秒</span>
        </el-form-item>
        <el-form-item label="验证码位数">
          <el-radio-group v-model="methodConfig.digits">
            <el-radio-button :label="6">6位</el-radio-button>
            <el-radio-button :label="8">8位</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMethodConfig">保存</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'MFA',
  data() {
    return {
      mfaEnabled: true,
      mfaMethods: [
        { key: 'totp', name: 'TOTP验证器', icon: 'el-icon-mobile-phone', color: '#409eff', enabled: true, description: '使用Google Authenticator等应用生成验证码', userCount: 45 },
        { key: 'sms', name: '短信验证码', icon: 'el-icon-message', color: '#67c23a', enabled: true, description: '通过短信接收验证码', userCount: 32 },
        { key: 'email', name: '邮件验证码', icon: 'el-icon-message-box', color: '#e6a23c', enabled: false, description: '通过邮件接收验证码', userCount: 0 }
      ],
      mfaPolicy: {
        scope: 'all',
        conditions: ['new_device', 'suspicious'],
        rememberDays: 7,
        maxFailures: 5,
        backupCodes: 10
      },
      userSearch: '',
      userMfaList: [
        { username: 'admin', email: 'admin@example.com', avatar: '', mfaMethod: 'totp', bindTime: '2023-11-01 10:00:00', lastUsed: '2023-12-01 09:30:00' },
        { username: 'zhangsan', email: 'zhangsan@example.com', avatar: '', mfaMethod: 'sms', bindTime: '2023-11-15 14:20:00', lastUsed: '2023-12-01 08:45:00' },
        { username: 'lisi', email: 'lisi@example.com', avatar: '', mfaMethod: null, bindTime: '-', lastUsed: '-' }
      ],
      userTotal: 3,
      configDialogVisible: false,
      currentMethod: {},
      methodConfig: {
        issuer: 'SimpleAuth0',
        period: 30,
        digits: 6
      }
    }
  },
  methods: {
    handleGlobalToggle(val) {
      this.$message.success(`MFA已${val ? '启用' : '禁用'}`)
    },
    handleMethodToggle(method) {
      this.$message.success(`${method.name}已${method.enabled ? '启用' : '禁用'}`)
    },
    configureMethod(method) {
      this.currentMethod = method
      this.configDialogVisible = true
    },
    saveMethodConfig() {
      this.configDialogVisible = false
      this.$message.success('配置已保存')
    },
    savePolicy() {
      this.$message.success('MFA策略已保存')
    },
    getMethodTag(method) {
      const tagMap = { totp: 'primary', sms: 'success', email: 'warning' }
      return tagMap[method] || 'info'
    },
    getMethodName(method) {
      const nameMap = { totp: 'TOTP验证器', sms: '短信验证码', email: '邮件验证码' }
      return nameMap[method] || method
    },
    resetMfa(row) {
      this.$confirm(`确定重置用户 ${row.username} 的MFA设置吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        row.mfaMethod = null
        row.bindTime = '-'
        row.lastUsed = '-'
        this.$message.success('MFA已重置')
      })
    },
    enforceMfa(row) {
      this.$confirm(`确定强制为用户 ${row.username} 启用MFA吗？`, '提示').then(() => {
        row.mfaMethod = 'totp'
        row.bindTime = new Date().toLocaleString()
        this.$message.success('已强制启用MFA')
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.mfa-page {
  padding: 20px;

  .global-config-card {
    border-radius: 10px;

    .global-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .global-title {
        font-size: 18px;
        font-weight: 600;
        margin-bottom: 8px;
      }

      .global-desc {
        color: #909399;
      }
    }
  }

  .method-card {
    border-radius: 10px;
    transition: all 0.3s;

    &.method-enabled {
      border-color: #409eff;
    }

    .method-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      .method-icon {
        width: 48px;
        height: 48px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 24px;
      }
    }

    .method-name {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 8px;
    }

    .method-desc {
      font-size: 13px;
      color: #909399;
      margin-bottom: 15px;
      min-height: 36px;
    }

    .method-users {
      padding-top: 15px;
      border-top: 1px solid #ebeef5;
      color: #606266;

      .users-count {
        font-size: 20px;
        font-weight: 600;
        color: #409eff;
      }
    }

    .method-actions {
      margin-top: 10px;
    }
  }

  .policy-card,
  .user-card {
    border-radius: 10px;
    margin-top: 20px;

    .policy-form {
      max-width: 600px;

      .form-tip {
        margin-left: 10px;
        color: #909399;
      }
    }

    .user-cell {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .text-muted {
      color: #909399;
    }

    .pagination {
      margin-top: 15px;
      text-align: right;
    }
  }
}
</style>
