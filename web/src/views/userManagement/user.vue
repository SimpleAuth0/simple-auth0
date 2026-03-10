<template>
  <div class="user-page">

    <!-- 搜索区 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" size="small" class="search-form">

        <!-- 常规搜索 -->
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable />
        </el-form-item>

        <div class="search-btn-group">
          <el-button type="primary" size="mini" icon="el-icon-search" @click="search">查询</el-button>
          <el-button size="mini" icon="el-icon-refresh" @click="reset">重置</el-button>
          <el-button size="mini" type="text" class="toggle-advanced-btn" @click="toggleAdvanced">
            <i :class="showAdvanced ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" />
            高级搜索
          </el-button>
        </div>

        <!-- 折叠高级搜索 -->
        <transition name="slide-fade">
          <div v-show="showAdvanced" class="advanced-search">
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" clearable placeholder="全部状态" style="width: 160px">
                <el-option label="启用" value="enabled" />
                <el-option label="禁用" value="disabled" />
              </el-select>
            </el-form-item>
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
          @click="addUser"
        >
          新增用户
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
        class="user-table"
        :header-cell-style="tableHeaderStyle"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120">
          <template slot-scope="{ row }">
            <div class="user-info-cell">
              <el-avatar :size="32" :src="row.avatar" class="user-avatar">
                {{ row.username ? row.username.charAt(0).toUpperCase() : 'U' }}
              </el-avatar>
              <span class="username">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" min-width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="{ row }">
            <el-switch
              v-model="row.status"
              active-value="enabled"
              inactive-value="disabled"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" icon="el-icon-view" @click="viewUser(row)">查看</el-button>
            <el-button size="mini" type="text" icon="el-icon-edit" @click="editUser(row)">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-key" @click="resetPassword(row)">重置密码</el-button>
            <el-popconfirm
              title="确定删除该用户吗？"
              @confirm="deleteUser(row)"
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

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="userForm"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
        class="user-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row v-if="!isEdit" :gutter="20">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="userForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="userForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="所属用户组" prop="groupIds">
          <el-select
            v-model="userForm.groupIds"
            multiple
            collapse-tags
            placeholder="请选择用户组"
            style="width: 100%"
          >
            <el-option
              v-for="group in groupOptions"
              :key="group.id"
              :label="group.groupName"
              :value="group.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="所属角色" prop="roleIds">
          <el-select
            v-model="userForm.roleIds"
            multiple
            collapse-tags
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio label="enabled">启用</el-radio>
            <el-radio label="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="userForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 查看用户详情对话框 -->
    <el-dialog
      title="用户详情"
      :visible.sync="detailVisible"
      width="650px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 'enabled' ? 'success' : 'danger'">
            {{ currentUser.status === 'enabled' ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属用户组" :span="2">
          <el-tag v-for="group in currentUser.groups" :key="group" size="small" style="margin-right: 8px">
            {{ group }}
          </el-tag>
          <span v-if="!currentUser.groups || currentUser.groups.length === 0">-</span>
        </el-descriptions-item>
        <el-descriptions-item label="所属角色" :span="2">
          <el-tag v-for="role in currentUser.roles" :key="role" size="small" type="warning" style="margin-right: 8px">
            {{ role }}
          </el-tag>
          <span v-if="!currentUser.roles || currentUser.roles.length === 0">-</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentUser.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentUser.updateTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentUser.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailVisible = false">关 闭</el-button>
        <el-button type="primary" @click="editFromDetail">编 辑</el-button>
      </div>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      title="重置密码"
      :visible.sync="pwdDialogVisible"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="pwdForm"
        :model="pwdForm"
        :rules="pwdRules"
        label-width="100px"
      >
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="pwdForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="pwdForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="pwdDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="submitPwd">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'UserList',
  data() {
    // 密码验证
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        if (this.userForm.confirmPassword !== '') {
          this.$refs.userForm.validateField('confirmPassword')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.userForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    const validatePwd2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.pwdForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      showAdvanced: false,
      searchForm: {
        username: '',
        phone: '',
        email: '',
        status: '',
        createTime: null
      },
      tableData: [
        {
          id: 1,
          username: 'admin',
          nickname: '系统管理员',
          phone: '13800000001',
          email: 'admin@example.com',
          status: 'enabled',
          createTime: '2023-04-10 10:00:00',
          updateTime: '2023-12-01 15:30:00',
          groups: ['管理员组'],
          roles: ['系统管理员'],
          remark: '系统超级管理员'
        },
        {
          id: 2,
          username: 'zhangsan',
          nickname: '张三',
          phone: '13800000002',
          email: 'zhangsan@example.com',
          status: 'enabled',
          createTime: '2023-05-01 09:00:00',
          updateTime: '2023-11-20 14:20:00',
          groups: ['研发组'],
          roles: ['普通用户'],
          remark: ''
        },
        {
          id: 3,
          username: 'lisi',
          nickname: '李四',
          phone: '13800000003',
          email: 'lisi@example.com',
          status: 'disabled',
          createTime: '2023-06-15 11:30:00',
          updateTime: '2023-10-10 09:15:00',
          groups: ['测试组'],
          roles: ['普通用户'],
          remark: '已离职'
        }
      ],
      pageSize: 10,
      currentPage: 1,
      total: 3,
      selectedRows: [],

      // 对话框相关
      dialogVisible: false,
      dialogTitle: '新增用户',
      isEdit: false,
      submitLoading: false,
      currentId: null,

      // 用户表单
      userForm: {
        username: '',
        nickname: '',
        phone: '',
        email: '',
        password: '',
        confirmPassword: '',
        groupIds: [],
        roleIds: [],
        status: 'enabled',
        remark: ''
      },
      userRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        nickname: [
          { max: 20, message: '长度不能超过 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ],
        password: [
          { required: true, validator: validatePass, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validatePass2, trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      },

      // 下拉选项
      groupOptions: [
        { id: 1, groupName: '管理员组' },
        { id: 2, groupName: '研发组' },
        { id: 3, groupName: '测试组' },
        { id: 4, groupName: '运维组' }
      ],
      roleOptions: [
        { id: 1, roleName: '系统管理员' },
        { id: 2, roleName: '普通用户' },
        { id: 3, roleName: '审计员' }
      ],

      // 详情对话框
      detailVisible: false,
      currentUser: {},

      // 密码重置对话框
      pwdDialogVisible: false,
      pwdLoading: false,
      pwdForm: {
        newPassword: '',
        confirmPassword: ''
      },
      pwdRules: {
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validatePwd2, trigger: 'blur' }
        ]
      },
      resetPwdUserId: null
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
      this.searchForm = {
        username: '',
        phone: '',
        email: '',
        status: '',
        createTime: null
      }
      this.search()
    },
    addUser() {
      this.dialogTitle = '新增用户'
      this.isEdit = false
      this.currentId = null
      this.resetForm()
      this.dialogVisible = true
    },
    editUser(row) {
      this.dialogTitle = '编辑用户'
      this.isEdit = true
      this.currentId = row.id
      this.userForm = {
        username: row.username,
        nickname: row.nickname || '',
        phone: row.phone || '',
        email: row.email || '',
        password: '',
        confirmPassword: '',
        groupIds: [1], // 模拟数据
        roleIds: row.roles ? row.roles.map((r, i) => i + 1) : [],
        status: row.status,
        remark: row.remark || ''
      }
      this.dialogVisible = true
    },
    viewUser(row) {
      this.currentUser = { ...row }
      this.detailVisible = true
    },
    editFromDetail() {
      this.detailVisible = false
      this.editUser(this.currentUser)
    },
    deleteUser(row) {
      const index = this.tableData.findIndex(item => item.id === row.id)
      if (index > -1) {
        this.tableData.splice(index, 1)
        this.total--
        this.$message.success('删除成功')
      }
    },
    batchDelete() {
      this.$confirm(`确定删除选中的 ${this.selectedRows.length} 个用户吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        const ids = this.selectedRows.map(row => row.id)
        this.tableData = this.tableData.filter(item => !ids.includes(item.id))
        this.total -= ids.length
        this.selectedRows = []
        this.$message.success('批量删除成功')
      }).catch(() => {})
    },
    handleStatusChange(row, val) {
      const statusText = val === 'enabled' ? '启用' : '禁用'
      this.$message.success(`用户 ${row.username} 已${statusText}`)
    },
    resetPassword(row) {
      this.resetPwdUserId = row.id
      this.pwdForm = { newPassword: '', confirmPassword: '' }
      this.pwdDialogVisible = true
      this.$nextTick(() => {
        this.$refs.pwdForm && this.$refs.pwdForm.clearValidate()
      })
    },
    submitPwd() {
      this.$refs.pwdForm.validate(valid => {
        if (valid) {
          this.pwdLoading = true
          setTimeout(() => {
            this.pwdLoading = false
            this.pwdDialogVisible = false
            this.$message.success('密码重置成功')
          }, 800)
        }
      })
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
      this.userForm = {
        username: '',
        nickname: '',
        phone: '',
        email: '',
        password: '',
        confirmPassword: '',
        groupIds: [],
        roleIds: [],
        status: 'enabled',
        remark: ''
      }
      this.$nextTick(() => {
        this.$refs.userForm && this.$refs.userForm.clearValidate()
      })
    },
    handleDialogClose() {
      this.resetForm()
    },
    submitForm() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          setTimeout(() => {
            this.submitLoading = false
            if (this.isEdit) {
              const index = this.tableData.findIndex(item => item.id === this.currentId)
              if (index > -1) {
                this.tableData[index] = {
                  ...this.tableData[index],
                  ...this.userForm,
                  updateTime: new Date().toLocaleString()
                }
              }
              this.$message.success('修改成功')
            } else {
              const newUser = {
                id: this.tableData.length + 1,
                ...this.userForm,
                createTime: new Date().toLocaleString(),
                groups: this.userForm.groupIds.map(id => {
                  const group = this.groupOptions.find(g => g.id === id)
                  return group ? group.groupName : ''
                }).filter(Boolean),
                roles: this.userForm.roleIds.map(id => {
                  const role = this.roleOptions.find(r => r.id === id)
                  return role ? role.roleName : ''
                }).filter(Boolean)
              }
              this.tableData.unshift(newUser)
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
.user-page {
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

      .toggle-advanced-btn {
        display: flex;
        align-items: center;
        padding: 0;
        font-size: 13px;
        color: #409eff;

        i {
          margin-right: 2px;
          font-size: 12px;
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
        background-color: #409eff20;
        color: #409eff;
        font-weight: 500;
      }

      .add-btn:hover {
        background-color: #409eff30;
      }
    }

    .user-table {
      width: 100%;
      border: none;

      ::v-deep .el-table__body-wrapper {
        border-radius: 6px;
      }

      ::v-deep .el-table--enable-row-hover tr:hover > td {
        background-color: #f5f7fa !important;
      }

      .user-info-cell {
        display: flex;
        align-items: center;

        .user-avatar {
          margin-right: 10px;
          background: #409eff;
          color: #fff;
          font-size: 14px;
        }

        .username {
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

.user-form {
  .el-select {
    width: 100%;
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
