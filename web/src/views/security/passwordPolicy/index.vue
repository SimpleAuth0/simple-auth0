<template>
  <div class="password-policy-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">密码策略</h1>
          <p class="page-desc">配置密码强度要求和过期策略，提升账户安全性</p>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon weak">
            <i class="el-icon-warning" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.weakPasswordUsers }}</div>
            <div class="stat-label">弱密码用户</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon expire">
            <i class="el-icon-time" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.expiringUsers }}</div>
            <div class="stat-label">即将过期</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon recent">
            <i class="el-icon-refresh" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.recentlyChanged }}</div>
            <div class="stat-label">近期修改</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon strong">
            <i class="el-icon-check" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.strongPasswordRate }}%</div>
            <div class="stat-label">强密码占比</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 标签页内容 -->
    <el-tabs v-model="activeTab" type="border-card" class="policy-tabs">
      <!-- 策略配置 -->
      <el-tab-pane label="策略配置" name="config">
        <span slot="label">
          <i class="el-icon-setting" /> 策略配置
        </span>
        <el-card class="tab-content-card" shadow="never">
          <el-row :gutter="60">
            <el-col :span="12">
              <h4 class="section-title">
                <i class="el-icon-setting" />
                基础设置
              </h4>

              <el-form :model="policies" label-width="180px">
                <el-form-item label="最小密码长度">
                  <el-slider v-model="policies.minLength" :min="6" :max="32" show-input />
                  <div class="form-hint">建议至少8位</div>
                </el-form-item>

                <el-form-item label="最大密码长度">
                  <el-slider v-model="policies.maxLength" :min="16" :max="128" show-input />
                </el-form-item>

                <el-form-item label="密码复杂度要求">
                  <el-checkbox-group v-model="policies.complexity">
                    <el-checkbox label="uppercase">大写字母</el-checkbox>
                    <el-checkbox label="lowercase">小写字母</el-checkbox>
                    <el-checkbox label="number">数字</el-checkbox>
                    <el-checkbox label="special">特殊字符</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>

                <el-form-item label="密码过期策略">
                  <el-select v-model="policies.expiryDays" style="width: 100%">
                    <el-option label="永不过期" :value="0" />
                    <el-option label="30天" :value="30" />
                    <el-option label="60天" :value="60" />
                    <el-option label="90天" :value="90" />
                    <el-option label="180天" :value="180" />
                    <el-option label="365天" :value="365" />
                  </el-select>
                  <div class="form-hint">密码过期后用户必须修改密码</div>
                </el-form-item>
              </el-form>
            </el-col>

            <el-col :span="12">
              <h4 class="section-title">
                <i class="el-icon-s-tools" />
                高级设置
              </h4>

              <el-form :model="policies" label-width="180px">
                <el-form-item label="历史密码检查">
                  <el-input-number v-model="policies.historyCount" :min="0" :max="24" style="width: 120px" />
                  <span class="form-tip">次</span>
                  <div class="form-hint">禁止重复使用最近N次的历史密码</div>
                </el-form-item>

                <el-form-item label="密码修改冷却期">
                  <el-input-number v-model="policies.changeCooldown" :min="0" :max="72" style="width: 120px" />
                  <span class="form-tip">小时</span>
                  <div class="form-hint">修改密码后需等待多久才能再次修改</div>
                </el-form-item>

                <el-form-item label="首次登录修改密码">
                  <el-switch v-model="policies.forceChangeOnFirstLogin" />
                  <div class="form-hint">新用户首次登录必须修改初始密码</div>
                </el-form-item>

                <el-form-item label="重置密码后修改">
                  <el-switch v-model="policies.forceChangeAfterReset" />
                  <div class="form-hint">管理员重置密码后用户必须修改密码</div>
                </el-form-item>

                <el-form-item label="常见弱密码检查">
                  <el-switch v-model="policies.checkCommonPasswords" />
                  <div class="form-hint">禁止使用常见弱密码（如123456, password等）</div>
                </el-form-item>

                <el-form-item label="包含用户名检查">
                  <el-switch v-model="policies.checkUsername" />
                  <div class="form-hint">密码不能包含用户名</div>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>

          <div class="tab-actions">
            <el-button type="primary" icon="el-icon-check" @click="saveConfig">保存配置</el-button>
            <el-button icon="el-icon-refresh" @click="resetConfig">重置</el-button>
            <el-button icon="el-icon-document" @click="exportPolicy">导出配置</el-button>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 强度测试 -->
      <el-tab-pane label="强度测试" name="test">
        <span slot="label">
          <i class="el-icon-s-data" /> 强度测试
        </span>
        <el-card class="tab-content-card" shadow="never">
          <div class="test-section">
            <h4 class="section-title">
              <i class="el-icon-s-data" />
              密码强度实时检测
              <el-tooltip content="输入密码测试当前策略下的强度">
                <i class="el-icon-question" style="margin-left: 8px; color: #999; font-size: 14px;" />
              </el-tooltip>
            </h4>

            <el-row :gutter="40" class="test-input-row">
              <el-col :span="12">
                <el-input
                  v-model="testPassword"
                  type="password"
                  placeholder="输入密码测试强度"
                  show-password
                  size="large"
                  @input="checkStrength"
                />
              </el-col>
              <el-col :span="12">
                <div class="strength-bar">
                  <div class="strength-label">
                    强度: <span :style="{ color: strengthColor }">{{ strengthLabel }}</span>
                  </div>
                  <el-progress :percentage="strengthScore" :color="strengthColor" :stroke-width="12" />
                </div>
              </el-col>
            </el-row>

            <div class="strength-checks">
              <div class="check-item" :class="{ 'check-pass': checks.length }">
                <i :class="checks.length ? 'el-icon-success' : 'el-icon-circle-close'" />
                长度至少 {{ policies.minLength }} 位
              </div>
              <div class="check-item" :class="{ 'check-pass': checks.uppercase }">
                <i :class="checks.uppercase ? 'el-icon-success' : 'el-icon-circle-close'" />
                包含大写字母 (A-Z)
              </div>
              <div class="check-item" :class="{ 'check-pass': checks.lowercase }">
                <i :class="checks.lowercase ? 'el-icon-success' : 'el-icon-circle-close'" />
                包含小写字母 (a-z)
              </div>
              <div class="check-item" :class="{ 'check-pass': checks.number }">
                <i :class="checks.number ? 'el-icon-success' : 'el-icon-circle-close'" />
                包含数字 (0-9)
              </div>
              <div class="check-item" :class="{ 'check-pass': checks.special }">
                <i :class="checks.special ? 'el-icon-success' : 'el-icon-circle-close'" />
                包含特殊字符 (!@#$%^&*)
              </div>
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 弱密码管理 -->
      <el-tab-pane label="弱密码管理" name="dictionary">
        <span slot="label">
          <i class="el-icon-warning-outline" /> 弱密码管理
        </span>
        <el-card class="tab-content-card" shadow="never">
          <div class="dictionary-header">
            <h4 class="section-title" style="margin: 0;">
              <i class="el-icon-warning-outline" />
              弱密码列表
            </h4>
            <div class="dictionary-actions">
              <el-button type="primary" size="small" icon="el-icon-plus" @click="addWeakPassword">
                添加弱密码
              </el-button>
              <el-button size="small" icon="el-icon-upload2" @click="importWeakPasswords">
                批量导入
              </el-button>
            </div>
          </div>

          <div class="word-list">
            <el-tag
              v-for="pwd in weakPasswords"
              :key="pwd"
              closable
              type="danger"
              class="weak-password-tag"
              size="medium"
              @close="removeWeakPassword(pwd)"
            >
              {{ pwd }}
            </el-tag>
          </div>

          <el-empty v-if="weakPasswords.length === 0" description="暂无弱密码定义" />

          <!-- 弱密码用户处理 -->
          <div class="weak-password-actions">
            <h4 class="section-title">
              <i class="el-icon-message" />
              弱密码用户处理
            </h4>
            <div class="action-item">
              <div class="action-info">
                <div class="action-name">通知弱密码用户</div>
                <div class="action-desc">当前有 <strong>{{ stats.weakPasswordUsers }}</strong> 位用户使用弱密码，发送邮件通知其修改密码</div>
              </div>
              <el-button type="warning" icon="el-icon-message" @click="notifyWeakPasswordUsers">
                发送通知
              </el-button>
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 密码整改 -->
      <el-tab-pane label="密码整改" name="remediation">
        <span slot="label">
          <i class="el-icon-s-check" /> 密码整改
        </span>
        <el-card class="tab-content-card" shadow="never">
          <div class="remediation-section">
            <h4 class="section-title">
              <i class="el-icon-s-check" />
              密码安全整改
            </h4>
            <p class="section-desc">针对密码安全问题进行全局整改操作，提升系统整体密码安全水平</p>

            <div class="remediation-list">
              <div class="remediation-card danger">
                <div class="remediation-icon">
                  <i class="el-icon-refresh" />
                </div>
                <div class="remediation-content">
                  <div class="remediation-title">强制密码重置</div>
                  <div class="remediation-desc">要求所有用户在下次登录时强制修改密码，适用于安全事件后的紧急处置</div>
                </div>
                <el-button type="danger" plain icon="el-icon-refresh" @click="forcePasswordReset">
                  立即执行
                </el-button>
              </div>

              <div class="remediation-card warning">
                <div class="remediation-icon">
                  <i class="el-icon-time" />
                </div>
                <div class="remediation-content">
                  <div class="remediation-title">清理过期密码用户</div>
                  <div class="remediation-desc">当前有 <strong>{{ stats.expiringUsers }}</strong> 位用户密码即将过期，可批量发送提醒或强制重置</div>
                </div>
                <el-button type="warning" plain icon="el-icon-message" @click="notifyExpiringUsers">
                  发送提醒
                </el-button>
              </div>

              <div class="remediation-card info">
                <div class="remediation-icon">
                  <i class="el-icon-lock" />
                </div>
                <div class="remediation-content">
                  <div class="remediation-title">锁定长期未修改密码用户</div>
                  <div class="remediation-desc">自动锁定超过密码有效期且未修改密码的用户账户，需管理员手动解锁</div>
                </div>
                <el-button type="primary" plain icon="el-icon-lock" @click="lockInactiveUsers">
                  执行锁定
                </el-button>
              </div>
            </div>

            <div class="remediation-tips">
              <h5><i class="el-icon-info" /> 操作建议</h5>
              <ul>
                <li>强制密码重置将影响所有用户，建议在非工作时间执行，并提前通知用户</li>
                <li>定期执行密码整改可有效降低弱密码带来的安全风险</li>
                <li>建议在执行大规模整改前，先导出当前配置作为备份</li>
              </ul>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  name: 'PasswordPolicy',
  data() {
    return {
      activeTab: 'config',
      testPassword: '',
      strengthScore: 0,
      strengthLabel: '未输入',
      strengthColor: '#909399',
      checks: {
        length: false,
        uppercase: false,
        lowercase: false,
        number: false,
        special: false
      },
      policies: {
        minLength: 8,
        maxLength: 64,
        complexity: ['uppercase', 'lowercase', 'number'],
        expiryDays: 90,
        historyCount: 5,
        changeCooldown: 24,
        forceChangeOnFirstLogin: true,
        forceChangeAfterReset: true,
        checkCommonPasswords: true,
        checkUsername: true
      },
      weakPasswords: ['123456', 'password', '12345678', 'qwerty', 'admin123', 'letmein', '123456789', '111111', '123123', 'welcome'],
      stats: {
        weakPasswordUsers: 3,
        expiringUsers: 8,
        recentlyChanged: 15,
        strongPasswordRate: 92
      }
    }
  },
  methods: {
    refreshData() {
      this.$message.success('数据已刷新')
    },
    checkStrength() {
      const pwd = this.testPassword
      if (!pwd) {
        this.strengthScore = 0
        this.strengthLabel = '未输入'
        this.strengthColor = '#909399'
        this.checks = { length: false, uppercase: false, lowercase: false, number: false, special: false }
        return
      }

      this.checks.length = pwd.length >= this.policies.minLength
      this.checks.uppercase = /[A-Z]/.test(pwd)
      this.checks.lowercase = /[a-z]/.test(pwd)
      this.checks.number = /\d/.test(pwd)
      this.checks.special = /[!@#$%^&*(),.?":{}|<>]/.test(pwd)

      let score = 0
      if (this.checks.length) score += 20
      if (this.checks.uppercase) score += 20
      if (this.checks.lowercase) score += 20
      if (this.checks.number) score += 20
      if (this.checks.special) score += 20

      this.strengthScore = score

      if (score < 40) {
        this.strengthLabel = '弱'
        this.strengthColor = '#f56c6c'
      } else if (score < 80) {
        this.strengthLabel = '中'
        this.strengthColor = '#e6a23c'
      } else {
        this.strengthLabel = '强'
        this.strengthColor = '#67c23a'
      }
    },
    saveConfig() {
      this.$message.success('密码策略配置已保存')
    },
    resetConfig() {
      this.policies = {
        minLength: 8,
        maxLength: 64,
        complexity: ['uppercase', 'lowercase', 'number'],
        expiryDays: 90,
        historyCount: 5,
        changeCooldown: 24,
        forceChangeOnFirstLogin: true,
        forceChangeAfterReset: true,
        checkCommonPasswords: true,
        checkUsername: true
      }
      this.$message.success('配置已重置')
    },
    addWeakPassword() {
      this.$prompt('请输入弱密码', '添加弱密码', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (value && !this.weakPasswords.includes(value)) {
          this.weakPasswords.push(value)
          this.$message.success('弱密码已添加')
        }
      })
    },
    importWeakPasswords() {
      this.$message.info('批量导入弱密码')
    },
    removeWeakPassword(pwd) {
      const index = this.weakPasswords.indexOf(pwd)
      if (index > -1) {
        this.weakPasswords.splice(index, 1)
        this.$message.success('已删除')
      }
    },
    notifyWeakPasswordUsers() {
      this.$confirm(`确定向 ${this.stats.weakPasswordUsers} 位弱密码用户发送修改通知吗？`, '提示', {
        confirmButtonText: '发送',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('通知已发送')
      })
    },
    notifyExpiringUsers() {
      this.$confirm(`确定向 ${this.stats.expiringUsers} 位密码即将过期的用户发送提醒吗？`, '提示', {
        confirmButtonText: '发送',
        cancelButtonText: '取消'
      }).then(() => {
        this.$message.success('提醒邮件已发送')
      })
    },
    lockInactiveUsers() {
      this.$confirm('确定要锁定长期未修改密码的用户账户吗？被锁定的用户需要联系管理员解锁。', '警告', {
        confirmButtonText: '确定锁定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('已锁定符合条件的用户账户')
      })
    },
    forcePasswordReset() {
      this.$confirm('确定强制所有用户在下次登录时修改密码吗？此操作不可撤销。', '警告', {
        confirmButtonText: '强制重置',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        this.$message.success('已设置强制密码修改')
      })
    },
    exportPolicy() {
      this.$message.success('策略配置已导出')
    }
  }
}
</script>

