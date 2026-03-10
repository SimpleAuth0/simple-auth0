<template>
  <div class="attack-protection-page">

    <!-- 攻击防护开关卡片 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in protectionModules" :key="item.key">
        <el-card class="protection-card" shadow="hover">
          <div class="protection-header">
            <div class="protection-icon" :style="{ background: item.color }">
              <i :class="item.icon" />
            </div>
            <el-switch v-model="item.enabled" @change="handleModuleChange(item)" />
          </div>
          <div class="protection-title">{{ item.name }}</div>
          <div class="protection-desc">{{ item.description }}</div>
          <div class="protection-stats" v-if="item.enabled">
            <span class="stat-item">
              <span class="stat-label">今日拦截</span>
              <span class="stat-value" :style="{ color: item.color }">{{ item.todayBlocked }}</span>
            </span>
            <span class="stat-item">
              <span class="stat-label">累计拦截</span>
              <span class="stat-value">{{ item.totalBlocked }}</span>
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 防护配置 -->
    <el-card class="config-card" shadow="hover">
      <div slot="header">
        <span>防护配置</span>
      </div>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="IP封禁" name="ip">
          <div class="tab-header">
            <el-button type="primary" size="small" icon="el-icon-plus" @click="addIpBlock">添加封禁IP</el-button>
            <el-button size="small" icon="el-icon-upload2" @click="importIpList">批量导入</el-button>
          </div>

          <el-table :data="ipBlockList" size="small" class="config-table">
            <el-table-column prop="ip" label="IP地址" width="150" />
            <el-table-column prop="type" label="类型" width="100">
              <template slot-scope="{ row }">
                <el-tag :type="row.type === 'manual' ? 'warning' : 'info'" size="mini">
                  {{ row.type === 'manual' ? '手动' : '自动' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="封禁原因" min-width="200" show-overflow-tooltip />
            <el-table-column prop="blockTime" label="封禁时间" width="160" />
            <el-table-column prop="expireTime" label="过期时间" width="160" />
            <el-table-column prop="status" label="状态" width="80">
              <template slot-scope="{ row }">
                <el-tag :type="row.status === 'active' ? 'danger' : 'success'" size="mini">
                  {{ row.status === 'active' ? '封禁中' : '已解封' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template slot-scope="{ row }">
                <el-button v-if="row.status === 'active'" size="mini" type="text" @click="unblockIp(row)">解封</el-button>
                <el-button size="mini" type="text" class="danger" @click="deleteIpBlock(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="ipBlockTotal"
              :page-size="10"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="请求限流" name="rate">
          <el-form :model="rateLimitConfig" label-width="150px" class="config-form">
            <el-form-item label="全局QPS限制">
              <el-input-number v-model="rateLimitConfig.globalQps" :min="100" :max="10000" :step="100" />
              <span class="form-tip">请求/秒</span>
            </el-form-item>
            <el-form-item label="单IP QPS限制">
              <el-input-number v-model="rateLimitConfig.ipQps" :min="10" :max="1000" :step="10" />
              <span class="form-tip">请求/秒</span>
            </el-form-item>
            <el-form-item label="单用户 QPS限制">
              <el-input-number v-model="rateLimitConfig.userQps" :min="10" :max="500" :step="10" />
              <span class="form-tip">请求/秒</span>
            </el-form-item>
            <el-form-item label="限流触发动作">
              <el-radio-group v-model="rateLimitConfig.action">
                <el-radio label="block">拒绝请求</el-radio>
                <el-radio label="captcha">要求验证码</el-radio>
                <el-radio label="delay">延迟响应</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveRateLimitConfig">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="敏感词过滤" name="sensitive">
          <div class="tab-header">
            <el-button type="primary" size="small" icon="el-icon-plus" @click="addSensitiveWord">添加敏感词</el-button>
            <el-button size="small" icon="el-icon-upload2" @click="importWords">批量导入</el-button>
          </div>

          <el-tag
            v-for="word in sensitiveWords"
            :key="word.id"
            closable
            :type="word.level === 'high' ? 'danger' : 'warning'"
            @close="removeSensitiveWord(word)"
            class="sensitive-tag"
          >
            {{ word.word }}
          </el-tag>

          <el-empty v-if="sensitiveWords.length === 0" description="暂无敏感词" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 攻击日志 -->
    <el-card class="log-card" shadow="hover">
      <div slot="header">
        <span>攻击拦截日志</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshLogs">刷新</el-button>
      </div>

      <el-table :data="attackLogs" size="small" class="log-table">
        <el-table-column prop="time" label="时间" width="160" />
        <el-table-column prop="type" label="攻击类型" width="120">
          <template slot-scope="{ row }">
            <el-tag :type="getAttackTypeTag(row.type)" size="mini">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="来源IP" width="130" />
        <el-table-column prop="target" label="攻击目标" min-width="200" show-overflow-tooltip />
        <el-table-column prop="action" label="处理动作" width="100">
          <template slot-scope="{ row }">
            <span :class="{ 'text-danger': row.action === 'blocked' }">
              {{ row.action === 'blocked' ? '已拦截' : '已告警' }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="logTotal"
          :page-size="10"
        />
      </div>
    </el-card>

    <!-- 添加IP封禁对话框 -->
    <el-dialog title="添加封禁IP" :visible.sync="ipDialogVisible" width="450px">
      <el-form :model="ipForm" label-width="100px" :rules="ipRules" ref="ipForm">
        <el-form-item label="IP地址" prop="ip">
          <el-input v-model="ipForm.ip" placeholder="支持单个IP或CIDR格式" />
        </el-form-item>
        <el-form-item label="封禁原因" prop="reason">
          <el-input v-model="ipForm.reason" type="textarea" :rows="2" placeholder="请输入封禁原因" />
        </el-form-item>
        <el-form-item label="封禁时长">
          <el-select v-model="ipForm.duration" style="width: 100%">
            <el-option label="1小时" value="1h" />
            <el-option label="24小时" value="24h" />
            <el-option label="7天" value="7d" />
            <el-option label="30天" value="30d" />
            <el-option label="永久" value="forever" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="ipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitIpBlock">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'AttackProtection',
  data() {
    return {
      protectionModules: [
        { key: 'sql', name: 'SQL注入防护', icon: 'el-icon-coin', color: '#f56c6c', enabled: true, description: '防止恶意SQL注入攻击', todayBlocked: 12, totalBlocked: 1258 },
        { key: 'xss', name: 'XSS攻击防护', icon: 'el-icon-warning', color: '#e6a23c', enabled: true, description: '防止跨站脚本攻击', todayBlocked: 8, totalBlocked: 892 },
        { key: 'csrf', name: 'CSRF防护', icon: 'el-icon-lock', color: '#67c23a', enabled: true, description: '防止跨站请求伪造', todayBlocked: 3, totalBlocked: 456 },
        { key: 'brute', name: '暴力破解防护', icon: 'el-icon-key', color: '#409eff', enabled: true, description: '防止密码暴力破解', todayBlocked: 25, totalBlocked: 2341 }
      ],
      activeTab: 'ip',
      ipBlockList: [
        { id: 1, ip: '192.168.1.100', type: 'auto', reason: '暴力破解攻击', blockTime: '2023-12-01 10:30:00', expireTime: '2023-12-08 10:30:00', status: 'active' },
        { id: 2, ip: '10.0.0.50', type: 'manual', reason: '恶意扫描', blockTime: '2023-12-01 09:15:00', expireTime: '永久', status: 'active' }
      ],
      ipBlockTotal: 2,
      rateLimitConfig: {
        globalQps: 1000,
        ipQps: 100,
        userQps: 60,
        action: 'captcha'
      },
      sensitiveWords: [
        { id: 1, word: '恶意代码', level: 'high' },
        { id: 2, word: '攻击', level: 'medium' },
        { id: 3, word: '入侵', level: 'high' }
      ],
      attackLogs: [
        { time: '2023-12-01 14:30:25', type: 'SQL注入', ip: '192.168.1.100', target: '/api/users?id=1 OR 1=1', action: 'blocked' },
        { time: '2023-12-01 14:28:10', type: 'XSS攻击', ip: '10.0.0.55', target: '/api/comments', action: 'blocked' },
        { time: '2023-12-01 14:25:33', type: '暴力破解', ip: '172.16.0.20', target: '/login', action: 'blocked' }
      ],
      logTotal: 3,
      ipDialogVisible: false,
      ipForm: {
        ip: '',
        reason: '',
        duration: '24h'
      },
      ipRules: {
        ip: [{ required: true, message: '请输入IP地址', trigger: 'blur' }],
        reason: [{ required: true, message: '请输入封禁原因', trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleModuleChange(item) {
      const status = item.enabled ? '启用' : '禁用'
      this.$message.success(`${item.name}已${status}`)
    },
    addIpBlock() {
      this.ipForm = { ip: '', reason: '', duration: '24h' }
      this.ipDialogVisible = true
    },
    submitIpBlock() {
      this.$refs.ipForm.validate(valid => {
        if (valid) {
          this.ipBlockList.unshift({
            id: Date.now(),
            ip: this.ipForm.ip,
            type: 'manual',
            reason: this.ipForm.reason,
            blockTime: new Date().toLocaleString(),
            expireTime: this.ipForm.duration === 'forever' ? '永久' : this.ipForm.duration,
            status: 'active'
          })
          this.ipBlockTotal++
          this.ipDialogVisible = false
          this.$message.success('IP封禁成功')
        }
      })
    },
    unblockIp(row) {
      row.status = 'unblocked'
      this.$message.success('IP已解封')
    },
    deleteIpBlock(row) {
      const index = this.ipBlockList.findIndex(item => item.id === row.id)
      if (index > -1) {
        this.ipBlockList.splice(index, 1)
        this.ipBlockTotal--
        this.$message.success('删除成功')
      }
    },
    importIpList() {
      this.$message.info('批量导入功能')
    },
    saveRateLimitConfig() {
      this.$message.success('限流配置已保存')
    },
    addSensitiveWord() {
      this.$prompt('请输入敏感词', '添加敏感词', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        this.sensitiveWords.push({
          id: Date.now(),
          word: value,
          level: 'medium'
        })
        this.$message.success('敏感词添加成功')
      })
    },
    importWords() {
      this.$message.info('批量导入敏感词')
    },
    removeSensitiveWord(word) {
      const index = this.sensitiveWords.findIndex(item => item.id === word.id)
      if (index > -1) {
        this.sensitiveWords.splice(index, 1)
        this.$message.success('敏感词已删除')
      }
    },
    getAttackTypeTag(type) {
      const tagMap = { 'SQL注入': 'danger', 'XSS攻击': 'warning', '暴力破解': 'primary', 'CSRF': 'success' }
      return tagMap[type] || 'info'
    },
    refreshLogs() {
      this.$message.success('日志已刷新')
    }
  }
}
</script>

<style lang="scss" scoped>
.attack-protection-page {
  padding: 20px;

  .protection-card {
    border-radius: 10px;
    margin-bottom: 20px;

    .protection-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      .protection-icon {
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

    .protection-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }

    .protection-desc {
      font-size: 13px;
      color: #909399;
      margin-bottom: 15px;
    }

    .protection-stats {
      display: flex;
      gap: 20px;
      padding-top: 15px;
      border-top: 1px solid #ebeef5;

      .stat-item {
        display: flex;
        flex-direction: column;

        .stat-label {
          font-size: 12px;
          color: #909399;
        }

        .stat-value {
          font-size: 20px;
          font-weight: 600;
          margin-top: 4px;
        }
      }
    }
  }

  .config-card,
  .log-card {
    border-radius: 10px;
    margin-bottom: 20px;

    .tab-header {
      margin-bottom: 15px;
    }

    .config-form {
      max-width: 500px;

      .form-tip {
        margin-left: 10px;
        color: #909399;
      }
    }

    .config-table,
    .log-table {
      ::v-deep .el-table__header th {
        background: #f5f7fa;
        font-weight: bold;
      }
    }

    .sensitive-tag {
      margin: 0 10px 10px 0;
    }

    .pagination {
      margin-top: 15px;
      text-align: right;
    }
  }

  .text-danger {
    color: #f56c6c;
  }
}
</style>
