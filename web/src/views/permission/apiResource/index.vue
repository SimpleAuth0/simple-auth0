<template>
  <div class="api-resource-page">

    <!-- 搜索区 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" size="small" class="search-form">

        <el-form-item label="API名称">
          <el-input v-model="searchForm.name" placeholder="请输入API名称" clearable />
        </el-form-item>

        <el-form-item label="请求路径">
          <el-input v-model="searchForm.path" placeholder="请输入请求路径" clearable />
        </el-form-item>

        <el-form-item label="请求方法">
          <el-select v-model="searchForm.method" clearable placeholder="全部方法" style="width: 120px">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
            <el-option label="PATCH" value="PATCH" />
          </el-select>
        </el-form-item>

        <div class="search-btn-group">
          <el-button type="primary" icon="el-icon-search" size="mini" @click="search">查询</el-button>
          <el-button size="mini" icon="el-icon-refresh" @click="reset">重置</el-button>
        </div>

      </el-form>
    </el-card>

    <!-- 列表卡片 -->
    <el-card class="table-card" shadow="hover">

      <!-- 顶部操作行 -->
      <div class="table-header-bar">
        <el-button
          size="small"
          type="primary"
          icon="el-icon-plus"
          class="add-btn"
          @click="addResource"
        >
          新增API资源
        </el-button>
        <el-button
          size="small"
          type="danger"
          icon="el-icon-delete"
          plain
          :disabled="selectedRows.length === 0"
          @click="batchDelete"
        >
          批量删除
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        class="resource-table"
        :header-cell-style="tableHeaderStyle"
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="API名称" min-width="150" />
        <el-table-column prop="path" label="请求路径" min-width="200">
          <template slot-scope="{ row }">
            <code class="api-path">{{ row.path }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="method" label="方法" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="getMethodType(row.method)" size="small">{{ row.method }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="editResource(row)">编辑</el-button>
            <el-popconfirm
              title="确定删除该API资源吗？"
              @confirm="deleteResource(row)"
            >
              <el-button slot="reference" size="mini" type="text" class="danger" icon="el-icon-delete">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-box">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          :total="total"
          :current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>

    </el-card>

    <!-- 新增/编辑API资源对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="550px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="resourceForm"
        :model="resourceForm"
        :rules="resourceRules"
        label-width="100px"
        class="resource-form"
      >
        <el-form-item label="API名称" prop="name">
          <el-input v-model="resourceForm.name" placeholder="请输入API名称" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="请求路径" prop="path">
          <el-input v-model="resourceForm.path" placeholder="例如：/api/v1/users">
            <template slot="prepend">{{ baseUrl }}</template>
          </el-input>
        </el-form-item>

        <el-form-item label="请求方法" prop="method">
          <el-radio-group v-model="resourceForm.method">
            <el-radio-button label="GET" />
            <el-radio-button label="POST" />
            <el-radio-button label="PUT" />
            <el-radio-button label="DELETE" />
            <el-radio-button label="PATCH" />
          </el-radio-group>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="resourceForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入API描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'ApiResource',
  data() {
    return {
      baseUrl: '/api/v1',
      loading: false,
      searchForm: {
        name: '',
        path: '',
        method: ''
      },
      tableData: [
        { id: 1, name: '用户列表', path: '/users', method: 'GET', description: '获取用户列表', createTime: '2023-04-10 10:00:00' },
        { id: 2, name: '创建用户', path: '/users', method: 'POST', description: '创建新用户', createTime: '2023-04-10 10:00:00' },
        { id: 3, name: '更新用户', path: '/users/{id}', method: 'PUT', description: '更新用户信息', createTime: '2023-04-10 10:00:00' },
        { id: 4, name: '删除用户', path: '/users/{id}', method: 'DELETE', description: '删除用户', createTime: '2023-04-10 10:00:00' },
        { id: 5, name: '角色列表', path: '/roles', method: 'GET', description: '获取角色列表', createTime: '2023-04-11 09:00:00' },
        { id: 6, name: '创建角色', path: '/roles', method: 'POST', description: '创建新角色', createTime: '2023-04-11 09:00:00' }
      ],
      pageSize: 10,
      currentPage: 1,
      total: 6,
      selectedRows: [],

      // 对话框
      dialogVisible: false,
      dialogTitle: '新增API资源',
      isEdit: false,
      submitLoading: false,
      currentId: null,

      // 表单
      resourceForm: {
        name: '',
        path: '',
        method: 'GET',
        description: ''
      },
      resourceRules: {
        name: [
          { required: true, message: '请输入API名称', trigger: 'blur' }
        ],
        path: [
          { required: true, message: '请输入请求路径', trigger: 'blur' },
          { pattern: /^\/.*/, message: '路径必须以/开头', trigger: 'blur' }
        ],
        method: [
          { required: true, message: '请选择请求方法', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    search() {
      this.loading = true
      setTimeout(() => {
        this.loading = false
        this.$message.success('查询成功')
      }, 500)
    },
    reset() {
      this.searchForm = {
        name: '',
        path: '',
        method: ''
      }
      this.search()
    },
    addResource() {
      this.dialogTitle = '新增API资源'
      this.isEdit = false
      this.currentId = null
      this.resetForm()
      this.dialogVisible = true
    },
    editResource(row) {
      this.dialogTitle = '编辑API资源'
      this.isEdit = true
      this.currentId = row.id
      this.resourceForm = {
        name: row.name,
        path: row.path,
        method: row.method,
        description: row.description || ''
      }
      this.dialogVisible = true
    },
    deleteResource(row) {
      const index = this.tableData.findIndex(item => item.id === row.id)
      if (index > -1) {
        this.tableData.splice(index, 1)
        this.total--
        this.$message.success('删除成功')
      }
    },
    batchDelete() {
      this.$confirm(`确定删除选中的 ${this.selectedRows.length} 个API资源吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        const ids = this.selectedRows.map(row => row.id)
        this.tableData = this.tableData.filter(item => !ids.includes(item.id))
        this.total -= ids.length
        this.selectedRows = []
        this.$message.success('批量删除成功')
      }).catch(() => {})
    },
    getMethodType(method) {
      const typeMap = {
        'GET': 'success',
        'POST': 'primary',
        'PUT': 'warning',
        'DELETE': 'danger',
        'PATCH': 'info'
      }
      return typeMap[method] || 'info'
    },
    pageChange(p) {
      this.currentPage = p
      this.search()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.search()
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection
    },
    tableHeaderStyle() {
      return { background: '#f5f7fa', fontWeight: 'bold', color: '#303133' }
    },
    resetForm() {
      this.resourceForm = {
        name: '',
        path: '',
        method: 'GET',
        description: ''
      }
      this.$nextTick(() => {
        this.$refs.resourceForm && this.$refs.resourceForm.clearValidate()
      })
    },
    handleDialogClose() {
      this.resetForm()
    },
    submitForm() {
      this.$refs.resourceForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          setTimeout(() => {
            this.submitLoading = false
            if (this.isEdit) {
              const index = this.tableData.findIndex(item => item.id === this.currentId)
              if (index > -1) {
                this.tableData[index] = {
                  ...this.tableData[index],
                  ...this.resourceForm
                }
              }
              this.$message.success('修改成功')
            } else {
              const newResource = {
                id: this.tableData.length + 1,
                ...this.resourceForm,
                createTime: new Date().toLocaleString()
              }
              this.tableData.unshift(newResource)
              this.total++
              this.$message.success('新增成功')
            }
            this.dialogVisible = false
          }, 800)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.api-resource-page {
  padding: 20px;

  .search-card {
    margin-bottom: 16px;
    border-radius: 10px;
  }

  .search-form {
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .el-form-item {
      margin-right: 20px;
      margin-bottom: 12px;
    }

    .search-btn-group {
      display: flex;
      align-items: center;
      margin-left: auto;

      button {
        margin-left: 8px;
      }
    }
  }

  .table-card {
    border-radius: 10px;
    padding-bottom: 10px;

    .table-header-bar {
      display: flex;
      justify-content: flex-start;
      gap: 10px;
      margin-bottom: 12px;

      .add-btn {
        border: none !important;
        background-color: #409eff20;
        color: #409eff;
        font-weight: 500;
      }

      .add-btn:hover {
        background-color: #409eff30;
      }
    }

    .resource-table {
      width: 100%;
      border: none;

      ::v-deep .el-table__body-wrapper {
        border-radius: 6px;
      }

      ::v-deep .el-table--enable-row-hover tr:hover > td {
        background-color: #f5f7fa !important;
      }

      .api-path {
        background: #f5f7fa;
        padding: 2px 8px;
        border-radius: 4px;
        font-family: monospace;
        font-size: 12px;
      }
    }

    .pagination-box {
      margin-top: 15px;
      text-align: right;
    }
  }
}

.resource-form {
  .el-radio-group {
    .el-radio-button__inner {
      padding: 8px 15px;
    }
  }
}

.danger {
  color: #f56c6c;
}

.danger:hover {
  color: #f78989;
}
</style>
