<template>
  <div class="security-policy-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">登录验证策略</h1>
          <p class="page-desc">配置登录安全策略、会话管理和审计规则</p>
        </div>
      </div>

      <!-- 安全概览 -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon locked">
            <i class="el-icon-lock" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.lockedCount }}</div>
            <div class="stat-label">已锁定账户</div>
            <el-button
              v-if="overview.lockedCount > 0"
              type="text"
              size="mini"
              @click="viewLockedAccounts"
            >
              查看
            </el-button>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon mfa">
            <i class="el-icon-mobile-phone" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.noMfaCount }}</div>
            <div class="stat-label">未启用MFA的管理员</div>
            <el-button v-if="overview.noMfaCount > 0" type="text" size="mini" @click="enforceMfa">
              强制启用
            </el-button>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon expire">
            <i class="el-icon-time" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.expiringPasswordCount }}</div>
            <div class="stat-label">即将过期密码</div>
            <el-button v-if="overview.expiringPasswordCount > 0" type="text" size="mini" @click="notifyExpiring">
              通知
            </el-button>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon sessions">
            <i class="el-icon-user" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ activeSessions }}</div>
            <div class="stat-label">活跃会话</div>
            <el-button type="text" size="mini" @click="viewActiveSessions">查看</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 策略配置 -->
    <div class="policies-section">
      <div class="section-header">
        <span class="section-title">策略配置</span>
      </div>

      <!-- 登录策略 -->
      <el-card class="policy-card" shadow="hover">
        <div slot="header">
          <span>
            <i class="el-icon-key" />
            登录策略
            <span>
              <el-switch
                v-model="policies.login.enabled"
                active-text="启用"
                style="float: right"
                @change="savePolicy('login')"
              />
              </i></span></span></div>

        <el-form :model="policies.login.config" label-width="180px" :disabled="!policies.login.enabled">
          <el-row :gutter="40">
            <el-col :span="12">
              <el-form-item label="最大登录失败次数">
                <el-input-number v-model="policies.login.config.maxFailures" :min="3" :max="10" />
                <span class="form-tip">次失败后锁定账户</span>
              </el-form-item>

              <el-form-item label="账户锁定时间">
                <el-select v-model="policies.login.config.lockDuration" style="width: 200px">
                  <el-option label="5分钟" :value="5" />
                  <el-option label="15分钟" :value="15" />
                  <el-option label="30分钟" :value="30" />
                  <el-option label="1小时" :value="60" />
                  <el-option label="永久锁定（需管理员解锁）" :value="-1" />
                </el-select>
              </el-form-item>

              <el-form-item label="会话超时时间">
                <el-select v-model="policies.login.config.sessionTimeout" style="width: 200px">
                  <el-option label="15分钟" :value="15" />
                  <el-option label="30分钟" :value="30" />
                  <el-option label="1小时" :value="60" />
                  <el-option label="2小时" :value="120" />
                  <el-option label="4小时" :value="240" />
                  <el-option label="8小时" :value="480" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="并发登录限制">
                <el-radio-group v-model="policies.login.config.concurrentLogin">
                  <el-radio label="single">单点登录（只允许一个设备）</el-radio>
                  <el-radio label="multiple">允许多设备同时登录</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="IP白名单">
                <el-switch v-model="policies.login.config.ipWhitelistEnabled" />
                <div class="form-hint">启用后只有指定IP范围可以登录</div>
              </el-form-item>

              <el-form-item v-if="policies.login.config.ipWhitelistEnabled" label="允许的IP范围">
                <el-input
                  v-model="policies.login.config.ipWhitelist"
                  type="textarea"
                  :rows="3"
                  placeholder="每行输入一个IP或CIDR，例如：&#10;192.168.1.0/24&#10;10.0.0.1"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button type="primary" icon="el-icon-check" @click="savePolicy('login')">保存登录策略</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 审计策略 -->
      <el-card class="policy-card" shadow="hover">
        <div slot="header">
          <span>
            <i
              class="el-icon-document-checked"
            />
            审计策略
            <span>
              <el-switch
                v-model="policies.audit.enabled"
                active-text="启用"
                style="float: right"
                @change="savePolicy('audit')"
              />
            </span>
          </span></div>

        <el-form :model="policies.audit.config" label-width="180px" :disabled="!policies.audit.enabled">
          <el-row :gutter="40">
            <el-col :span="12">
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
                <el-select v-model="policies.audit.config.retentionDays" style="width: 200px">
                  <el-option label="30天" :value="30" />
                  <el-option label="90天" :value="90" />
                  <el-option label="180天" :value="180" />
                  <el-option label="1年" :value="365" />
                  <el-option label="永久保留" :value="0" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="异常告警">
                <el-switch v-model="policies.audit.config.alertEnabled" />
                <div class="form-hint">检测到异常行为时发送告警通知</div>
              </el-form-item>

              <el-form-item v-if="policies.audit.config.alertEnabled" label="告警条件">
                <div class="alert-conditions">
                  <div class="condition-item">
                    <span>连续登录失败超过</span>
                    <el-input-number
                      v-model="policies.audit.config.alertLoginFailures"
                      :min="3"
                      :max="10"
                      size="small"
                    />
                    <span>次</span>
                  </div>
                  <div class="condition-item">
                    <span>单IP请求超过</span>
                    <el-input-number
                      v-model="policies.audit.config.alertRequestsPerMinute"
                      :min="100"
                      :max="10000"
                      :step="100"
                      size="small"
                    />
                    <span>次/分钟</span>
                  </div>
                </div>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button type="primary" icon="el-icon-check" @click="savePolicy('audit')">保存审计策略</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 锁定账户对话框 -->
    <el-dialog title="已锁定账户" :visible.sync="lockedDialogVisible" width="700px">
      <el-table :data="lockedAccounts" size="small">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="lockTime" label="锁定时间" />
        <el-table-column prop="reason" label="锁定原因" />
        <el-table-column prop="unlockTime" label="自动解锁" />
        <el-table-column label="操作" width="100">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" icon="el-icon-unlock" @click="unlockAccount(row)">解锁</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 活跃会话对话框 -->
    <el-dialog title="活跃会话" :visible.sync="sessionsDialogVisible" width="800px">
      <el-table :data="activeSessionsList" size="small">
        <el-table-column prop="username" label="用户" />
        <el-table-column prop="device" label="设备" />
        <el-table-column prop="ip" label="IP地址" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="loginTime" label="登录时间" />
        <el-table-column label="操作" width="100">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" class="danger" @click="kickoutSession(row)">踢出</el-button>
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
      activeSessions: 128,
      policies: {
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
        lockedCount: 1,
        noMfaCount: 2,
        expiringPasswordCount: 5
      },
      lockedDialogVisible: false,
      sessionsDialogVisible: false,
      lockedAccounts: [
        { username: 'test_user', lockTime: '2023-12-01 10:30:00', reason: '连续登录失败5次', unlockTime: '2023-12-01 11:00:00' }
      ],
      activeSessionsList: [
        { username: 'admin', device: 'Chrome / Windows', ip: '192.168.1.100', location: '北京', loginTime: '2023-12-01 09:00:00' },
        { username: 'zhangsan', device: 'Safari / macOS', ip: '192.168.1.101', location: '上海', loginTime: '2023-12-01 10:30:00' }
      ]
    }
  },
  methods: {
    refreshData() {
      this.$message.success('数据已刷新')
    },
    saveAllPolicies() {
      this.$message.success('所有策略配置已保存')
    },
    savePolicy(type) {
      const policyName = { login: '登录', audit: '审计' }[type]
      this.$message.success(`${policyName}策略已保存`)
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
    viewActiveSessions() {
      this.sessionsDialogVisible = true
    },
    kickoutSession(row) {
      const index = this.activeSessionsList.findIndex(item => item.username === row.username)
      if (index > -1) {
        this.activeSessionsList.splice(index, 1)
        this.activeSessions--
        this.$message.success(`用户 ${row.username} 已踢出`)
      }
    },
    enforceMfa() {
      this.$confirm('确定为所有未启用MFA的管理员强制启用MFA吗？', '提示').then(() => {
        this.$message.success('已强制启用MFA')
        this.overview.noMfaCount = 0
      })
    },
    notifyExpiring() {
      this.$message.success('已发送密码过期提醒')
    }
  }
}
</script>