<style lang="scss" scoped>
.password-policy-page {
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

          &.weak {
            background: #fff2f0;
            color: #ff4d4f;
          }

          &.expire {
            background: #fff7e6;
            color: #faad14;
          }

          &.recent {
            background: #e6f4ff;
            color: #1890ff;
          }

          &.strong {
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

  // 标签页样式
  .policy-tabs {
    border-radius: 12px;
    overflow: hidden;

    ::v-deep .el-tabs__header {
      background: #f5f7fa;
      margin: 0;
    }

    ::v-deep .el-tabs__nav {
      border-radius: 0;
    }

    ::v-deep .el-tabs__item {
      height: 48px;
      line-height: 48px;
      font-size: 14px;

      i {
        margin-right: 4px;
      }
    }

    ::v-deep .el-tabs__content {
      padding: 0;
    }
  }

  .tab-content-card {
    border: none;
    border-radius: 0;
    min-height: 500px;

    ::v-deep .el-card__body {
      padding: 24px;
    }
  }

  .tab-actions {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #ebeef5;
    text-align: center;
  }

  // 测试区域样式
  .test-section {
    .test-input-row {
      margin-top: 20px;
      margin-bottom: 30px;
    }
  }

  // 字典头部样式
  .dictionary-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .dictionary-actions {
      display: flex;
      gap: 10px;
    }
  }

  // 弱密码用户处理
  .weak-password-actions {
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid #ebeef5;

    .action-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      background: #fdf6ec;
      border: 1px solid #f5dab1;
      border-radius: 8px;

      .action-info {
        flex: 1;

        .action-name {
          font-size: 16px;
          font-weight: 500;
          color: #1a1a1a;
          margin-bottom: 8px;
        }

        .action-desc {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }

  // 密码整改样式
  .remediation-section {
    .section-desc {
      font-size: 14px;
      color: #666;
      margin-bottom: 24px;
    }

    .remediation-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
      margin-bottom: 32px;
    }

    .remediation-card {
      display: flex;
      align-items: center;
      padding: 24px;
      border: 1px solid #ebeef5;
      border-radius: 8px;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }

      &.danger {
        background: #fef0f0;
        border-color: #fde2e2;

        .remediation-icon {
          background: #f56c6c;
          color: #fff;
        }
      }

      &.warning {
        background: #fdf6ec;
        border-color: #faecd8;

        .remediation-icon {
          background: #e6a23c;
          color: #fff;
        }
      }

      &.info {
        background: #f0f9ff;
        border-color: #d9ecff;

        .remediation-icon {
          background: #409eff;
          color: #fff;
        }
      }

      .remediation-icon {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        margin-right: 20px;
        flex-shrink: 0;
      }

      .remediation-content {
        flex: 1;

        .remediation-title {
          font-size: 16px;
          font-weight: 500;
          color: #1a1a1a;
          margin-bottom: 6px;
        }

        .remediation-desc {
          font-size: 14px;
          color: #666;
          line-height: 1.5;
        }
      }
    }

    .remediation-tips {
      padding: 16px 20px;
      background: #f5f7fa;
      border-radius: 8px;

      h5 {
        margin: 0 0 12px 0;
        font-size: 14px;
        color: #1a1a1a;

        i {
          color: #409eff;
          margin-right: 6px;
        }
      }

      ul {
        margin: 0;
        padding-left: 20px;

        li {
          font-size: 13px;
          color: #666;
          line-height: 1.8;
        }
      }
    }
  }

  .strength-bar {
    .strength-label {
      margin-bottom: 10px;
      font-weight: 500;
      color: #1a1a1a;
    }
  }

  .strength-checks {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;

    .check-item {
      color: #999;
      font-size: 14px;

      i {
        margin-right: 5px;
      }

      &.check-pass {
        color: #67c23a;

        i {
          color: #67c23a;
        }
      }
    }
  }

  .section-title {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #ebeef5;
    color: #1a1a1a;
    font-size: 16px;

    i {
      margin-right: 8px;
      color: #409eff;
    }
  }

  .form-tip {
    margin-left: 10px;
    color: #606266;
  }

  .form-hint {
    font-size: 12px;
    color: #999;
    margin-top: 4px;
  }

  .word-list {
    padding: 8px 0;
  }

  .weak-password-tag {
    margin: 0 10px 10px 0;
  }
}

// 响应式
@media (max-width: 1200px) {
  .password-policy-page {
    .page-header {
      .stats-row {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
}

@media (max-width: 768px) {
  .password-policy-page {
    padding: 16px;

    .page-header {
      .stats-row {
        grid-template-columns: 1fr;
      }
    }

    .policy-tabs {
      ::v-deep .el-tabs__item {
        padding: 0 12px;
        font-size: 12px;

        i {
          display: none;
        }
      }
    }

    .weak-password-actions {
      .action-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
      }
    }

    .remediation-section {
      .remediation-card {
        flex-direction: column;
        text-align: center;
        gap: 16px;

        .remediation-icon {
          margin-right: 0;
        }

        .remediation-content {
          text-align: center;
        }
      }
    }

    .dictionary-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }
  }
}
</style>
