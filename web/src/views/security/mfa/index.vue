<template>
  <div class="mfa-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">多因素认证 (MFA)</h1>
          <p class="page-desc">为用户登录添加额外的安全验证层，有效防止账户被盗用</p>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon enabled">
            <i class="el-icon-check" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ enabledUsers }}</div>
            <div class="stat-label">已启用MFA</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon rate">
            <i class="el-icon-pie-chart" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ mfaRate }}%</div>
            <div class="stat-label">启用率</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon method">
            <i class="el-icon-mobile-phone" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ enabledMethods }}</div>
            <div class="stat-label">启用方式</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon security">
            <i class="el-icon-shield" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ blockedAttacks }}</div>
            <div class="stat-label">今日验证</div>
          </div>
        </div>
      </div>
    </div>

    <!-- MFA方式配置 -->
    <div class="methods-section">
      <div class="section-header">
        <span class="section-title">认证方式配置</span>
      </div>

      <div class="methods-grid">
        <div
          v-for="method in mfaMethods"
          :key="method.key"
          class="method-card"
          :class="{ 'is-enabled': method.enabled }"
        >
          <div class="card-header">
            <div class="method-icon" :style="{ background: method.gradient }">
              <i :class="method.icon" />
            </div>
            <el-switch v-model="method.enabled" @change="handleMethodToggle(method)" />
          </div>

          <div class="card-body">
            <h3 class="method-name">{{ method.name }}</h3>
            <p class="method-desc">{{ method.description }}</p>

            <div v-if="method.enabled" class="method-stats">
              <div class="stat-row">
                <span class="stat-label">绑定用户</span>
                <span class="stat-value">{{ method.userCount }} 人</span>
              </div>
              <div class="stat-row">
                <span class="stat-label">今日验证</span>
                <span class="stat-value">{{ method.todayVerifications }} 次</span>
              </div>
            </div>
          </div>

          <div class="card-footer">
            <el-button type="text" icon="el-icon-setting" @click="configureMethod(method)">
              配置
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!--    &lt;!&ndash; MFA策略配置 &ndash;&gt;-->
    <!--    <el-card class="policy-card" shadow="hover">-->
    <!--      <div slot="header">-->
    <!--        <span>策略配置</span>-->
    <!--        <el-button-->
    <!--          type="primary"-->
    <!--          size="small"-->
    <!--          icon="el-icon-check"-->
    <!--          style="float: right"-->
    <!--          @click="savePolicy"-->
    <!--        >-->
    <!--          保存策略-->
    <!--        </el-button>-->
    <!--      </div>-->

    <!--      <el-form :model="mfaPolicy" label-width="180px" class="policy-form">-->
    <!--        <el-row :gutter="40">-->
    <!--          <el-col :span="12">-->
    <!--            <el-form-item label="MFA启用范围">-->
    <!--              <el-radio-group v-model="mfaPolicy.scope">-->
    <!--                <el-radio label="all">所有用户</el-radio>-->
    <!--                <el-radio label="admin">仅管理员</el-radio>-->
    <!--                <el-radio label="custom">自定义用户组</el-radio>-->
    <!--              </el-radio-group>-->
    <!--            </el-form-item>-->

    <!--            <el-form-item v-if="mfaPolicy.scope !== 'all'" label="登录时MFA触发条件">-->
    <!--              <el-checkbox-group v-model="mfaPolicy.conditions">-->
    <!--                <el-checkbox label="new_device">新设备登录</el-checkbox>-->
    <!--                <el-checkbox label="new_location">新地点登录</el-checkbox>-->
    <!--                <el-checkbox label="suspicious">可疑行为</el-checkbox>-->
    <!--                <el-checkbox label="high_value">高价值操作</el-checkbox>-->
    <!--              </el-checkbox-group>-->
    <!--            </el-form-item>-->

    <!--            <el-form-item label="记住设备时长">-->
    <!--              <el-select v-model="mfaPolicy.rememberDays" style="width: 200px">-->
    <!--                <el-option label="每次都需要" :value="0" />-->
    <!--                <el-option label="1天" :value="1" />-->
    <!--                <el-option label="7天" :value="7" />-->
    <!--                <el-option label="30天" :value="30" />-->
    <!--              </el-select>-->
    <!--              <div class="form-hint">在此时间内同设备登录无需再次验证</div>-->
    <!--            </el-form-item>-->
    <!--          </el-col>-->

    <!--          <el-col :span="12">-->
    <!--            <el-form-item label="验证失败锁定">-->
    <!--              <el-input-number v-model="mfaPolicy.maxFailures" :min="3" :max="10" />-->
    <!--              <span class="form-tip">次失败后锁定账户</span>-->
    <!--            </el-form-item>-->

    <!--            <el-form-item label="备用验证码数量">-->
    <!--              <el-input-number v-model="mfaPolicy.backupCodes" :min="5" :max="20" />-->
    <!--              <span class="form-tip">个一次性备用验证码</span>-->
    <!--            </el-form-item>-->

    <!--            <el-form-item label="强制启用MFA">-->
    <!--              <el-switch v-model="mfaPolicy.enforceForAdmins" />-->
    <!--              <div class="form-hint">管理员必须启用MFA才能登录</div>-->
    <!--            </el-form-item>-->
    <!--          </el-col>-->
    <!--        </el-row>-->
    <!--      </el-form>-->
    <!--    </el-card>-->

    <!--    &lt;!&ndash; 用户MFA绑定情况 &ndash;&gt;-->
    <!--    <el-card class="user-card" shadow="hover">-->
    <!--      <div slot="header">-->
    <!--        <span>用户MFA绑定情况</span>-->
    <!--        <div style="float: right; display: flex; gap: 12px">-->
    <!--          <el-input-->
    <!--            v-model="userSearch"-->
    <!--            placeholder="搜索用户"-->
    <!--            size="small"-->
    <!--            style="width: 200px"-->
    <!--            suffix-icon="el-icon-search"-->
    <!--          />-->
    <!--          <el-button type="primary" size="small" icon="el-icon-message" @click="notifyUnboundUsers">-->
    <!--            提醒未绑定用户-->
    <!--          </el-button>-->
    <!--        </div>-->
    <!--      </div>-->

    <!--      <el-table :data="userMfaList" size="small" :header-cell-style="tableHeaderStyle">-->
    <!--        <el-table-column prop="username" label="用户名" min-width="150">-->
    <!--          <template slot-scope="{ row }">-->
    <!--            <div class="user-cell">-->
    <!--              <el-avatar :size="32" :src="row.avatar">{{ row.username.charAt(0).toUpperCase() }}</el-avatar>-->
    <!--              <div class="user-info">-->
    <!--                <div class="username">{{ row.username }}</div>-->
    <!--                <div class="email">{{ row.email }}</div>-->
    <!--              </div>-->
    <!--            </div>-->
    <!--          </template>-->
    <!--        </el-table-column>-->
    <!--        <el-table-column prop="department" label="部门" width="120" />-->
    <!--        <el-table-column prop="role" label="角色" width="120">-->
    <!--          <template slot-scope="{ row }">-->
    <!--            <el-tag v-if="row.role === 'admin'" type="danger" size="small">管理员</el-tag>-->
    <!--            <el-tag v-else type="info" size="small">普通用户</el-tag>-->
    <!--          </template>-->
    <!--        </el-table-column>-->
    <!--        <el-table-column prop="mfaMethod" label="绑定方式" width="140">-->
    <!--          <template slot-scope="{ row }">-->
    <!--            <el-tag v-if="row.mfaMethod" :type="getMethodTag(row.mfaMethod)" size="small">-->
    <!--              <i :class="getMethodIcon(row.mfaMethod)" style="margin-right: 4px" />-->
    <!--              {{ getMethodName(row.mfaMethod) }}-->
    <!--            </el-tag>-->
    <!--            <span v-else class="text-muted">-->
    <!--              <i class="el-icon-close" style="margin-right: 4px" />-->
    <!--              未绑定-->
    <!--            </span>-->
    <!--          </template>-->
    <!--        </el-table-column>-->
    <!--        <el-table-column prop="bindTime" label="绑定时间" width="160" />-->
    <!--        <el-table-column prop="lastUsed" label="最后使用" width="160" />-->
    <!--        <el-table-column label="操作" width="150" fixed="right">-->
    <!--          <template slot-scope="{ row }">-->
    <!--            <el-button v-if="row.mfaMethod" size="mini" type="text" icon="el-icon-refresh" @click="resetMfa(row)">-->
    <!--              重置-->
    <!--            </el-button>-->
    <!--            <el-button v-else size="mini" type="text" icon="el-icon-bell" @click="enforceMfa(row)">-->
    <!--              提醒-->
    <!--            </el-button>-->
    <!--            <el-button size="mini" type="text" icon="el-icon-view" @click="viewMfaDetail(row)">-->
    <!--              详情-->
    <!--            </el-button>-->
    <!--          </template>-->
    <!--        </el-table-column>-->
    <!--      </el-table>-->

    <!--    <div class="pagination">-->
    <!--      <el-pagination-->
    <!--        background-->
    <!--        layout="total, sizes, prev, pager, next"-->
    <!--        :total="userTotal"-->
    <!--        :page-sizes="[10, 20, 50]"-->
    <!--        :page-size="10"-->
    <!--      />-->
    <!--    </div>-->
    <!--    </el-card>-->

    <!-- 配置对话框 -->
    <el-dialog :title="currentMethod.name + ' 配置'" :visible.sync="configDialogVisible" width="500px">
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

    <!-- MFA详情对话框 -->
    <el-dialog title="MFA详情" :visible.sync="detailDialogVisible" width="500px">
      <el-descriptions v-if="currentUser" :column="1" border>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="MFA状态">
          <el-tag v-if="currentUser.mfaMethod" type="success">已启用</el-tag>
          <el-tag v-else type="info">未启用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentUser.mfaMethod" label="绑定方式">
          {{ getMethodName(currentUser.mfaMethod) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentUser.bindTime" label="绑定时间">
          {{ currentUser.bindTime }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentUser.lastUsed" label="最后使用">
          {{ currentUser.lastUsed }}
        </el-descriptions-item>
        <el-descriptions-item label="备用验证码">
          <el-button type="text" size="small" @click="viewBackupCodes">查看备用验证码</el-button>
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button v-if="currentUser && currentUser.mfaMethod" type="warning" @click="resetMfa(currentUser)">
          重置MFA
        </el-button>
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
      enabledUsers: 77,
      mfaRate: 85,
      enabledMethods: 2,
      blockedAttacks: 23,
      mfaMethods: [
        {
          key: 'totp',
          name: 'TOTP验证器',
          icon: 'el-icon-mobile-phone',
          gradient: 'linear-gradient(135deg, #409eff 0%, #36cfc9 100%)',
          enabled: true,
          description: '使用Google Authenticator、Microsoft Authenticator等应用生成验证码',
          userCount: 45,
          todayVerifications: 128
        },
        {
          key: 'sms',
          name: '短信验证码',
          icon: 'el-icon-message',
          gradient: 'linear-gradient(135deg, #67c23a 0%, #95de64 100%)',
          enabled: true,
          description: '通过短信接收验证码，适用于所有手机用户',
          userCount: 32,
          todayVerifications: 86
        },
        {
          key: 'email',
          name: '邮件验证码',
          icon: 'el-icon-message-box',
          gradient: 'linear-gradient(135deg, #e6a23c 0%, #ffc53d 100%)',
          enabled: false,
          description: '通过邮件接收验证码，作为备用验证方式',
          userCount: 0,
          todayVerifications: 0
        }
      ],
      mfaPolicy: {
        scope: 'all',
        conditions: ['new_device', 'suspicious'],
        rememberDays: 7,
        maxFailures: 5,
        backupCodes: 10,
        enforceForAdmins: true
      },
      userSearch: '',
      userMfaList: [
        {
          username: 'admin',
          email: 'admin@example.com',
          department: '技术部',
          role: 'admin',
          avatar: '',
          mfaMethod: 'totp',
          bindTime: '2023-11-01 10:00:00',
          lastUsed: '2023-12-01 09:30:00'
        },
        {
          username: 'zhangsan',
          email: 'zhangsan@example.com',
          department: '产品部',
          role: 'user',
          avatar: '',
          mfaMethod: 'sms',
          bindTime: '2023-11-15 14:20:00',
          lastUsed: '2023-12-01 08:45:00'
        },
        {
          username: 'lisi',
          email: 'lisi@example.com',
          department: '测试部',
          role: 'user',
          avatar: '',
          mfaMethod: null,
          bindTime: '-',
          lastUsed: '-'
        }
      ],
      userTotal: 3,
      configDialogVisible: false,
      detailDialogVisible: false,
      currentMethod: {},
      currentUser: null,
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
      this.enabledMethods = this.mfaMethods.filter(m => m.enabled).length
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
    notifyUnboundUsers() {
      this.$message.success('已向未绑定用户发送提醒')
    },
    getMethodTag(method) {
      const tagMap = { totp: 'primary', sms: 'success', email: 'warning' }
      return tagMap[method] || 'info'
    },
    getMethodIcon(method) {
      const iconMap = { totp: 'el-icon-mobile-phone', sms: 'el-icon-message', email: 'el-icon-message-box' }
      return iconMap[method] || 'el-icon-question'
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
        this.enabledUsers--
        this.$message.success('MFA已重置')
      })
    },
    enforceMfa(row) {
      this.$confirm(`确定提醒用户 ${row.username} 启用MFA吗？`, '提示').then(() => {
        this.$message.success('提醒已发送')
      })
    },
    viewMfaDetail(row) {
      this.currentUser = row
      this.detailDialogVisible = true
    },
    viewBackupCodes() {
      this.$message.info('备用验证码：1234-5678, 8765-4321, ...')
    },
    tableHeaderStyle() {
      return { background: '#f5f7fa', fontWeight: 'bold', color: '#303133' }
    }
  }
}
</script>