<style lang="scss" scoped>
.security-policy-page {
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

          &.locked {
            background: #fff2f0;
            color: #ff4d4f;
          }

          &.mfa {
            background: #fff7e6;
            color: #faad14;
          }

          &.expire {
            background: #e6f4ff;
            color: #1890ff;
          }

          &.sessions {
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

  // 策略配置
  .policies-section {
    .section-header {
      margin-bottom: 16px;

      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: #1a1a1a;
      }
    }

    .policy-card {
      border-radius: 12px;
      margin-bottom: 24px;

      ::v-deep .el-card__header {
        font-weight: 600;

        i {
          margin-right: 8px;
          color: #409eff;
        }
      }

      .form-tip {
        margin-left: 10px;
        color: #909399;
      }

      .form-hint {
        font-size: 12px;
        color: #999;
        margin-top: 4px;
      }

      .alert-conditions {
        .condition-item {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 12px;

          &:last-child {
            margin-bottom: 0;
          }

          span {
            color: #606266;
          }
        }
      }
    }
  }
}

// 响应式
@media (max-width: 1200px) {
  .security-policy-page {
    .page-header {
      .stats-row {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
}

@media (max-width: 768px) {
  .security-policy-page {
    padding: 16px;

    .page-header {
      .stats-row {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>
