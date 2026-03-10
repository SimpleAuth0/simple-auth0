<template>
  <div class="password-policy-page">

    <!-- 密码强度实时检测 -->
    <el-card class="test-card" shadow="hover">
      <div slot="header">
        <span>密码强度测试</span>
      </div>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-input
            v-model="testPassword"
            type="password"
            placeholder="输入密码测试强度"
            show-password
            @input="checkStrength"
          />
        </el-col>
        <el-col :span="12">
          <div class="strength-bar">
            <div class="strength-label">强度: {{ strengthLabel }}</div>
            <el-progress :percentage="strengthScore" :color="strengthColor" :stroke-width="10" />
          </div>
        </el-col>
      </el-row>

      <div class="strength-checks">
        <div class="check-item" :class="{ 'check-pass': checks.length }">
          <i :class="checks.length ? 'el-icon-success' : 'el-icon-error'" />
          长度至少 {{ policies.minLength }} 位
        </div>
        <div class="check-item" :class="{ 'check-pass': checks.uppercase }">
          <i :class="checks.uppercase ? 'el-icon-success' : 'el-icon-error'" />
          包含大写字母 (A-Z)
        </div>
        <div class="check-item" :class="{ 'check-pass': checks.lowercase }">
          <i :class="checks.lowercase ? 'el-icon-success' : 'el-icon-error'" />
          包含小写字母 (a-z)
        </div>
        <div class="check-item" :class="{ 'check-pass': checks.number }">
          <i :class="checks.number ? 'el-icon-success' : 'el-icon-error'" />
          包含数字 (0-9)
        </div>
        <div class="check-item" :class="{ 'check-pass': checks.special }">
          <i :class="checks.special ? 'el-icon-success' : 'el-icon-error'" />
          包含特殊字符 (!@#$%^&*)
        </div>
      </div>
    </el-card>

    <!-- 密码策略配置 -->
    <el-card class="config-card" shadow="hover">
      <div slot="header">
        <span>密码策略配置</span>
        <el-button type="primary" size="small" @click="saveConfig" style="float: right;">保存配置</el-button>
      </div>

      <el-row :gutter="40">
        <el-col :span="12">
          <h4 class="section-title">基础设置</h4>

          <el-form :model="policies" label-width="160px">
            <el-form-item label="最小密码长度">
              <el-slider v-model="policies.minLength" :min="6" :max="32" show-input />
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
            </el-form-item>
          </el-form>
        </el-col>

        <el-col :span="12">
          <h4 class="section-title">高级设置</h4>

          <el-form :model="policies" label-width="160px">
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
              <div class="form-hint">新用户首次登录必须修改密码</div>
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
    </el-card>

    <!-- 弱密码字典管理 -->
    <el-card class="dictionary-card" shadow="hover">
      <div slot="header">
        <span>弱密码字典</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="addWeakPassword" style="float: right; margin-left: 10px;">添加</el-button>
        <el-button size="small" icon="el-icon-upload2" @click="importWeakPasswords" style="float: right;">导入</el-button>
      </div>

      <el-tag
        v-for="pwd in weakPasswords"
        :key="pwd"
        closable
        type="danger"
        @close="removeWeakPassword(pwd)"
        class="weak-password-tag"
      >
        {{ pwd }}
      </el-tag>

      <el-empty v-if="weakPasswords.length === 0" description="暂无弱密码定义" />
    </el-card>

    <!-- 密码使用统计 -->
    <el-card class="stats-card" shadow="hover">
      <div slot="header">
        <span>密码安全统计</span>
      </div>

      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-box">
            <div class="stat-icon" style="background: #f56c6c20; color: #f56c6c;">
              <i class="el-icon-warning" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.weakPasswordUsers }}</div>
              <div class="stat-label">弱密码用户</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-box">
            <div class="stat-icon" style="background: #e6a23c20; color: #e6a23c;">
              <i class="el-icon-time" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.expiringUsers }}</div>
              <div class="stat-label">即将过期</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-box">
            <div class="stat-icon" style="background: #409eff20; color: #409eff;">
              <i class="el-icon-refresh" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.recentlyChanged }}</div>
              <div class="stat-label">近期修改</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-box">
            <div class="stat-icon" style="background: #67c23a20; color: #67c23a;">
              <i class="el-icon-check" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.strongPasswordRate }}%</div>
              <div class="stat-label">强密码占比</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-divider />

      <div class="action-bar">
        <el-button type="warning" icon="el-icon-message" @click="notifyWeakPasswordUsers">通知弱密码用户</el-button>
        <el-button type="primary" icon="el-icon-refresh" @click="forcePasswordReset">强制所有用户修改密码</el-button>
      </div>
    </el-card>

  </div>
</template>

<script>
export default {
  name: 'PasswordPolicy',
  data() {
    return {
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
      weakPasswords: ['123456', 'password', '12345678', 'qwerty', 'admin123', 'letmein'],
      stats: {
        weakPasswordUsers: 3,
        expiringUsers: 8,
        recentlyChanged: 15,
        strongPasswordRate: 92
      }
    }
  },
  methods: {
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
      this.$confirm(`确定向 ${this.stats.weakPasswordUsers} 位弱密码用户发送修改通知吗？`, '提示').then(() => {
        this.$message.success('通知已发送')
      })
    },
    forcePasswordReset() {
      this.$confirm('确定强制所有用户在下次登录时修改密码吗？', '警告', {
        type: 'warning'
      }).then(() => {
        this.$message.success('已设置强制密码修改')
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.password-policy-page {
  padding: 20px;

  .test-card,
  .config-card,
  .dictionary-card,
  .stats-card {
    border-radius: 10px;
    margin-bottom: 20px;
  }

  .strength-bar {
    .strength-label {
      margin-bottom: 10px;
      font-weight: 500;
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
      color: #909399;

      i {
        margin-right: 5px;
      }

      &.check-pass {
        color: #67c23a;
      }
    }
  }

  .section-title {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #ebeef5;
    color: #303133;
  }

  .form-tip {
    margin-left: 10px;
    color: #606266;
  }

  .form-hint {
    font-size: 12px;
    color: #909399;
    margin-top: 5px;
  }

  .weak-password-tag {
    margin: 0 10px 10px 0;
  }

  .stat-box {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 10px;

    .stat-icon {
      width: 50px;
      height: 50px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      margin-right: 15px;
    }

    .stat-info {
      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }

  .action-bar {
    text-align: center;
    padding: 10px;
  }
}
</style>