<style lang="scss" scoped>
.mfa-page {
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

          &.enabled {
            background: #e6f7e6;
            color: #52c41a;
          }

          &.rate {
            background: #e6f4ff;
            color: #1890ff;
          }

          &.method {
            background: #f6ffed;
            color: #73d13d;
          }

          &.security {
            background: #fff2f0;
            color: #ff4d4f;
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

  // 方法配置
  .methods-section {
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

    .methods-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 16px;

      .method-card {
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

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 16px;

          .method-icon {
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
          .method-name {
            font-size: 16px;
            font-weight: 600;
            color: #1a1a1a;
            margin: 0 0 8px 0;
          }

          .method-desc {
            font-size: 13px;
            color: #999;
            margin-bottom: 16px;
            line-height: 1.5;
            min-height: 40px;
          }

          .method-stats {
            background: #f5f5f5;
            border-radius: 8px;
            padding: 12px;

            .stat-row {
              display: flex;
              justify-content: space-between;
              margin-bottom: 8px;

              &:last-child {
                margin-bottom: 0;
              }

              .stat-label {
                font-size: 13px;
                color: #666;
              }

              .stat-value {
                font-size: 13px;
                font-weight: 500;
                color: #1a1a1a;
              }
            }
          }
        }

        .card-footer {
          margin-top: 16px;
          padding-top: 16px;
          border-top: 1px solid #f0f0f0;
        }
      }
    }
  }

  // 策略卡片
  .policy-card {
    border-radius: 12px;
    margin-bottom: 24px;

    ::v-deep .el-card__header {
      font-weight: 600;
    }

    .policy-form {
      max-width: 800px;

      .form-hint {
        font-size: 12px;
        color: #999;
        margin-top: 4px;
      }

      .form-tip {
        margin-left: 10px;
        color: #909399;
      }
    }
  }

  // 用户卡片
  .user-card {
    border-radius: 12px;

    ::v-deep .el-card__header {
      font-weight: 600;
    }

    .user-cell {
      display: flex;
      align-items: center;
      gap: 12px;

      .user-info {
        .username {
          font-weight: 500;
          color: #1a1a1a;
        }

        .email {
          font-size: 12px;
          color: #999;
        }
      }
    }

    .text-muted {
      color: #999;
    }

    .pagination {
      margin-top: 16px;
      text-align: right;
    }
  }
}

// 响应式
@media (max-width: 1200px) {
  .mfa-page {
    .page-header {
      .stats-row {
        grid-template-columns: repeat(2, 1fr);
      }
    }

    .methods-section {
      .methods-grid {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
}

@media (max-width: 768px) {
  .mfa-page {
    padding: 16px;

    .page-header {
      .stats-row {
        grid-template-columns: 1fr;
      }
    }

    .methods-section {
      .methods-grid {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>
