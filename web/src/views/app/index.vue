<template>
  <div class="app-page">

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" size="small">

        <el-form-item label="应用名称">
          <el-input v-model="query.name" placeholder="输入名称或 ClientId" />
        </el-form-item>

        <el-form-item label="应用类型">
          <el-select v-model="query.type" clearable placeholder="全部类型">
            <el-option label="传统WEB应用" value="web" />
            <el-option label="单页应用" value="spa" />
            <el-option label="移动应用" value="mobile" />
            <el-option label="M2M应用" value="m2m" />
            <el-option label="标准应用" value="std" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部">
            <el-option label="启用" value="enabled" />
            <el-option label="停用" value="disabled" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
        </el-form-item>

        <el-form-item style="float:right">
          <el-button type="primary" icon="el-icon-plus" @click="openCreateDialog">
            新增应用
          </el-button>
        </el-form-item>

      </el-form>
    </el-card>

    <!-- 应用列表 -->
    <el-card class="table-card">
      <div class="card-title">应用列表</div>

      <el-table :data="appList" class="app-table" highlight-current-row>
        <el-table-column label="应用名称" prop="name" min-width="150" />
        <el-table-column label="Client ID" prop="clientId" min-width="200" />

        <el-table-column label="应用类型" prop="type" width="160">
          <template slot-scope="{ row }">
            <el-tag size="mini">{{ row.type }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="协议类型" prop="protocolType" width="160" />

        <el-table-column label="状态" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" size="mini">
              {{ row.enabled ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="detailApp(row)">详情</el-button>
            <el-button size="mini" type="text" @click="editApp(row)">编辑</el-button>
            <el-button size="mini" type="text" @click="enableApp(row)">禁用</el-button>
            <el-button size="mini" type="text" class="danger" @click="deleteApp(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :page-size="10"
          :total="total"
        />
      </div>
    </el-card>

  </div>
</template>

<script>
export default {
  name: 'ApplicationList',

  data() {
    return {
      query: {
        name: '',
        type: '',
        status: ''
      },

      appList: [
        {
          name: '后台管理系统',
          clientId: 'abcd-1234-efgh',
          type: 'Web',
          protocolType: 'OIDC',
          enabled: true
        },
        {
          name: '自动化脚本',
          clientId: 'xxxx-8888-0000',
          type: 'Machine-to-Machine',
          protocolType: 'CAS',
          enabled: false
        }
      ],

      total: 2
    }
  },

  methods: {
    search() {
      this.$message.success('已更新搜索结果')
    },

    openCreateDialog() {
      this.$router.push('/app/create')
    },

    detailApp(appId) {
      this.$router.push({
        name: 'AppDetail',
        params: { id: appId },
        query: { from: 'create' }
      })
    },

    editApp(app) {
      this.$message.info('打开编辑应用：' + app.name)
    },

    enableApp(app) {
      this.$confirm(`确定禁用应用？`, '提示', { type: 'warning' })
        .then(() => this.$message.success('已禁用'))
        .catch(() => {
        })
    },

    deleteApp(app) {
      this.$confirm(`确定删除应用：${app.name}？`, '提示', { type: 'warning' })
        .then(() => this.$message.success('已删除'))
        .catch(() => {
        })
    }
  }
}
</script>

<style scoped lang="scss">
.app-page {
  padding: 20px;
}

/* 搜索栏样式 */
.search-card {
  margin-bottom: 20px;
  border-radius: 10px;
}

/* 表格卡片 */
.table-card {
  border-radius: 10px;

  .card-title {
    font-size: 18px;
    font-weight: 600;
    padding-bottom: 12px;
  }
}

.app-table .more {
  color: #409eff;
  font-size: 12px;
  margin-left: 5px;
}

.empty {
  color: #999;
  font-size: 12px;
}

.danger {
  color: #f56c6c !important;
}

.pagination {
  text-align: right;
  margin-top: 15px;
}
</style>
