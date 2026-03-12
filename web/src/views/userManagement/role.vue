<template>
  <div class="role-page">

    <!-- 搜索区 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" size="small" class="search-form">

        <!-- 常规搜索 -->
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>

        <el-form-item label="描述">
          <el-input v-model="searchForm.description" placeholder="请输入描述" clearable />
        </el-form-item>

        <div class="search-btn-group">
          <el-button type="primary" icon="el-icon-search" size="mini" @click="search">查询</el-button>
          <el-button size="mini" icon="el-icon-refresh" @click="reset">重置</el-button>
          <el-button size="mini" type="text" class="toggle-advanced-btn" @click="toggleAdvanced">
            <i :class="showAdvanced ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" />
            高级搜索
          </el-button>
        </div>

        <!-- 折叠高级搜索 -->
        <transition name="slide-fade">
          <div v-show="showAdvanced" class="advanced-search">
            <el-form-item label="创建时间">
              <el-date-picker
                v-model="searchForm.createTime"
                type="daterange"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                unlink-panels
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </div>
        </transition>

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
          @click="addRole"
        >
          新增角色
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
        class="role-table"
        :header-cell-style="tableHeaderStyle"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" min-width="150">
          <template slot-scope="{ row }">
            <div class="role-name-cell">
              <el-tag :type="getRoleType(row.roleCode)" size="small" effect="dark">
                {{ row.roleName.charAt(0) }}
              </el-tag>
              <span class="role-name">{{ row.roleName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="permissionCount" label="权限数" width="100">
          <template slot-scope="{ row }">
            <el-button type="text" @click="viewPermissions(row)">{{ row.permissionCount }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="userCount" label="用户数" width="100">
          <template slot-scope="{ row }">
            <el-button type="text" @click="viewUsers(row)">{{ row.userCount }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="editRole(row)">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-s-check" @click="assignPermissions(row)">分配权限</el-button>
            <el-popconfirm
              title="确定删除该角色吗？"
              @confirm="deleteRole(row)"
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

    <!-- 新增/编辑角色对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="550px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="roleForm"
        :model="roleForm"
        :rules="roleRules"
        label-width="100px"
        class="role-form"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码" :disabled="isEdit" maxlength="50">
            <template slot="prepend">ROLE_</template>
          </el-input>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
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

    <!-- 分配权限对话框 -->
    <el-dialog
      title="分配权限"
      :visible.sync="permissionDialogVisible"
      width="600px"
    >
      <div class="permission-manage">
        <el-tabs v-model="activePermissionTab">
          <el-tab-pane label="API权限" name="api">
            <el-tree
              ref="apiTree"
              :data="apiPermissions"
              show-checkbox
              node-key="id"
              :default-expand-all="true"
              :props="{ label: 'name', children: 'children' }"
            />
          </el-tab-pane>
          <el-tab-pane label="菜单权限" name="menu">
            <el-tree
              ref="menuTree"
              :data="menuPermissions"
              show-checkbox
              node-key="id"
              :default-expand-all="true"
              :props="{ label: 'name', children: 'children' }"
            />
          </el-tab-pane>
          <el-tab-pane label="数据权限" name="data">
            <el-radio-group v-model="dataPermission">
              <el-radio label="all">全部数据</el-radio>
              <el-radio label="dept">本部门数据</el-radio>
              <el-radio label="self">仅本人数据</el-radio>
              <el-radio label="custom">自定义</el-radio>
            </el-radio-group>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div slot="footer">
        <el-button @click="permissionDialogVisible = false">关 闭</el-button>
        <el-button type="primary" :loading="permissionLoading" @click="savePermissions">保 存</el-button>
      </div>
    </el-dialog>

    <!-- 查看权限对话框 -->
    <el-dialog
      title="角色权限列表"
      :visible.sync="permissionListVisible"
      width="500px"
    >
      <el-tree :data="currentPermissions" :props="{ label: 'name', children: 'children' }" />
      <div slot="footer">
        <el-button @click="permissionListVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 查看用户对话框 -->
    <el-dialog
      title="角色用户列表"
      :visible.sync="userListVisible"
      width="600px"
    >
      <el-table :data="currentUsers" size="small">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
      </el-table>
      <div slot="footer">
        <el-button @click="userListVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'RoleList',
  data() {
    return {
      loading: false,
      showAdvanced: false,
      searchForm: {
        roleName: '',
        description: '',
        createTime: null
      },
      tableData: [
        { id: 1, roleName: '系统管理员', roleCode: 'ADMIN', description: '拥有全部权限', permissionCount: 52, userCount: 2, createTime: '2023-04-10 10:00:00' },
        { id: 2, roleName: '普通用户', roleCode: 'USER', description: '仅可访问基础功能', permissionCount: 15, userCount: 25, createTime: '2023-05-01 09:00:00' },
        { id: 3, roleName: '审计员', roleCode: 'AUDITOR', description: '仅可查看日志和报表', permissionCount: 8, userCount: 3, createTime: '2023-06-15 11:30:00' }
      ],
      pageSize: 10,
      currentPage: 1,
      total: 3,
      selectedRows: [],

      // 对话框相关
      dialogVisible: false,
      dialogTitle: '新增角色',
      isEdit: false,
      submitLoading: false,
      currentId: null,

      // 角色表单
      roleForm: {
        roleName: '',
        roleCode: '',
        description: ''
      },
      roleRules: {
        roleName: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        roleCode: [
          { required: true, message: '请输入角色编码', trigger: 'blur' },
          { pattern: /^[A-Z_]+$/, message: '只能包含大写字母和下划线', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
        ]
      },

      // 权限管理
      permissionDialogVisible: false,
      permissionLoading: false,
      activePermissionTab: 'api',
      dataPermission: 'all',
      apiPermissions: [
        { id: 1, name: '用户管理', children: [
          { id: 11, name: '用户查询' },
          { id: 12, name: '用户新增' },
          { id: 13, name: '用户编辑' },
          { id: 14, name: '用户删除' }
        ] },
        { id: 2, name: '角色管理', children: [
          { id: 21, name: '角色查询' },
          { id: 22, name: '角色新增' },
          { id: 23, name: '角色编辑' },
          { id: 24, name: '角色删除' }
        ] }
      ],
      menuPermissions: [
        { id: 1, name: '首页', children: [] },
        { id: 2, name: '应用管理', children: [
          { id: 21, name: '应用列表' },
          { id: 22, name: '创建应用' }
        ] },
        { id: 3, name: '用户管理', children: [
          { id: 31, name: '用户列表' },
          { id: 32, name: '用户组' },
          { id: 33, name: '角色' }
        ] }
      ],

      // 查看权限
      permissionListVisible: false,
      currentPermissions: [],

      // 查看用户
      userListVisible: false,
      currentUsers: []
    }
  },
  methods: {
    toggleAdvanced() {
      this.showAdvanced = !this.showAdvanced
    },
    search() {
      this.loading = true
      setTimeout(() => {
        this.loading = false
        this.$message.success('查询成功')
      }, 500)
    },
    reset() {
      this.searchForm = { roleName: '', description: '', createTime: null }
      this.search()
    },
    addRole() {
      this.dialogTitle = '新增角色'
      this.isEdit = false
      this.currentId = null
      this.resetForm()
      this.dialogVisible = true
    },
    editRole(row) {
      this.dialogTitle = '编辑角色'
      this.isEdit = true
      this.currentId = row.id
      this.roleForm = {
        roleName: row.roleName,
        roleCode: row.roleCode,
        description: row.description || ''
      }
      this.dialogVisible = true
    },
    deleteRole(row) {
      const index = this.tableData.findIndex(item => item.id === row.id)
      if (index > -1) {
        this.tableData.splice(index, 1)
        this.total--
        this.$message.success('删除成功')
      }
    },
    batchDelete() {
      this.$confirm(`确定删除选中的 ${this.selectedRows.length} 个角色吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        const ids = this.selectedRows.map(row => row.id)
        this.tableData = this.tableData.filter(item => !ids.includes(item.id))
        this.total -= ids.length
        this.selectedRows = []
        this.$message.success('批量删除成功')
      }).catch(() => {})
    },
    viewPermissions(row) {
      this.currentPermissions = this.apiPermissions
      this.permissionListVisible = true
    },
    viewUsers(row) {
      this.currentUsers = [
        { username: 'admin', nickname: '管理员', email: 'admin@example.com' },
        { username: 'zhangsan', nickname: '张三', email: 'zhangsan@example.com' }
      ]
      this.userListVisible = true
    },
    assignPermissions(row) {
      this.currentId = row.id
      this.activePermissionTab = 'api'
      this.dataPermission = 'all'
      this.permissionDialogVisible = true
    },
    savePermissions() {
      this.permissionLoading = true
      setTimeout(() => {
        this.permissionLoading = false
        this.permissionDialogVisible = false
        this.$message.success('权限分配成功')
      }, 800)
    },
    getRoleType(code) {
      const typeMap = {
        'ADMIN': 'danger',
        'USER': 'success',
        'AUDITOR': 'warning'
      }
      return typeMap[code] || 'info'
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
      this.roleForm = {
        roleName: '',
        roleCode: '',
        description: ''
      }
      this.$nextTick(() => {
        this.$refs.roleForm && this.$refs.roleForm.clearValidate()
      })
    },
    handleDialogClose() {
      this.resetForm()
    },
    submitForm() {
      this.$refs.roleForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          setTimeout(() => {
            this.submitLoading = false
            if (this.isEdit) {
              const index = this.tableData.findIndex(item => item.id === this.currentId)
              if (index > -1) {
                this.tableData[index] = {
                  ...this.tableData[index],
                  ...this.roleForm
                }
              }
              this.$message.success('修改成功')
            } else {
              const newRole = {
                id: this.tableData.length + 1,
                ...this.roleForm,
                permissionCount: 0,
                userCount: 0,
                createTime: new Date().toLocaleString()
              }
              this.tableData.unshift(newRole)
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
.role-page {
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

      .toggle-advanced-btn {
        color: #409eff;
        display: flex;
        align-items: center;

        i {
          margin-right: 4px;
        }
      }
    }

    .advanced-search {
      margin-top: 12px;
      display: flex;
      flex-wrap: wrap;
      width: 100%;

      .el-form-item {
        margin-right: 20px;
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
        font-weight: 500;
      }

      .add-btn:hover {
        background-color: #409eff30;
      }
    }

    .role-table {
      width: 100%;
      border: none;

      ::v-deep .el-table__body-wrapper {
        border-radius: 6px;
      }

      ::v-deep .el-table--enable-row-hover tr:hover > td {
        background-color: #f5f7fa !important;
      }

      .role-name-cell {
        display: flex;
        align-items: center;

        .role-name {
          margin-left: 10px;
          font-weight: 500;
        }
      }

      .text-btn {
        border: none;
        background: transparent;
        color: #409eff;
        padding: 0 6px;
      }

      .text-btn:hover {
        color: #66b1ff;
        text-decoration: underline;
      }

      .danger {
        color: #f56c6c;
      }

      .danger:hover {
        color: #f78989;
      }
    }

    .pagination-box {
      margin-top: 15px;
      text-align: right;
    }
  }
}

.permission-manage {
  .el-tree {
    max-height: 400px;
    overflow-y: auto;
  }
}

/* 高级搜索下拉动画 */
.slide-fade-enter-active, .slide-fade-leave-active {
  transition: all 0.3s ease;
}
.slide-fade-enter, .slide-fade-leave-to {
  opacity: 0;
  max-height: 0;
  overflow: hidden;
}
</style>
