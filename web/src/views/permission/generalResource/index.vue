<template>
  <div class="general-resource-page">

    <!-- 搜索区 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" size="small" class="search-form">

        <el-form-item label="资源名称">
          <el-input v-model="searchForm.name" placeholder="请输入资源名称" clearable />
        </el-form-item>

        <el-form-item label="资源类型">
          <el-select v-model="searchForm.type" clearable placeholder="全部类型" style="width: 140px">
            <el-option label="菜单" value="menu" />
            <el-option label="按钮" value="button" />
            <el-option label="页面元素" value="element" />
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
          新增资源
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

      <!-- 树形表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        class="resource-table"
        :header-cell-style="tableHeaderStyle"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="资源名称" min-width="200">
          <template slot-scope="{ row }">
            <i :class="getResourceIcon(row.type)" style="margin-right: 8px; color: #909399;" />
            <span>{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="资源编码" width="180" />
        <el-table-column prop="type" label="类型" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="getTypeTag(row.type)" size="small">{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由/权限标识" min-width="200">
          <template slot-scope="{ row }">
            <code v-if="row.path" class="resource-path">{{ row.path }}</code>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{ row }">
            <el-switch v-model="row.status" active-value="enabled" inactive-value="disabled" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" icon="el-icon-plus" @click="addChild(row)">添加子项</el-button>
            <el-button size="mini" type="text" icon="el-icon-edit" @click="editResource(row)">编辑</el-button>
            <el-popconfirm
              title="确定删除该资源吗？"
              @confirm="deleteResource(row)"
            >
              <el-button slot="reference" size="mini" type="text" class="danger" icon="el-icon-delete">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

    </el-card>

    <!-- 新增/编辑资源对话框 -->
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
        <el-form-item label="上级资源">
          <el-cascader
            v-model="resourceForm.parentId"
            :options="resourceOptions"
            :props="{ checkStrictly: true, value: 'id', label: 'name' }"
            clearable
            style="width: 100%"
            placeholder="顶级资源"
          />
        </el-form-item>

        <el-form-item label="资源类型" prop="type">
          <el-radio-group v-model="resourceForm.type">
            <el-radio-button label="menu">菜单</el-radio-button>
            <el-radio-button label="button">按钮</el-radio-button>
            <el-radio-button label="element">页面元素</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="资源名称" prop="name">
          <el-input v-model="resourceForm.name" placeholder="请输入资源名称" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="资源编码" prop="code">
          <el-input v-model="resourceForm.code" placeholder="请输入资源编码" maxlength="50" />
        </el-form-item>

        <el-form-item label="路由/权限" prop="path">
          <el-input v-model="resourceForm.path" placeholder="菜单填路由，按钮填权限标识" />
        </el-form-item>

        <el-form-item label="图标" v-if="resourceForm.type === 'menu'">
          <el-input v-model="resourceForm.icon" placeholder="请输入图标类名，如：el-icon-user" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="resourceForm.sort" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="resourceForm.status">
                <el-radio label="enabled">启用</el-radio>
                <el-radio label="disabled">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
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
  name: 'GeneralResource',
  data() {
    return {
      loading: false,
      searchForm: {
        name: '',
        type: ''
      },
      tableData: [
        {
          id: 1,
          name: '系统管理',
          code: 'SYSTEM',
          type: 'menu',
          path: '/system',
          icon: 'el-icon-setting',
          sort: 1,
          status: 'enabled',
          children: [
            {
              id: 11,
              name: '用户管理',
              code: 'USER_MGMT',
              type: 'menu',
              path: '/system/user',
              icon: 'el-icon-user',
              sort: 1,
              status: 'enabled',
              children: [
                { id: 111, name: '新增', code: 'USER_ADD', type: 'button', path: 'user:add', sort: 1, status: 'enabled' },
                { id: 112, name: '编辑', code: 'USER_EDIT', type: 'button', path: 'user:edit', sort: 2, status: 'enabled' },
                { id: 113, name: '删除', code: 'USER_DEL', type: 'button', path: 'user:delete', sort: 3, status: 'enabled' }
              ]
            },
            {
              id: 12,
              name: '角色管理',
              code: 'ROLE_MGMT',
              type: 'menu',
              path: '/system/role',
              icon: 'el-icon-s-custom',
              sort: 2,
              status: 'enabled'
            }
          ]
        },
        {
          id: 2,
          name: '应用管理',
          code: 'APP_MGMT',
          type: 'menu',
          path: '/app',
          icon: 'el-icon-menu',
          sort: 2,
          status: 'enabled'
        }
      ],
      selectedRows: [],

      // 对话框
      dialogVisible: false,
      dialogTitle: '新增资源',
      isEdit: false,
      submitLoading: false,
      currentId: null,

      // 表单
      resourceForm: {
        parentId: null,
        type: 'menu',
        name: '',
        code: '',
        path: '',
        icon: '',
        sort: 0,
        status: 'enabled'
      },
      resourceRules: {
        type: [
          { required: true, message: '请选择资源类型', trigger: 'change' }
        ],
        name: [
          { required: true, message: '请输入资源名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入资源编码', trigger: 'blur' }
        ]
      },
      resourceOptions: []
    }
  },
  mounted() {
    this.generateResourceOptions()
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
      this.searchForm = { name: '', type: '' }
      this.search()
    },
    addResource() {
      this.dialogTitle = '新增资源'
      this.isEdit = false
      this.currentId = null
      this.resetForm()
      this.dialogVisible = true
    },
    addChild(row) {
      this.dialogTitle = '新增子资源'
      this.isEdit = false
      this.currentId = null
      this.resetForm()
      this.resourceForm.parentId = row.id
      this.dialogVisible = true
    },
    editResource(row) {
      this.dialogTitle = '编辑资源'
      this.isEdit = true
      this.currentId = row.id
      this.resourceForm = {
        parentId: row.parentId,
        type: row.type,
        name: row.name,
        code: row.code,
        path: row.path || '',
        icon: row.icon || '',
        sort: row.sort || 0,
        status: row.status
      }
      this.dialogVisible = true
    },
    deleteResource(row) {
      this.$message.success('删除成功')
    },
    batchDelete() {
      this.$confirm(`确定删除选中的 ${this.selectedRows.length} 个资源吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        this.selectedRows = []
        this.$message.success('批量删除成功')
      }).catch(() => {})
    },
    handleStatusChange(row) {
      const statusText = row.status === 'enabled' ? '启用' : '禁用'
      this.$message.success(`资源 ${row.name} 已${statusText}`)
    },
    getResourceIcon(type) {
      const iconMap = {
        'menu': 'el-icon-folder-opened',
        'button': 'el-icon-mouse',
        'element': 'el-icon-s-operation'
      }
      return iconMap[type] || 'el-icon-question'
    },
    getTypeTag(type) {
      const tagMap = { 'menu': '', 'button': 'success', 'element': 'warning' }
      return tagMap[type] || 'info'
    },
    getTypeText(type) {
      const textMap = { 'menu': '菜单', 'button': '按钮', 'element': '页面元素' }
      return textMap[type] || type
    },
    generateResourceOptions() {
      // 生成级联选择器的数据
      const flatten = (data) => {
        return data.map(item => {
          const result = { id: item.id, name: item.name }
          if (item.children && item.children.length) {
            result.children = flatten(item.children)
          }
          return result
        })
      }
      this.resourceOptions = flatten(this.tableData)
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection
    },
    tableHeaderStyle() {
      return { background: '#f5f7fa', fontWeight: 'bold', color: '#303133' }
    },
    resetForm() {
      this.resourceForm = {
        parentId: null,
        type: 'menu',
        name: '',
        code: '',
        path: '',
        icon: '',
        sort: 0,
        status: 'enabled'
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
            this.dialogVisible = false
            this.$message.success(this.isEdit ? '修改成功' : '新增成功')
          }, 800)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.general-resource-page {
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

      .resource-path {
        background: #f5f7fa;
        padding: 2px 8px;
        border-radius: 4px;
        font-family: monospace;
        font-size: 12px;
      }
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
