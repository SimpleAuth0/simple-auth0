<template>
  <div class="security-policy-page">

    <!-- 密码策略 -->
    <el-card class="policy-section" shadow="hover">
      <div slot="header">
        <span>密码策略</span>
        <el-switch v-model="policies.password.enabled" active-text="启用" style="float: right;" @change="savePolicy('password')" />
      </div>

      <el-form :model="policies.password.config" label-width="180px" :disabled="!policies.password.enabled">
        <el-form-item label="最小密码长度">
          <el-slider v-model="policies.password.config.minLength" :min="6" :max="32" show-input />
        </el-form-item>

        <el-form-item label="密码复杂度要求">
          <el-checkbox-group v-model="policies.password.config.complexity">
            <el-checkbox label="uppercase">包含大写字母</el-checkbox>
            <el-checkbox label="lowercase">包含小写字母</el-checkbox>
            <el-checkbox label="number">包含数字</el-checkbox>
            <el-checkbox label="special">包含特殊字符</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="密码过期时间">
          <el-select v-model="policies.password.config.expiryDays">
            <el-option label="永不过期" :value="0" />
            <el-option label="30天" :value="30" />
            <el-option label="60天" :value="60" />
            <el-option label="90天" :value="90" />
            <el-option label="180天" :value="180" />
          </el-select>
        </el-form-item>

        <el-form-item label="历史密码检查">
          <el-input-number v-model="policies.password.config.historyCount" :min="0" :max="24" />
          <span class="form-tip">不能重复使用最近N次密码</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="savePolicy('password')">保存密码策略</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 登录策略 -->
    <el-card class="policy-section" shadow="hover">
      <div slot="header">
        <span>登录策略</span>
        <el-switch v-model="policies.login.enabled" active-text="启用" style="float: right;" @change="savePolicy('login')" />
      </div>

      <el-form :model="policies.login.config" label-width="180px" :disabled="!policies.login.enabled">
        <el-form-item label="最大登录失败次数">
          <el-input-number v-model="policies.login.config.maxFailures" :min="3" :max="10" />
          <span class="form-tip">次失败后锁定账户</span>
        </el-form-item>

        <el-form-item label="账户锁定时间">
          <el-select v-model="policies.login.config.lockDuration">
            <el-option label="5分钟" :value="5" />
            <el-option label="15分钟" :value="15" />
            <el-option label="30分钟" :value="30" />
            <el-option label="1小时" :value="60" />
            <el-option label="永久锁定（需管理员解锁）" :value="-1" />
          </el-select>
        </el-form-item>

        <el-form-item label="会话超时时间">
          <el-select v-model="policies.login.config.sessionTimeout">
            <el-option label="15分钟" :value="15" />
            <el-option label="30分钟" :value="30" />
            <el-option label="1小时" :value="60" />
            <el-option label="2小时" :value="120" />
            <el-option label="4小时" :value="240" />
            <el-option label="8小时" :value="480" />
          </el-select>
        </el-form-item>

        <el-form-item label="并发登录限制">
          <el-radio-group v-model="policies.login.config.concurrentLogin">
            <el-radio label="single">单点登录（只允许一个设备）</el-radio>
            <el-radio label="multiple">允许多设备同时登录</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="IP白名单">
          <el-switch v-model="policies.login.config.ipWhitelistEnabled" />
        </el-form-item>

        <el-form-item label="允许的IP范围" v-if="policies.login.config.ipWhitelistEnabled">
          <el-input
            v-model="policies.login.config.ipWhitelist"
            type="textarea"
            :rows="3"
            placeholder="每行输入一个IP或CIDR，例如：&#10;192.168.1.0/24&#10;10.0.0.1"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="savePolicy('login')">保存登录策略</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 审计策略 -->
    <el-card class="policy-section" shadow="hover">
      <div slot="header">
        <span>审计策略</span>
        <el-switch v-model="policies.audit.enabled" active-text="启用" style="float: right;" @change="savePolicy('audit')" />
      </div>

      <el-form :model="policies.audit.config" label-width="180px" :disabled="!policies.audit.enabled">
        <el-form-item label="记录的操作类型">
          <el-checkbox-group v-model="policies.audit.config.events">
            <el-checkbox label="login">登录/登出</el-checkbox>
            <el-checkbox label="password">密码修改</el-checkbox>
            <el-checkbox label="user">用户管理</el-checkbox>
            <el-checkbox label="role">角色管理</el-checkbox>
            <el-checkbox label="permission">权限变更</el-checkbox>
            <el-checkbox label="config">系统配置</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="日志保留时间">
          <el-select v-model="policies.audit.config.retentionDays">
            <el-option label="30天" :value="30" />
            <el-option label="90天" :value="90" />
            <el-option label="180天" :value="180" />
            <el-option label="1年" :value="365" />
            <el-option label="永久保留" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="异常告警">
          <el-switch v-model="policies.audit.config.alertEnabled" />
        </el-form-item>

        <el-form-item label="告警条件" v-if="policies.audit.config.alertEnabled">
          <div class="alert-conditions">
            <div class="condition-item">
              <span>连续登录失败超过</span>
              <el-input-number v-model="policies.audit.config.alertLoginFailures" :min="3" :max="10" size="small" />
              <span>次</span>
            </div>
            <div class="condition-item">
              <span>单IP请求超过</span>
              <el-input-number v-model="policies.audit.config.alertRequestsPerMinute" :min="100" :max="10000" :step="100" size="small" />
              <span>次/分钟</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="savePolicy('audit')">保存审计策略</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 账户安全概览 -->
    <el-card class="security-overview" shadow="hover">
      <div slot="header">
        <span>账户安全概览</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshOverview">刷新</el-button>
      </div>

      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value text-danger">{{ overview.weakPasswordCount }}</div>
            <div class="stat-label">弱密码用户</div>
            <el-button v-if="overview.weakPasswordCount > 0" type="text" size="small" @click="notifyWeakPassword">通知修改</el-button>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value text-warning">{{ overview.noMfaCount }}</div>
            <div class="stat-label">未启用MFA的管理员</div>
            <el-button v-if="overview.noMfaCount > 0" type="text" size="small" @click="enforceMfa">强制启用</el-button>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ overview.lockedCount }}</div>
            <div class="stat-label">已锁定账户</div>
            <el-button v-if="overview.lockedCount > 0" type="text" size="small" @click="viewLockedAccounts">查看</el-button>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ overview.expiringPasswordCount }}</div>
            <div class="stat-label">即将过期密码</div>
            <el-button v-if="overview.expiringPasswordCount > 0" type="text" size="small" @click="notifyExpiring">通知</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 锁定账户对话框 -->
    <el-dialog title="已锁定账户" :visible.sync="lockedDialogVisible" width="700px">
      <el-table :data="lockedAccounts" size="small">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="lockTime" label="锁定时间" />
        <el-table-column prop="reason" label="锁定原因" />
        <el-table-column label="操作" width="100">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="unlockAccount(row)">解锁</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'SecurityPolicy',
  data() {
    return {
      policies: {
        password: {
          enabled: true,
          config: {
            minLength: 8,
            complexity: ['uppercase', 'lowercase', 'number'],
            expiryDays: 90,
            historyCount: 5
          }
        },
        login: {
          enabled: true,
          config: {
            maxFailures: 5,
            lockDuration: 30,
            sessionTimeout: 60,
            concurrentLogin: 'multiple',
            ipWhitelistEnabled: false,
            ipWhitelist: ''
          }
        },
        audit: {
          enabled: true,
          config: {
            events: ['login', 'password', 'user', 'role', 'permission'],
            retentionDays: 180,
            alertEnabled: true,
            alertLoginFailures: 5,
            alertRequestsPerMinute: 1000
          }
        }
      },
      overview: {
        weakPasswordCount: 3,
        noMfaCount: 2,
        lockedCount: 1,
        expiringPasswordCount: 5
      },
      lockedDialogVisible: false,
      lockedAccounts: [
        { username: 'test_user', lockTime: '2023-12-01 10:30:00', reason: '连续登录失败5次' }
      ]
    }
  },
  methods: {
    savePolicy(type) {
      const policyName = { password: '密码', login: '登录', audit: '审计' }[type]
      this.$message.success(`${policyName}策略已保存`)
    },
    refreshOverview() {
      this.$message.success('安全概览已刷新')
    },
    notifyWeakPassword() {
      this.$message.success('已发送弱密码修改通知')
    },
    enforceMfa() {
      this.$confirm('确定为所有未启用MFA的管理员强制启用MFA吗？', '提示').then(() => {
        this.$message.success('已强制启用MFA')
        this.overview.noMfaCount = 0
      })
    },
    viewLockedAccounts() {
      this.lockedDialogVisible = true
    },
    unlockAccount(row) {
      const index = this.lockedAccounts.findIndex(item => item.username === row.username)
      if (index > -1) {
        this.lockedAccounts.splice(index, 1)
        this.overview.lockedCount--
        this.$message.success(`账户 ${row.username} 已解锁`)
      }
    },
    notifyExpiring() {
      this.$message.success('已发送密码过期提醒')
    }
  }
}
</script>

<style lang="scss" scoped>
.security-policy-page {
  padding: 20px;

  .policy-section {
    border-radius: 10px;
    margin-bottom: 20px;

    ::v-deep .el-card__header {
      font-weight: 600;
    }

    .form-tip {
      margin-left: 10px;
      color: #909399;
    }

    .alert-conditions {
      .condition-item {
        margin-bottom: 10px;

        span {
          margin: 0 5px;
        }
      }
    }
  }

  .security-overview {
    border-radius: 10px;

    .stat-item {
      text-align: center;
      padding: 20px;

      .stat-value {
        font-size: 32px;
        font-weight: 600;
        margin-bottom: 8px;

        &.text-danger {
          color: #f56c6c;
        }

        &.text-warning {
          color: #e6a23c;
        }
      }

      .stat-label {
        color: #909399;
        font-size: 14px;
        margin-bottom: 10px;
      }
    }
  }
}
</style>
